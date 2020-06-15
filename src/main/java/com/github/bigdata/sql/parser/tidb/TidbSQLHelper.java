package com.github.bigdata.sql.parser.tidb;

import com.github.bigdata.sql.antlr4.ParseErrorListener;
import com.github.bigdata.sql.antlr4.ParseException;
import com.github.bigdata.sql.antlr4.PostProcessor;
import com.github.bigdata.sql.antlr4.UpperCaseCharStream;
import com.github.bigdata.sql.antlr4.mysql.MySqlLexer;
import com.github.bigdata.sql.antlr4.mysql.MySqlParser;
import com.github.bigdata.sql.parser.StatementType;
import com.github.bigdata.sql.parser.StatementData;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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

        UpperCaseCharStream charStream = new UpperCaseCharStream(CharStreams.fromString(trimCmd))
        MySqlLexer lexer = new MySqlLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ParseErrorListener());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokenStream);
        parser.addParseListener(new PostProcessor());
        parser.removeErrorListeners();
        parser.addErrorListener(new ParseErrorListener());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        val sqlVisitor = new TidbSQLAntlr4Visitor();
        try {
            try {
                // first, try parsing with potentially faster SLL mode
                val data = sqlVisitor.visit(parser.sqlStatement())
                if(data == null) {
                    return StatementData(StatementType.UNKOWN)
                } else {
                    return data
                }
            }
            catch (e: ParseCancellationException) {
                tokenStream.seek(0) // rewind input stream
                parser.reset()

                // Try Again.
                parser.interpreter.predictionMode = PredictionMode.LL
                val data = sqlVisitor.visit(parser.sqlStatement())
                if(data == null) {
                    return StatementData(StatementType.UNKOWN)
                } else {
                    return data
                }
            }
        } catch (e: ParseException) {
            if(StringUtils.isNotBlank(e.command)) {
                throw e;
            } else {
                throw e.withCommand(trimCmd)
            }
        }
    }

    @JvmStatic fun splitAlterSql(sql: String): List<String> {
        val _items = mutableListOf<String>()

        val charStream = UpperCaseCharStream(CharStreams.fromString(sql))
        val lexer = MySqlLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(ParseErrorListener())

        val tokens = CommonTokenStream(lexer)
        val parser = MySqlParser(tokens)
        val statement = parser.sqlStatement().getChild(0).getChild(0)

        if (statement is MySqlParser.AlterTableContext) {
            val tableNameContext = statement.getChild(2) as MySqlParser.TableNameContext
            val stopIndex = tableNameContext.stop.stopIndex
            val alterSqlPrefix = StringUtils.substring(sql, 0, stopIndex + 1);

            val childStat = statement.getChild(3)
            if (childStat is MySqlParser.AlterByAddColumnsContext) {
                val child = childStat.getChild(2)
                var startIndex = if(child is MySqlParser.UidContext) {
                    val rightNode = child.getChild(0)
                    if (rightNode is TerminalNodeImpl) {
                        rightNode.symbol.startIndex
                    } else {
                        val node = rightNode.getChild(0) as TerminalNodeImpl
                        node.symbol.startIndex
                    }
                } else {
                    val right = child as TerminalNodeImpl
                    right.symbol.startIndex + 1
                }

                for(i in 3 .. childStat.childCount) {
                    val node = childStat.getChild(i)
                    if(node is TerminalNodeImpl) {
                        if("," == node.text) {
                            val alterSql = StringUtils.substring(sql, startIndex, node.symbol.startIndex)
                            _items.add(alterSqlPrefix + " ADD COLUMN " + alterSql)
                            startIndex = node.symbol.startIndex + 1
                        }
                    }
                }

                var alterSql = StringUtils.substring(sql, startIndex)
                alterSql = StringUtils.substringBeforeLast(alterSql, ")")
                _items.add(alterSqlPrefix + " ADD COLUMN " + alterSql)

            } else {
                val childCount = statement.childCount;

                var index = 3
                while (index < childCount) {
                    val startContext = statement.getChild(index) as ParserRuleContext
                    index = index + 1
                    val stopContext:TerminalNodeImpl?
                            = if(index<childCount) statement.getChild(index) as TerminalNodeImpl else null

                    val startIndex = startContext.start.startIndex
                    val stopIndex = if(stopContext != null) stopContext.symbol.startIndex else -1;

                    var sql = if(stopIndex > 0) {
                        StringUtils.substring(sql, startIndex, stopIndex)
                    } else {
                        StringUtils.substring(sql, startIndex)
                    }

                    sql = StringUtils.trim(sql)
                    if(StringUtils.endsWith(sql, ";")) {
                        sql = StringUtils.substring(sql, 0, -1)
                    }

                    _items.add(alterSqlPrefix + " " + sql)

                    index = index + 1
                }
            }
        }

        return _items.toList()
    }
}
