package com.github.bigdata.sql.parser.tidb;

import com.github.bigdata.sql.antlr4.ParseErrorListener;
import com.github.bigdata.sql.antlr4.ParseException;
import com.github.bigdata.sql.antlr4.PostProcessor;
import com.github.bigdata.sql.antlr4.UpperCaseCharStream;
import com.github.bigdata.sql.antlr4.mysql.MySqlLexer;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.AlterByAddColumnsContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.TableNameContext;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser.UidContext;
import com.github.bigdata.sql.parser.StatementType;

import kotlin.jvm.JvmStatic;

import com.github.bigdata.sql.parser.StatementData;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.commons.lang.StringUtils;

public class TidbSQLHelper {
	public static Boolean checkSupportedSQL(StatementType statementType) {
		if(statementType == StatementType.CREATE_TABLE || statementType == StatementType.DROP_TABLE_TIDB || statementType == StatementType.TRUNCATE_TABLE || statementType == StatementType.RENAME_TABLE ||
				statementType == StatementType.ALTER_TABLE_CHANGE_COL || statementType == StatementType.ALTER_TABLE_MODIFY_COL || statementType == StatementType.ALTER_TABLE_ADD_COL || 
				statementType == StatementType.ALTER_TABLE_DROP_COL || statementType == StatementType.ALTER_TABLE_ADD_INDEX || statementType == StatementType.ALTER_TABLE_DROP_INDEX || statementType == StatementType.ALTER_TABLE_ADD_UNIQUE_KEY ||
				statementType == StatementType.INSERT_SELECT || statementType == StatementType.INSERT_VALUES || statementType == StatementType.SELECT || statementType == StatementType.DELETE || statementType == StatementType.UPDATE || statementType == StatementType.ANALYZE_TABLE || statementType == StatementType.SHOW) {
			return true;
		}else {
			return false;
		}
    }

    public static StatementData getStatementData(String command){
        String trimCmd = StringUtils.trim(command);

        if(StringUtils.startsWithIgnoreCase(trimCmd,"show")) {
            return new StatementData(StatementType.SHOW, null);
        }

        UpperCaseCharStream charStream = new UpperCaseCharStream(CharStreams.fromString(trimCmd));
        MySqlLexer lexer = new MySqlLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ParseErrorListener());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokenStream);
        parser.addParseListener(new PostProcessor());
        parser.removeErrorListeners();
        parser.addErrorListener(new ParseErrorListener());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        TidbSQLAntlr4Visitor sqlVisitor = new TidbSQLAntlr4Visitor();
        try {
            try {
                // first, try parsing with potentially faster SLL mode
                StatementData data = sqlVisitor.visit(parser.sqlStatement());
                if(data == null) {
                    return new StatementData(StatementType.UNKOWN);
                } else {
                    return data;
                }
            }
            catch (ParseCancellationException e) {
                tokenStream.seek(0); // rewind input stream
                parser.reset();

                // Try Again.
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                StatementData data = sqlVisitor.visit(parser.sqlStatement());
                if(data == null) {
                    return new StatementData(StatementType.UNKOWN);
                } else {
                    return data;
                }
            }
        } catch (ParseException e) {
            if(StringUtils.isNotBlank(e.getCommand())) {
                throw e;
            } else {
                throw e.withCommand(trimCmd);
            }
        }
    }

    @JvmStatic 
    public List<String> splitAlterSql(String sql) {
        List<String> _items = new ArrayList<String>();

        UpperCaseCharStream charStream = new UpperCaseCharStream(CharStreams.fromString(sql));
        MySqlLexer lexer = new MySqlLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ParseErrorListener());

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);
        ParseTree statement = parser.sqlStatement().getChild(0).getChild(0);

        if (statement instanceof MySqlParser.AlterTableContext) {
        	TableNameContext tableNameContext = (TableNameContext)statement.getChild(2);
            int stopIndex = tableNameContext.stop.getStopIndex();
            String alterSqlPrefix = StringUtils.substring(sql, 0, stopIndex + 1);

            ParseTree childStat = statement.getChild(3);
            if (childStat instanceof AlterByAddColumnsContext) {
            	ParseTree child = childStat.getChild(2);
                int startIndex = ((TerminalNodeImpl)child).symbol.getStartIndex() + 1;
                if(child instanceof UidContext) {
                	ParseTree rightNode = child.getChild(0);
                    if (rightNode instanceof TerminalNodeImpl) {
                    	startIndex = ((TerminalNodeImpl) rightNode).getSymbol().getStartIndex();
                    } else {
                    	TerminalNodeImpl node = (TerminalNodeImpl)rightNode.getChild(0);
                    	startIndex = node.symbol.getStartIndex();
                    }
                }

                for(int i=3;i<childStat.getChildCount();i++) {
                    ParseTree node = childStat.getChild(i);
                    if(node instanceof TerminalNodeImpl) {
                        if("," == node.getText()) {
                            String alterSql = StringUtils.substring(sql, startIndex, ((TerminalNodeImpl) node).getSymbol().getStartIndex());
                            _items.add(alterSqlPrefix + " ADD COLUMN " + alterSql);
                            startIndex = ((TerminalNodeImpl) node).getSymbol().getStartIndex() + 1;
                        }
                    }
                }

                String alterSql = StringUtils.substring(sql, startIndex);
                alterSql = StringUtils.substringBeforeLast(alterSql, ")");
                _items.add(alterSqlPrefix + " ADD COLUMN " + alterSql);

            } else {
                int childCount = statement.getChildCount();

                int index = 3;
                while (index < childCount) {
                	ParserRuleContext startContext = (ParserRuleContext)statement.getChild(index);
                    index = index + 1;
                    TerminalNodeImpl stopContext = null;
                    if(index<childCount) 
                    	stopContext = (TerminalNodeImpl)statement.getChild(index);

                    int startIndex = startContext.start.getStartIndex();
                    stopIndex = -1;
                    if(stopContext != null) 
                    	stopIndex = stopContext.symbol.getStartIndex();

                    sql = StringUtils.substring(sql, startIndex);
                    if(stopIndex > 0) {
                    	sql = StringUtils.substring(sql, startIndex, stopIndex);
                    } 

                    sql = StringUtils.trim(sql);
                    if(StringUtils.endsWith(sql, ";")) {
                        sql = StringUtils.substring(sql, 0, -1);
                    }

                    _items.add(alterSqlPrefix + " " + sql);

                    index = index + 1;
                }
            }
        }

        return _items;
    }
}
