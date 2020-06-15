package com.github.bigdata.sql.parser.tidb;

import com.github.bigdata.sql.antlr4.mysql.MySqlParser;
import com.github.bigdata.sql.antlr4.mysql.MySqlParserBaseVisitor;
import com.github.bigdata.sql.parser.*;
import com.github.bigdata.sql.parser.util.StringUtil;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class TidbSQLAntlr4Visitor extends MySqlParserBaseVisitor<StatementData>{
	private StatementType currentOptType = StatementType.UNKOWN;
    private TableData statementData = new TableData();
    private TableSource tableSource = null;
    private Integer limit = null;
    private  ArrayList<String> primaries = new ArrayList<String>();

    //-----------------------------------database-------------------------------------------------
    @Override 
    public StatementData visitCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        String databaseName = ctx.uid().getText();
        DcDatabase sqlData = new DcDatabase(databaseName);
        return new StatementData(StatementType.CREATE_DATABASE, sqlData);
    }

    @Override 
    public StatementData visitDropDatabase(MySqlParser.DropDatabaseContext ctx) {
        String databaseName = ctx.uid().getText();
        DcDatabase sqlData = new DcDatabase(databaseName);

        return new StatementData(StatementType.DROP_DATABASE, sqlData);
    }

    //-----------------------------------table-------------------------------------------------
    @Override 
    public StatementData visitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        val (databaseName, tableName) = parseFullId(ctx.tableName().fullId());
        var comment: String? = null
        ctx.tableOption().forEach {
            when(it) {
                is MySqlParser.TableOptionCommentContext -> {
                    comment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().text)
                }
                else -> null
            }
        }
        val columns = ArrayList<DcColumn>()
        val uniques = ArrayList<TidbColumn>()
        val properties = HashMap<String, String>()

        ctx.createDefinitions().children.forEach { column ->
            if(column is MySqlParser.ColumnDeclarationContext ) {
                val name = StringUtil.cleanQuote(column.uid().text)


                var dataType = column.columnDefinition().dataType().getChild(0).text.toLowerCase()
                val count = column.columnDefinition().dataType().childCount
                if(count > 1) {
                    val item = column.columnDefinition().dataType().getChild(1)
                    if(item is MySqlParser.LengthOneDimensionContext ||
                            item is MySqlParser.LengthTwoDimensionContext ||
                            item is MySqlParser.LengthTwoOptionalDimensionContext) {
                        dataType = dataType + item.text
                    }
                }

                var colComment:String? = null
                column.columnDefinition().columnConstraint().forEach {
                    if(it is MySqlParser.CommentColumnConstraintContext) {
                        colComment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().text)
                    }
                }
                columns.add(DcColumn(name, dataType, colComment))
            } else if (column is MySqlParser.ConstraintDeclarationContext) {
                column
            }
        }


        val ifNotExists: Boolean = if (ctx.ifNotExists() != null) true else false

        val sqlData = TidbCreateTable(databaseName, tableName, comment,
                columns, primaries, uniques, properties, ifNotExists)

        super.visitColumnCreateTable(ctx)
        return StatementData(StatementType.CREATE_TABLE, sqlData)
    }

    @Override 
    public StatementData visitPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        val count = ctx.indexColumnNames().childCount

        for (i in 1..(count-2)) {
            var column = ctx.indexColumnNames().getChild(i).text
            column = StringUtil.cleanQuote(column)
            primaries.add(column)
        }

        return null
    }

    @Override 
    public StatementData visitDropTable(MySqlParser.DropTableContext ctx) {
        if(ctx.tables().tableName().size > 1) {
            throw SQLParserException("不支持drop多个表")
        }
        val (databaseName, tableName) = parseFullId(ctx.tables().tableName(0).fullId())

        val dcTable = DcTable(databaseName, tableName)
        dcTable.ifExists = if (ctx.ifExists() != null) true else false
        return StatementData(StatementType.DROP_TABLE_TIDB, dcTable)
    }

    @Override 
    public StatementData visitTruncateTable(MySqlParser.TruncateTableContext ctx){
        val (databaseName, tableName) = parseFullId(ctx.tableName().fullId());

        val dcTable = DcTable(databaseName, tableName)
        return StatementData(StatementType.TRUNCATE_TABLE, dcTable)
    }

    @Override 
    public StatementData visitRenameTable(MySqlParser.RenameTableContext ctx) {
        val (databaseName, oldTableName) = parseFullId(ctx.renameTableClause().get(0).tableName(0).fullId());
        val (_, newTableName) = parseFullId(ctx.renameTableClause().get(0).tableName(1).fullId());

        val renameTable = DcRenameTable(databaseName, oldTableName, newTableName);
        return StatementData(StatementType.RENAME_TABLE, renameTable);
    }

    @Override 
    public StatementData visitUseStatement(MySqlParser.UseStatementContext ctx) {
        val databaseName = ctx.uid().text
        val data = DcDatabase(databaseName)
        return StatementData(StatementType.USE, data)
    }

    //-----------------------------------Alter-----------------------------------------------
    @Override 
    public StatementData visitAlterTable(MySqlParser.AlterTableContext ctx) {
        if(ctx.childCount > 4) {
            throw SQLParserException("不允许同时执行多个alter");
        }
        val statement = ctx.getChild(3);
        if(statement is MySqlParser.AlterByChangeColumnContext) {
            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)

            val oldColumn = StringUtil.cleanQuote(statement.oldColumn.text)
            val newColumn = StringUtil.cleanQuote(statement.newColumn.text)
            val dataType = statement.columnDefinition().dataType().text
            var comment:String? = null

            statement.columnDefinition().children.forEach {
                if(it is MySqlParser.CommentColumnConstraintContext) {
                    comment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().text)
                }
            }

            val column = DcColumn(newColumn, dataType, comment, oldColumn)
            tableSource.column = column

            return StatementData(StatementType.ALTER_TABLE_CHANGE_COL, tableSource)
        } else if(statement is MySqlParser.AlterByAddColumnContext) {
            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)

            val name = StringUtil.cleanQuote(statement.uid().get(0).text)
            val dataType = statement.columnDefinition().dataType().text
            var comment:String? = null
            statement.columnDefinition().children.forEach {
                if(it is MySqlParser.CommentColumnConstraintContext) {
                    comment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().text)
                }
            }

            val column = DcColumn(name, dataType, comment)
            tableSource.column = column

            return StatementData(StatementType.ALTER_TABLE_ADD_COL, tableSource)
        } else if(statement is MySqlParser.AlterByDropColumnContext) {
            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)

            val name = StringUtil.cleanQuote(statement.uid().text)
            val column = DcColumn(name)
            tableSource.column = column

            return StatementData(StatementType.ALTER_TABLE_DROP_COL, tableSource)
        } else if(statement is MySqlParser.AlterByModifyColumnContext) {
            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)

            val name = StringUtil.cleanQuote(statement.uid().get(0).text)
            val dataType = statement.columnDefinition().dataType().text
            val column = DcColumn(name, dataType)
            tableSource.column = column

            return StatementData(StatementType.ALTER_TABLE_MODIFY_COL, tableSource)
        }

        return super.visitAlterTable(ctx)
    }

    @Override 
    public StatementData visitAlterByAddIndex(MySqlParser.AlterByAddIndexContext ctx) {
        currentOptType = StatementType.ALTER_TABLE_ADD_INDEX;
        super.visitAlterByAddIndex(ctx);
        return new StatementData(currentOptType, tableSource);
    }

    @Override 
    public StatementData visitAlterByDropIndex(MySqlParser.AlterByDropIndexContext ctx) {
        currentOptType = StatementType.ALTER_TABLE_DROP_INDEX;
        super.visitAlterByDropIndex(ctx);
        return new StatementData(currentOptType, tableSource);
    }

    @Override 
    public StatementData visitAlterByAddUniqueKey(MySqlParser.AlterByAddUniqueKeyContext ctx) {
        currentOptType = StatementType.ALTER_TABLE_ADD_UNIQUE_KEY;
        super.visitAlterByAddUniqueKey(ctx);
        return new StatementData(currentOptType, tableSource);
    }

    @Override 
    public StatementData visitAnalyzeTable(MySqlParser.AnalyzeTableContext ctx){
    	ArrayList<TableSource> tables = new ArrayList<TableSource>();
        ctx.tables().tableName().forEach(context -> {
            val (databaseName, tableName) = parseFullId(context.fullId());
            tables.add(TableSource(databaseName, tableName));
        })

        return StatementData(StatementType.ANALYZE_TABLE, TableData(tables))
    }

    //-----------------------------------DML-------------------------------------------------

    override fun visitDmlStatement(ctx: MySqlParser.DmlStatementContext): StatementData {
        if(ctx.selectStatement() != null) {
            currentOptType = StatementType.SELECT
            super.visitDmlStatement(ctx)

            statementData.limit = limit
            return return StatementData(StatementType.SELECT, statementData)
        } else if (ctx.insertStatement() != null) {
            val statement = ctx.insertStatement()
            val (databaseName, tableName) = parseFullId(statement.tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)
            statementData.outpuTables.add(tableSource)

            if(statement.insertStatementValue().selectStatement() != null) {
                currentOptType = StatementType.INSERT_SELECT
                super.visit(ctx.insertStatement().insertStatementValue().selectStatement())
                return return StatementData(StatementType.INSERT_SELECT, statementData)
            } else {
                currentOptType = StatementType.INSERT_VALUES
                return return StatementData(StatementType.INSERT_VALUES, statementData)
            }
        } else if (ctx.updateStatement() != null) {
            val statement = ctx.updateStatement()
            if(statement.multipleUpdateStatement() != null) {
                throw SQLParserException("不支持更新多个表")
            }

            val (databaseName, tableName) =
                    parseFullId(ctx.updateStatement().singleUpdateStatement().tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)
            statementData.outpuTables.add(tableSource)

            return return StatementData(StatementType.UPDATE, statementData)
        } else if (ctx.deleteStatement() != null) {
            val statement = ctx.deleteStatement()
            if(statement.multipleDeleteStatement() != null) {
                throw SQLParserException("不支持删除多个表")
            }

            val (databaseName, tableName) =
                    parseFullId(ctx.deleteStatement().singleDeleteStatement().tableName().fullId())
            var tableSource = TableSource(databaseName, tableName)
            statementData.outpuTables.add(tableSource)

            return return StatementData(StatementType.DELETE, statementData)
        } else {
            throw SQLParserException("不支持的DML")
        }
    }

    //-----------------------------------private method-------------------------------------------------

    override fun visitTableName(ctx: MySqlParser.TableNameContext): StatementData? {
        if(StatementType.SELECT == currentOptType ||
                StatementType.INSERT_SELECT == currentOptType ||
                currentOptType == StatementType.ALTER_TABLE_RENAME) {
            val (databaseName, tableName) = parseFullId(ctx.fullId())
            var tableSource = TableSource(databaseName, tableName)
            statementData.inputTables.add(tableSource)
        } else if(StatementType.ALTER_TABLE_ADD_INDEX == currentOptType ||
                StatementType.ALTER_TABLE_DROP_INDEX == currentOptType ||
                StatementType.ALTER_TABLE_ADD_UNIQUE_KEY == currentOptType ||
                StatementType.ALTER_TABLE_ADD_PRIMARY_KEY == currentOptType) {

            val (databaseName, tableName) = parseFullId(ctx.fullId())
            tableSource = TableSource(databaseName, tableName)
        }
        return null
    }

    override fun visitLimitClause(ctx: MySqlParser.LimitClauseContext): StatementData? {
        if(currentOptType == StatementType.SELECT ) {
            var _sqlData = statementData
            if(_sqlData is TableData) {
                limit = ctx.limit.text.toInt()
            }
        }
        return null
    }

    private fun parseFullId(fullId: MySqlParser.FullIdContext): Pair<String?, String> {
        var databaseName:String? = null
        var tableName = ""

        if(fullId.childCount == 2) {
            databaseName = fullId.uid().get(0).text
            tableName = (fullId.getChild(1) as TerminalNodeImpl).text.substring(1)
        } else if(fullId.childCount == 3) {
            databaseName = StringUtil.cleanQuote(fullId.uid().get(0).text)
            tableName = StringUtil.cleanQuote((fullId.getChild(2) as MySqlParser.UidContext).text)
        } else {
            tableName = fullId.uid().get(0).text
        }

        if(databaseName != null) {
            databaseName = StringUtil.cleanQuote(databaseName)
        }
        if(tableName != null) {
            tableName = StringUtil.cleanQuote(tableName)
        }

        return Pair(databaseName, tableName);
    }
}

/**

   
}
**/