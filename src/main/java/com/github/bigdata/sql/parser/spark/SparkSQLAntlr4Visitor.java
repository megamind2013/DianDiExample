package com.github.bigdata.sql.parser.spark;

import com.github.bigdata.sql.antlr4.spark.SparkSqlBaseBaseVisitor;
import com.github.bigdata.sql.antlr4.spark.SparkSqlBaseParser;
import com.github.bigdata.sql.parser.*;
import com.github.bigdata.sql.parser.util.StringUtil;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;
import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkSQLAntlr4Visitor extends SparkSqlBaseBaseVisitor<StatementData>{
    private StatementType currentOptType = StatementType.UNKOWN;
    private TableData statementData = new TableData();
    private String multiInsertToken = null;
    private Integer limit = null;
    private String command = null;


    @Override
    public StatementData visit(ParseTree tree) {
        StatementData data = super.visit(tree);

        if (data == null) {
            throw new SQLParserException("不支持的SQL");
        }

        return data;
    }

    //-----------------------------------database-------------------------------------------------
    @Override
    public StatementData visitCreateDatabase(SparkSqlBaseParser.CreateDatabaseContext ctx) {
        String databaseName = ctx.identifier().getText();
        DcDatabase sqlData = new DcDatabase(databaseName);
        return new StatementData(StatementType.CREATE_DATABASE, sqlData);
    }

    @Override
    public StatementData visitDropDatabase(SparkSqlBaseParser.DropDatabaseContext ctx) {
        String databaseName = ctx.identifier().getText();
        DcDatabase sqlData = new DcDatabase(databaseName);
        return new StatementData(StatementType.DROP_DATABASE, sqlData);
    }

    @Override
    public StatementData visitDescribeDatabase(SparkSqlBaseParser.DescribeDatabaseContext ctx) {
        String databaseName = ctx.identifier().getText();
        DcDatabase sqlData = new DcDatabase(databaseName);
        return new StatementData(StatementType.DESC_DATABASE, sqlData);
    }

    @Override
    public StatementData visitShowTables(SparkSqlBaseParser.ShowTablesContext ctx) {
        return new StatementData(StatementType.SHOW_TABLES);
    }

    //-----------------------------------table-------------------------------------------------

    @Override
    public StatementData visitCreateHiveTable(SparkSqlBaseParser.CreateHiveTableContext ctx) {
        String databaseName = ctx.createTableHeader().tableIdentifier().db.getText();
        String tableName = ctx.createTableHeader().tableIdentifier().table.getText();
        String comment = null;
        if(ctx.comment != null)
            StringUtil.cleanSingleQuote(ctx.comment.getText());
        int lifeCycle = Integer.parseInt(ctx.lifecycle.getText());

        List<DcColumn> partitionColumns = null;
        List<DcColumn> columns = new ArrayList();
        if(ctx.query() == null) {
            if(ctx.partitionColumns != null) {

                for(ParseTree it : ctx.partitionColumns.children){
                    if(it instanceof SparkSqlBaseParser.ColTypeContext){
                        SparkSqlBaseParser.ColTypeContext column = (SparkSqlBaseParser.ColTypeContext)it;
                        String colName = column.identifier().getText();
                        String dataType = column.dataType().getText();
                        checkPartitionDataType(dataType);

                        String colComment = null;
                        if (column.STRING() != null)
                            StringUtil.cleanSingleQuote(column.STRING().getText());
                        columns.add(new DcColumn(colName, dataType, colComment));

                    }
                }
            }

            if(ctx.columns != null) {

                for(ParseTree it : ctx.columns.children){
                    if(it instanceof SparkSqlBaseParser.ColTypeContext){
                        SparkSqlBaseParser.ColTypeContext column = (SparkSqlBaseParser.ColTypeContext)it;
                        String colName = column.identifier().getText();
                        String dataType = column.dataType().getText();

                        String colComment = null;
                        if (column.STRING() != null)
                            StringUtil.cleanSingleQuote(column.STRING().getText());
                        columns.add(new DcColumn(colName, dataType, colComment));

                    }
                }
            }
        }

        Map properties = new HashMap<String, String>();
        if(ctx.tableProps != null) {

            for(ParseTree it : ctx.tableProps.children){
                if(it instanceof SparkSqlBaseParser.TablePropertyContext){
                    SparkSqlBaseParser.TablePropertyContext property = (SparkSqlBaseParser.TablePropertyContext)it;
                    String key = StringUtil.cleanSingleQuote(property.key.getText());
                    String value = StringUtil.cleanSingleQuote(property.value.getText());
                    properties.put(key, value);
                }
            }
        }

        String fileFormate = "PARQUET" ;//TODO
        DcTable dcTable = new DcTable(databaseName, tableName, comment, lifeCycle, partitionColumns, columns, properties, fileFormate);
        if (ctx.createTableHeader().NOT() != null)
            dcTable.setIfNotExists(true);

        if (ctx.createTableHeader().EXTERNAL() != null)
            dcTable.setExternal(true);

        if (ctx.createTableHeader().TEMPORARY() != null)
            dcTable.setTemporary(true);

        if (ctx.locationSpec().size() > 0)
            dcTable.setLocation(true);

        if(ctx.query() != null) {
            currentOptType = StatementType.CREATE_TABLE_AS_SELECT;
            String querySql = StringUtils.substring(command, ctx.query().start.getStartIndex());
            dcTable.setQuerySql(querySql);
            super.visitQuery(ctx.query());
            dcTable.setTableData(statementData);
            return new StatementData(StatementType.CREATE_TABLE_AS_SELECT, dcTable);
        } else {
            return new StatementData(StatementType.CREATE_TABLE, dcTable);
        }
    }

    @Override
    public StatementData visitCreateTableLike(SparkSqlBaseParser.CreateTableLikeContext ctx) {
        String newDatabaseName = ctx.target.db.getText();
        String newTableName = ctx.target.table.getText();

        String oldDatabaseName = ctx.source.db.getText();
        String oldTableName = ctx.source.table.getText();

        DcCopyTable dcTable = new DcCopyTable(oldDatabaseName, oldTableName, newDatabaseName, newTableName);
        if (ctx.NOT() != null){
            dcTable.setIfNotExists(true);
        }

        return new StatementData(StatementType.CREATE_TABLE_AS_LIKE, dcTable);
    }

    @Override
    public StatementData visitDropTable(SparkSqlBaseParser.DropTableContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        if(ctx.VIEW() != null) {
            DcView dcView = new DcView(databaseName, tableName);
            if (ctx.EXISTS() != null)
                dcView.setIfExists(true);
            return new StatementData(StatementType.DROP_VIEW, dcView);
        } else {
            DcTable dcTable = new DcTable(databaseName, tableName);
            if (ctx.EXISTS() != null)
                dcTable.setIfExists(true);
            return new StatementData(StatementType.DROP_TABLE, dcTable);
        }
    }
    @Override
    public StatementData visitTruncateTable(SparkSqlBaseParser.TruncateTableContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();
        DcTable dcTable = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.TRUNCATE_TABLE, dcTable);
    }
    @Override
    public StatementData visitRepairTable(SparkSqlBaseParser.RepairTableContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();
        DcTable dcTable = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.MSCK_TABLE, dcTable);
    }
    @Override
    public StatementData visitRenameTable(SparkSqlBaseParser.RenameTableContext ctx) {
        String databaseName = ctx.from.db.getText();
        String oldname = ctx.from.table.getText();
        String newname = ctx.to.table.getText();

        if(ctx.VIEW() != null) {
            DcRenameView dcView = new DcRenameView(databaseName, oldname, newname);
            return new StatementData(StatementType.ALTER_VIEW_RENAME, dcView);
        } else {
            DcRenameTable dcTable = new DcRenameTable(databaseName, oldname, newname);
            return new StatementData(StatementType.ALTER_TABLE_RENAME, dcTable);
        }
    }
    @Override
    public StatementData visitSetTableProperties(SparkSqlBaseParser.SetTablePropertiesContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        Map<String,String> properties = new HashMap<String, String>();
        if(ctx.tablePropertyList() != null) {
            for(ParseTree it : ctx.tablePropertyList().children){
                if(it instanceof SparkSqlBaseParser.TablePropertyContext){
                    SparkSqlBaseParser.TablePropertyContext property = (SparkSqlBaseParser.TablePropertyContext)it;
                    String key = StringUtil.cleanSingleQuote(property.key.getText());
                    String value = StringUtil.cleanSingleQuote(property.value.getText());
                    properties.put(key, value);
                }
            }
        }

        DcTable data = new DcTable(databaseName, tableName, null, null, null, null, properties, null);
        if(ctx.VIEW() == null) {
            return new StatementData(StatementType.ALTER_TABLE_PROPERTIES, data);
        } else {
            return new StatementData(StatementType.ALTER_VIEW_PROPERTIES, data);
        }
    }

    @Override
    public StatementData visitAddTableColumns(SparkSqlBaseParser.AddTableColumnsContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        List<DcColumn> columns = new ArrayList<>();

        for(ParseTree it : ctx.columns.children){
            if(it instanceof SparkSqlBaseParser.ColTypeContext){
                SparkSqlBaseParser.ColTypeContext column = (SparkSqlBaseParser.ColTypeContext)it;
                String colName = column.identifier().getText();
                String dataType = column.dataType().getText();
                String colComment = null;
                if(column.STRING() != null)
                    colComment = StringUtil.cleanSingleQuote(column.STRING().getText());
                columns.add(new DcColumn(colName, dataType, colComment));
            }
        }

        DcTable data = new DcTable(databaseName, tableName, null, null, null, columns);
        return new StatementData(StatementType.ALTER_TABLE_ADD_COLS, data);
    }

    @Override
    public StatementData visitChangeColumn(SparkSqlBaseParser.ChangeColumnContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        String oldName = ctx.identifier().getText();
        String newName = ctx.colType().identifier().getText();
        TerminalNode commentNode = ctx.colType().STRING();
        String comment = null;
        if(commentNode != null)
            StringUtil.cleanSingleQuote(commentNode.getText());

        DcAlterColumn data = new DcAlterColumn(databaseName, tableName, oldName, newName, comment);
        return new StatementData(StatementType.ALTER_TABLE_RENAME_COL, data);
    }

    @Override
    public StatementData visitSetTableLocation(SparkSqlBaseParser.SetTableLocationContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        TableSource tableSource = new TableSource(databaseName, tableName);
        return new StatementData(StatementType.ALTER_TABLE_SET_LOCATION, tableSource);
    }

    @Override
    public StatementData visitMergeTable(SparkSqlBaseParser.MergeTableContext ctx) {
        List<String> partitionVals = new ArrayList<>();
        for(SparkSqlBaseParser.PartitionValContext partitionValContext : ctx.partitionSpec().partitionVal()){
            partitionVals.add(partitionValContext.getText());
        }
        MergeData data = new MergeData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText(), partitionVals);
        return new StatementData(StatementType.MERGE_TABLE, data);
    }

    @Override
    public StatementData visitKillJob(SparkSqlBaseParser.KillJobContext ctx) {
        KillData data = new KillData(ctx.jobIdentifier().getText());
        return new StatementData(StatementType.KILL, data);
    }

    @Override
    public StatementData visitReadTable(SparkSqlBaseParser.ReadTableContext ctx) {
        List<String> partitionVals = new ArrayList<>();
        for(SparkSqlBaseParser.PartitionValContext partitionValContext : ctx.partitionSpec().partitionVal()){
            partitionVals.add(partitionValContext.getText());
        }
        ReadData data = new ReadData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText(),
                partitionVals, Integer.parseInt(ctx.number().getText()));

        return new StatementData(StatementType.READ_TABLE, data);
    }

    @Override
    public StatementData visitLoadData(SparkSqlBaseParser.LoadDataContext ctx) {
        List<String> partitionVals = new ArrayList<>();
        for(SparkSqlBaseParser.PartitionValContext partitionValContext : ctx.partitionSpec().partitionVal()){
            partitionVals.add(partitionValContext.getText());
        }

        String resName = ctx.resName.getText().substring(1, ctx.resName.getText().length()-1);
        LoadData data = new LoadData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText(),
                ctx.loadMode.getText(), resName, partitionVals);

        return new StatementData(StatementType.LOAD_TABLE, data);
    }

    @Override
    public StatementData visitRefreshTable(SparkSqlBaseParser.RefreshTableContext ctx) {
        RefreshData data = new RefreshData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText());

        return new StatementData(StatementType.REFRESH_TABLE, data);
    }

    @Override
    public StatementData visitDescribeTable(SparkSqlBaseParser.DescribeTableContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.DESC_TABLE, data);
    }

    @Override
    public StatementData visitShowColumns(SparkSqlBaseParser.ShowColumnsContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.SHOW_COLUMNS, data);
    }

    @Override
    public StatementData visitShowCreateTable(SparkSqlBaseParser.ShowCreateTableContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.SHOW_CREATE_TABLE, data);
    }

    @Override
    public StatementData visitShowTable(SparkSqlBaseParser.ShowTableContext ctx) {
        return new StatementData(StatementType.SHOW_TABLE);
    }

    @Override
    public StatementData visitShowTblProperties(SparkSqlBaseParser.ShowTblPropertiesContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.SHOW_TABLE_PROPERTIES, data);
    }

    @Override
    public StatementData visitAnalyze(SparkSqlBaseParser.AnalyzeContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.ANALYZE_TABLE, data);
    }

    //-----------------------------------partition-------------------------------------------------

    @Override
    public StatementData visitAddTablePartition(SparkSqlBaseParser.AddTablePartitionContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.ALTER_TABLE_ADD_PARTS, data);
    }

    @Override
    public StatementData visitDropTablePartitions(SparkSqlBaseParser.DropTablePartitionsContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.ALTER_TABLE_DROP_PARTS, data);
    }

    @Override
    public StatementData visitRenameTablePartition(SparkSqlBaseParser.RenameTablePartitionContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.ALTER_TABLE_RENAME_PART, data);
    }

    @Override
    public StatementData visitShowPartitions(SparkSqlBaseParser.ShowPartitionsContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcTable data = new DcTable(databaseName, tableName);
        return new StatementData(StatementType.SHOW_PARTITIONS, data);
    }

    //-----------------------------------view-------------------------------------------------

    @Override
    public StatementData visitCreateView(SparkSqlBaseParser.CreateViewContext ctx) {
        String comment = null;
        if(ctx.COMMENT() != null) {
            if(ctx.NOT() == null) {
                comment = StringUtil.cleanSingleQuote(ctx.getChild(4).getText());
            } else {
                comment = StringUtil.cleanSingleQuote(ctx.getChild(7).getText());
            }
        }

        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();
        boolean ifNotExists = false;
        if (ctx.NOT() != null)
            ifNotExists = true;

        DcView dcView = new DcView(databaseName, tableName, comment, ifNotExists);
        return new StatementData(StatementType.CREATE_VIEW, dcView);
    }

    @Override
    public StatementData visitAlterViewQuery(SparkSqlBaseParser.AlterViewQueryContext ctx) {
        String databaseName = ctx.tableIdentifier().db.getText();
        String tableName = ctx.tableIdentifier().table.getText();

        DcView dcView = new DcView(databaseName, tableName);
        return new StatementData(StatementType.ALTER_VIEW_QUERY, dcView);
    }

    //-----------------------------------function-------------------------------------------------

    @Override
    public StatementData visitCreateFunction(SparkSqlBaseParser.CreateFunctionContext ctx) {
        String name = ctx.qualifiedName().getText();
        String classNmae = ctx.className.getText();
        String file = ctx.resource(0).STRING().getText();

        DcFunction data = new DcFunction(name, classNmae, file);
        return new StatementData(StatementType.CREATE_FUNCTION, data);
    }

    @Override
    public StatementData visitDropFunction(SparkSqlBaseParser.DropFunctionContext ctx) {
        String name = ctx.qualifiedName().getText();
        DcFunction data = new DcFunction(name);
        return new StatementData(StatementType.DROP_FUNCTION, data);
    }

    @Override
    public StatementData visitDescribeFunction(SparkSqlBaseParser.DescribeFunctionContext ctx) {
        String name = ctx.describeFuncName().getText();
        DcFunction data = new DcFunction(name);
        return new StatementData(StatementType.DESC_TABLE, data);
    }

    @Override
    public StatementData visitShowFunctions(SparkSqlBaseParser.ShowFunctionsContext ctx) {
        return new StatementData(StatementType.SHOW_FUNCTIONS);
    }

    //-----------------------------------cache-------------------------------------------------

    @Override
    public StatementData visitCacheTable(SparkSqlBaseParser.CacheTableContext ctx) {
        return new StatementData(StatementType.CACHE);
    }

    @Override
    public StatementData visitUncacheTable(SparkSqlBaseParser.UncacheTableContext ctx) {
        return new StatementData(StatementType.UNCACHE);
    }

    @Override
    public StatementData visitClearCache(SparkSqlBaseParser.ClearCacheContext ctx) {
        return new StatementData(StatementType.CLEAR_CACHE);
    }

    //-----------------------------------other-------------------------------------------------

    @Override
    public StatementData visitExplain(SparkSqlBaseParser.ExplainContext ctx) {
        return new StatementData(StatementType.EXPLAIN);
    }

    @Override
    public StatementData visitAngel(SparkSqlBaseParser.AngelContext ctx) {
        return new StatementData(StatementType.ANGEL);
    }

    @Override
    public StatementData visitAddJar(SparkSqlBaseParser.AddJarContext ctx) {
        return new StatementData(StatementType.ADDJAR);
    }

    @Override
    public StatementData visitLoadTempTable(SparkSqlBaseParser.LoadTempTableContext ctx) {
        LoadData data = new LoadData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText());

        return new StatementData(StatementType.LOAD_TEMP_TABLE, data);
    }

    @Override
    public StatementData visitExportCSV(SparkSqlBaseParser.ExportCSVContext ctx) {
        ExportData data = new ExportData(ctx.tableIdentifier().db.getText(), ctx.tableIdentifier().table.getText());

        return new StatementData(StatementType.EXPORT_TABLE, data);
    }

    @Override
    public StatementData visitUse(SparkSqlBaseParser.UseContext ctx) {
        String databaseName = ctx.db.getText();
        DcDatabase data = new DcDatabase(databaseName);
        return new StatementData(StatementType.USE, data);
    }

    @Override
    public StatementData visitSetConfiguration(SparkSqlBaseParser.SetConfigurationContext ctx) {
        return new StatementData(StatementType.SET);
    }

    //-----------------------------------insert & query-------------------------------------------------

    @Override
    public StatementData visitStatementDefault(SparkSqlBaseParser.StatementDefaultContext ctx) {
        if(StringUtils.equalsIgnoreCase("select", ctx.start.getText())) {
            currentOptType = StatementType.SELECT;
            super.visitQuery(ctx.query());

            statementData.setLimit(limit);
            return new StatementData(StatementType.SELECT, statementData);
        } else if(StringUtils.equalsIgnoreCase("insert", ctx.start.getText())) {
            super.visitQuery(ctx.query());

            ParseTree tableContext = ctx.query().queryNoWith().getChild(0);
            SparkSqlBaseParser.TableIdentifierContext tableIdentifier = ((SparkSqlBaseParser.InsertOverwriteTableContext)tableContext).tableIdentifier();
            if(tableContext instanceof SparkSqlBaseParser.InsertIntoTableContext) {
                tableIdentifier = ((SparkSqlBaseParser.InsertIntoTableContext)tableContext).tableIdentifier();
            }

            String databaseName = tableIdentifier.db.getText();
            String tableName = tableIdentifier.table.getText();
            TableSource tableSource = new TableSource(databaseName, tableName);
            statementData.getOutpuTables().add(tableSource);

            if(currentOptType == StatementType.INSERT_VALUES) {
                return new StatementData(StatementType.INSERT_VALUES, statementData);
            } else {
                return new StatementData(StatementType.INSERT_SELECT, statementData);
            }
        } else if(StringUtils.equalsIgnoreCase("from", ctx.start.getText())) {
            currentOptType = StatementType.MULTI_INSERT;
            super.visitQuery(ctx.query());

            return new StatementData(StatementType.MULTI_INSERT, statementData);
        } else {
            return null;
        }
    }

    //-----------------------------------private method-------------------------------------------------

    @Override
    public StatementData visitTableIdentifier(SparkSqlBaseParser.TableIdentifierContext ctx) {
        if(currentOptType == null) {
            return null;
        }
        if(currentOptType == StatementType.CREATE_TABLE_AS_SELECT ||
                currentOptType == StatementType.SELECT ||
                currentOptType == StatementType.INSERT_SELECT) {
            TableSource tableSource = new TableSource(ctx.db.getText(), ctx.table.getText());
            statementData.getInputTables().add(tableSource);
        } else if(currentOptType == StatementType.MULTI_INSERT) {
            TableSource tableSource = new TableSource(ctx.db.getText(), ctx.table.getText());
            if("from" == multiInsertToken) {
                statementData.getInputTables().add(tableSource);
            }
        }
        return null;
    }

    @Override
    public StatementData visitInlineTableDefault1(SparkSqlBaseParser.InlineTableDefault1Context ctx) {
        currentOptType = StatementType.INSERT_VALUES;
        return null;
    }

    @Override
    public StatementData visitQuerySpecification(SparkSqlBaseParser.QuerySpecificationContext ctx) {
        currentOptType = StatementType.INSERT_SELECT;
        super.visitQuerySpecification(ctx);
        return null;
    }

    @Override
    public StatementData visitFromClause(SparkSqlBaseParser.FromClauseContext ctx) {
        multiInsertToken = "from";
        super.visitFromClause(ctx);
        return null;
    }

    @Override
    public StatementData visitMultiInsertQueryBody(SparkSqlBaseParser.MultiInsertQueryBodyContext ctx) {
        multiInsertToken = "insert";
        SparkSqlBaseParser.InsertIntoContext obj = ctx.insertInto();
        if(obj instanceof SparkSqlBaseParser.InsertOverwriteTableContext) {
            SparkSqlBaseParser.TableIdentifierContext tableIdentifier = ((SparkSqlBaseParser.InsertOverwriteTableContext)obj).tableIdentifier();
            TableSource tableSource = new TableSource(tableIdentifier.db.getText(), tableIdentifier.table.getText());
            statementData.getOutpuTables().add(tableSource);
        } else if(obj instanceof SparkSqlBaseParser.InsertIntoTableContext) {
            SparkSqlBaseParser.TableIdentifierContext tableIdentifier = ((SparkSqlBaseParser.InsertIntoTableContext)obj).tableIdentifier();
            TableSource tableSource = new TableSource(tableIdentifier.db.getText(), tableIdentifier.table.getText());
            statementData.getOutpuTables().add(tableSource);
        }
        return null;
    }

    @Override
    public StatementData visitQueryOrganization(SparkSqlBaseParser.QueryOrganizationContext ctx) {
        limit = Integer.parseInt(ctx.limit.getText());
        return null;
    }

    /**
     * 表列支持数据类型
     */
    private Boolean checkColumnDataType(String dataType) {
        if(StringUtils.startsWithIgnoreCase(dataType, "decimal")) {
            return true;
        }

        switch (dataType.toLowerCase()){
            case "string":
            case "int":
            case "bigint":
            case "double":
            case "date":
            case "timestamp":
            case "boolean":
                return true;
            default:throw new IllegalStateException("不支持数据类型：" + dataType);
        }
    }

    /**
     * 分区支持数据类型
     */
    private Boolean checkPartitionDataType(String dataType) {
        switch (dataType.toLowerCase()){
            case "string":
            case "int":
            case "bigint": return true;
            default:throw new IllegalStateException("不支持数据类型：" + dataType);
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean shouldVisitNextChild(RuleNode node, StatementData currentResult) {
        if(currentResult == null)
            return true;
        else
            return false;
    }
}




