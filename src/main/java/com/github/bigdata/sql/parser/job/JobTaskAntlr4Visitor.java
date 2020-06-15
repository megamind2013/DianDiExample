package com.github.bigdata.sql.parser.job;

import com.github.bigdata.sql.antlr4.job.JobTaskParser;
import com.github.bigdata.sql.antlr4.job.JobTaskParserBaseVisitor;
import com.github.bigdata.sql.parser.*;
import com.github.bigdata.sql.parser.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

public class JobTaskAntlr4Visitor extends JobTaskParserBaseVisitor<StatementData> {
	 private String command = null;
	 private ArrayList<StatementData> tableDatas = new ArrayList<StatementData>();

	 @Override
	 public StatementData visitJobTask(JobTaskParser.JobTaskContext ctx){
		 StatementData tableData = super.visitJobTask(ctx);
		 tableDatas.add(tableData);

		 return tableData;
	}

	 @Override
	 public StatementData visitJobStatement(JobTaskParser.JobStatementContext ctx) {
		 String resourceName = ctx.resourceNameExpr().getText();
		 String className = ctx.classNameExpr().getText();

		 ArrayList<String> params = new ArrayList<>();

        if(ctx.paramsExpr() != null) {
            ctx.paramsExpr().children.forEach(item -> {
            	JobTaskParser.ParamExprContext param = (JobTaskParser.ParamExprContext)item;
                String value = StringUtils.substring(command, param.start.getStartIndex(), param.stop.getStopIndex() + 1);
                if(StringUtils.startsWith(value, "/")) { //解决连续多个文件路径，不能正确解析
                    value = replaceWhitespace(value);

                    params.addAll(Arrays.asList(StringUtils.split(value, " ")));
                } else {
                    value = StringUtil.cleanSingleQuote(value);
                    value = StringUtil.cleanDoubleQuote(value);

                    params.add(value);
                }
            });	
        }

        JobData jobData = new JobData(resourceName, className, params);
        return new StatementData(StatementType.JOB, jobData);
    }
	 
	 @Override
	 public StatementData visitSetStatement(JobTaskParser.SetStatementContext ctx){
		 String key = ctx.keyExpr().getText();
		 String value = StringUtils.substring(command, ctx.value.start.getStartIndex(), ctx.value.stop.getStopIndex() + 1);
		 value = StringUtil.cleanDoubleQuote(value);
		 value = StringUtil.cleanSingleQuote(value);

		 SetData data = new SetData(key, value);
		 return new StatementData(StatementType.SET, data);
	}

	 @Override
	public StatementData visitUnsetStatement(JobTaskParser.UnsetStatementContext ctx) {
		 String key = ctx.keyExpr().getText();
		 UnSetData data = new UnSetData(key);
		 return new StatementData(StatementType.UNSET, data);
	 }

	 private String replaceWhitespace(String str){
        if (str != null) {
            int len = str.length();
            if (len > 0) {
            	char[] dest = new char[len];
                int destPos = 0;
                for (int i=0;i<len;i++) {
                    char c = str.charAt(i);
                    if (!Character.isWhitespace(c)) {
                        dest[destPos++] = c;
                    } else {
                        dest[destPos++] = ' ';
                    }
                }
                return new String(dest, 0, destPos);
            }
        }
        return str;
    }

	public ArrayList<StatementData> getTableDatas() {
        return tableDatas;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
