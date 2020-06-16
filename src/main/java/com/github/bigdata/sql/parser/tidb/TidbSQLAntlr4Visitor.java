package com.github.bigdata.sql.parser.tidb;

import com.github.bigdata.sql.antlr4.mysql.MySqlParser;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AlterByAddColumnContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AlterByChangeColumnContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AlterByDropColumnContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AlterByModifyColumnContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AnalyzeTableContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.ColumnConstraintContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.ColumnDeclarationContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.CommentColumnConstraintContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.ConstraintDeclarationContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.DeleteStatementContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.DmlStatementContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.InsertStatementContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.LimitClauseContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.TableNameContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.TableOptionCommentContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.TableOptionContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.UidContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.UpdateStatementContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParserBaseVisitor;
import com.github.bigdata.sql.parser.*;
import com.github.bigdata.sql.parser.util.StringUtil;

import net.itdiandi.java.beans.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
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
//        val (databaseName, tableName) = parseFullId(ctx.tableName().fullId());
        Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
        String comment = null;
        
        for(TableOptionContext it : ctx.tableOption()) {
        	if(it instanceof TableOptionCommentContext) {
        		comment = StringUtil.cleanSingleQuote(((TableOptionCommentContext) it).STRING_LITERAL().getText());
        	}
        }
//        
//        ctx.tableOption().forEach {
//            when(it) {
//                is MySqlParser.TableOptionCommentContext -> {
//                    comment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().text)
//                }
//                else -> null
//            }
//        }
        ArrayList<DcColumn> columns = new ArrayList<DcColumn>();
        ArrayList<TidbColumn> uniques = new ArrayList<TidbColumn>();
        HashMap<String, String> properties = new HashMap<String, String>();

        for(ParseTree pt : ctx.createDefinitions().children) {
        	if(pt instanceof ColumnDeclarationContext ) {
        		ColumnDeclarationContext column = (ColumnDeclarationContext)pt;
                String name = StringUtil.cleanQuote(column.uid().getText());

                String dataType = column.columnDefinition().dataType().getChild(0).getText().toLowerCase();
                int count = column.columnDefinition().dataType().getChildCount();
                if(count > 1) {
                    ParseTree item = column.columnDefinition().dataType().getChild(1);
                    if(item instanceof MySqlParser.LengthOneDimensionContext ||
                            item instanceof MySqlParser.LengthTwoDimensionContext ||
                            item instanceof MySqlParser.LengthTwoOptionalDimensionContext) {
                        dataType = dataType + item.getText();
                    }
                }

                String colComment = null;
                for(ColumnConstraintContext it : column.columnDefinition().columnConstraint()) {
                	if(it instanceof MySqlParser.CommentColumnConstraintContext) {
                        colComment = StringUtil.cleanSingleQuote(((CommentColumnConstraintContext) it).STRING_LITERAL().getText());
                    }
                }
                
                columns.add(new DcColumn(name, dataType, colComment));
            } 
//        	else if (pt instanceof ConstraintDeclarationContext) {
//                (ConstraintDeclarationContext)pt;
//            }
        }
        
//        ctx.createDefinitions().children.forEach { column ->
//        if(column is MySqlParser.ColumnDeclarationContext ) {
//            val name = StringUtil.cleanQuote(column.uid().text)
//
//
//            var dataType = column.columnDefinition().dataType().getChild(0).text.toLowerCase()
//            val count = column.columnDefinition().dataType().childCount
//            if(count > 1) {
//                val item = column.columnDefinition().dataType().getChild(1)
//                if(item instanceof MySqlParser.LengthOneDimensionContext ||
//                        item instanceof MySqlParser.LengthTwoDimensionContext ||
//                        item instanceof MySqlParser.LengthTwoOptionalDimensionContext) {
//                    dataType = dataType + item.getText();
//                }
//            }
//
//            String colComment = null;
//            column.columnDefinition().columnConstraint().forEach {
//                if(it instanceof MySqlParser.CommentColumnConstraintContext) {
//                    colComment = StringUtil.cleanSingleQuote(it.STRING_LITERAL().get)
//                }
//            }
//            columns.add(new DcColumn(name, dataType, colComment));
//        } else if (column instanceof ConstraintDeclarationContext) {
//            column
//        }
//        }


        boolean ifNotExists = false;
        if (ctx.ifNotExists() != null) 
        	ifNotExists = true;

        TidbCreateTable sqlData = new TidbCreateTable(pair.getKey(), pair.getValue(), comment,
                columns, primaries, uniques, properties, ifNotExists);

        super.visitColumnCreateTable(ctx);
        return new StatementData(StatementType.CREATE_TABLE, sqlData);
    }

    @Override 
    public StatementData visitPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        int count = ctx.indexColumnNames().getChildCount();

        for (int i=1;i<=count-2;i++) {
            String column = ctx.indexColumnNames().getChild(i).getText();
            column = StringUtil.cleanQuote(column);
            primaries.add(column);
        }

        return null;
    }

    @Override 
    public StatementData visitDropTable(MySqlParser.DropTableContext ctx) {
        if(ctx.tables().tableName().size() > 1) {
            throw new SQLParserException("不支持drop多个表");
        }
//        val (databaseName, tableName) = parseFullId(ctx.tables().tableName(0).fullId())
        Pair<String,String> pair = parseFullId(ctx.tables().tableName(0).fullId());
        DcTable dcTable = new DcTable(pair.getKey(), pair.getValue());
        dcTable.setIfExists(false);
        if (ctx.ifExists() != null) 
        	dcTable.setIfExists(true);
        return new StatementData(StatementType.DROP_TABLE_TIDB, dcTable);
    }

    @Override 
    public StatementData visitTruncateTable(MySqlParser.TruncateTableContext ctx){
//        val (databaseName, tableName) = parseFullId(ctx.tableName().fullId());
        Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
        DcTable dcTable = new DcTable(pair.getKey(), pair.getValue());
        return new StatementData(StatementType.TRUNCATE_TABLE, dcTable);
    }

    @Override 
    public StatementData visitRenameTable(MySqlParser.RenameTableContext ctx) {
//        val (databaseName, oldTableName) = parseFullId(ctx.renameTableClause().get(0).tableName(0).fullId());
        Pair<String,String> pair = parseFullId(ctx.renameTableClause().get(0).tableName(0).fullId());
//        val (_, newTableName) = parseFullId(ctx.renameTableClause().get(0).tableName(1).fullId());
        Pair<String,String> newPair = parseFullId(ctx.renameTableClause().get(0).tableName(1).fullId());

        DcRenameTable renameTable = new DcRenameTable(pair.getKey(), pair.getValue(), newPair.getValue());
        return new StatementData(StatementType.RENAME_TABLE, renameTable);
    }

    @Override 
    public StatementData visitUseStatement(MySqlParser.UseStatementContext ctx) {
        String databaseName = ctx.uid().getText();
        DcDatabase data = new DcDatabase(databaseName);
        return new StatementData(StatementType.USE, data);
    }

    //-----------------------------------Alter-----------------------------------------------
    @Override 
    public StatementData visitAlterTable(MySqlParser.AlterTableContext ctx) {
        if(ctx.getChildCount() > 4) {
            throw new SQLParserException("不允许同时执行多个alter");
        }
        ParseTree pt = ctx.getChild(3);
        if(pt instanceof AlterByChangeColumnContext) {
        	AlterByChangeColumnContext statement = (AlterByChangeColumnContext)pt;
//            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());

            String oldColumn = StringUtil.cleanQuote(statement.oldColumn.getText());
            String newColumn = StringUtil.cleanQuote(statement.newColumn.getText());
            String dataType = statement.columnDefinition().dataType().getText();
            String comment = null;
            
            for(ParseTree it : statement.columnDefinition().children) {
            	if(it instanceof MySqlParser.CommentColumnConstraintContext) {
                    comment = StringUtil.cleanSingleQuote(((MySqlParser.CommentColumnConstraintContext) it).STRING_LITERAL().getText());
                }
            }
            
            DcColumn column = new DcColumn(newColumn, dataType, comment, oldColumn);
            tableSource.setColumn(column);

            return new StatementData(StatementType.ALTER_TABLE_CHANGE_COL, tableSource);
        } else if(pt instanceof AlterByAddColumnContext) {
        	AlterByAddColumnContext statement = (AlterByAddColumnContext)pt;
//            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());

            String name = StringUtil.cleanQuote(statement.uid().get(0).getText());
            String dataType = statement.columnDefinition().dataType().getText();
            String comment = null;
            
            for(ParseTree it : statement.columnDefinition().children) {
                if(it instanceof MySqlParser.CommentColumnConstraintContext) {
                    comment = StringUtil.cleanSingleQuote(((CommentColumnConstraintContext) it).STRING_LITERAL().getText());
                }
            }

            DcColumn column = new DcColumn(name, dataType, comment);
            tableSource.setColumn(column);

            return new StatementData(StatementType.ALTER_TABLE_ADD_COL, tableSource);
        } else if(pt instanceof AlterByDropColumnContext) {
        	AlterByDropColumnContext statement = (AlterByDropColumnContext)pt;
//            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
            Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());

            String name = StringUtil.cleanQuote(statement.uid().getText());
            DcColumn column = new DcColumn(name);
            tableSource.setColumn(column);

            return new StatementData(StatementType.ALTER_TABLE_DROP_COL, tableSource);
        } else if(pt instanceof AlterByModifyColumnContext) {
//            val (databaseName, tableName) = parseFullId(ctx.tableName().fullId())
        	AlterByModifyColumnContext statement = (AlterByModifyColumnContext)pt;
            Pair<String,String> pair = parseFullId(ctx.tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());

            String name = StringUtil.cleanQuote(statement.uid().get(0).getText());
            String dataType = statement.columnDefinition().dataType().getText();
            DcColumn column = new DcColumn(name, dataType);
            tableSource.setColumn(column);

            return new StatementData(StatementType.ALTER_TABLE_MODIFY_COL, tableSource);
        }

        return super.visitAlterTable(ctx);
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
    public StatementData visitAnalyzeTable(AnalyzeTableContext ctx){
    	ArrayList<TableSource> tables = new ArrayList<TableSource>();
    	
    	for(TableNameContext context : ctx.tables().tableName()) {
    		 Pair<String,String> pair = parseFullId(context.fullId());
             tables.add(new TableSource(pair.getKey(), pair.getValue()));
    	}

        return new StatementData(StatementType.ANALYZE_TABLE, new TableData(tables));
    }

    //-----------------------------------DML-------------------------------------------------

    @Override 
    public StatementData visitDmlStatement(DmlStatementContext ctx) {
        if(ctx.selectStatement() != null) {
            currentOptType = StatementType.SELECT;
            super.visitDmlStatement(ctx);

            statementData.setLimit(limit);
            return new StatementData(StatementType.SELECT, statementData);
        } else if (ctx.insertStatement() != null) {
        	InsertStatementContext statement = ctx.insertStatement();
//            val (databaseName, tableName) = parseFullId(statement.tableName().fullId());
            Pair<String,String> pair = parseFullId(statement.tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());
            statementData.getOutpuTables().add(tableSource);

            if(statement.insertStatementValue().selectStatement() != null) {
                currentOptType = StatementType.INSERT_SELECT;
                super.visit(ctx.insertStatement().insertStatementValue().selectStatement());
                return new StatementData(StatementType.INSERT_SELECT, statementData);
            } else {
                currentOptType = StatementType.INSERT_VALUES;
                return new StatementData(StatementType.INSERT_VALUES, statementData);
            }
        } else if (ctx.updateStatement() != null) {
        	UpdateStatementContext statement = ctx.updateStatement();
            if(statement.multipleUpdateStatement() != null) {
                throw new SQLParserException("不支持更新多个表");
            }

            Pair<String,String> pair = parseFullId(ctx.updateStatement().singleUpdateStatement().tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());
            statementData.getOutpuTables().add(tableSource);

            return new StatementData(StatementType.UPDATE, statementData);
        } else if (ctx.deleteStatement() != null) {
        	DeleteStatementContext statement = ctx.deleteStatement();
            if(statement.multipleDeleteStatement() != null) {
                throw new SQLParserException("不支持删除多个表");
            }

            Pair<String,String> pair =  parseFullId(ctx.deleteStatement().singleDeleteStatement().tableName().fullId());
            TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());
            statementData.getOutpuTables().add(tableSource);

            return new StatementData(StatementType.DELETE, statementData);
        } else {
            throw new SQLParserException("不支持的DML");
        }
    }

    //-----------------------------------private method-------------------------------------------------

    @Override 
    public StatementData visitTableName(TableNameContext ctx) {
        if(StatementType.SELECT == currentOptType ||
                StatementType.INSERT_SELECT == currentOptType ||
                currentOptType == StatementType.ALTER_TABLE_RENAME) {
        	Pair<String,String> pair = parseFullId(ctx.fullId());
//            val (databaseName, tableName) = parseFullId(ctx.fullId());
        	TableSource tableSource = new TableSource(pair.getKey(), pair.getValue());
            statementData.getInputTables().add(tableSource);
        } else if(StatementType.ALTER_TABLE_ADD_INDEX == currentOptType ||
                StatementType.ALTER_TABLE_DROP_INDEX == currentOptType ||
                StatementType.ALTER_TABLE_ADD_UNIQUE_KEY == currentOptType ||
                StatementType.ALTER_TABLE_ADD_PRIMARY_KEY == currentOptType) {

        	Pair<String,String> pair =  parseFullId(ctx.fullId());
            tableSource = new TableSource(pair.getKey(), pair.getValue());
        }
        return null;
    }

    @Override
	public StatementData visitLimitClause(LimitClauseContext ctx) {
        if(currentOptType == StatementType.SELECT ) {
        	TableData _sqlData = statementData;
            if(_sqlData instanceof TableData) {
                limit = Integer.parseInt(ctx.limit.getText());
            }
        }
        return null;
    }

    private Pair<String, String> parseFullId(MySqlParser.FullIdContext fullId)  {
    	String databaseName = null;
        String tableName = "";

        if(fullId.getChildCount() == 2) {
            databaseName = fullId.uid().get(0).getText();
            tableName = ((TerminalNodeImpl)fullId.getChild(1)).getText().substring(1);
        } else if(fullId.getChildCount() == 3) {
            databaseName = StringUtil.cleanQuote(fullId.uid().get(0).getText());
            tableName = StringUtil.cleanQuote(((UidContext)fullId.getChild(2)).getText());
        } else {
            tableName = fullId.uid().get(0).getText();
        }

        if(databaseName != null) {
            databaseName = StringUtil.cleanQuote(databaseName);
        }
        if(tableName != null) {
            tableName = StringUtil.cleanQuote(tableName);
        }

        return new Pair<String,String>(databaseName, tableName);
    }
}