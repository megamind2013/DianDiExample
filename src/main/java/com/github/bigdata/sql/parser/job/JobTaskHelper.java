package com.github.bigdata.sql.parser.job;

import com.github.bigdata.sql.antlr4.ParseErrorListener;
import com.github.bigdata.sql.antlr4.ParseException;
import com.github.bigdata.sql.antlr4.PostProcessor;
import com.github.bigdata.sql.antlr4.UpperCaseCharStream;
import com.github.bigdata.sql.antlr4.job.JobTaskLexer;
import com.github.bigdata.sql.antlr4.job.JobTaskParser;
import com.github.bigdata.sql.parser.StatementData;

import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.lang.StringUtils;

public class JobTaskHelper {

	public static ArrayList<StatementData> getStatementData(String command){
        String trimCmd = StringUtils.trim(command);

        UpperCaseCharStream charStream = new UpperCaseCharStream(CharStreams.fromString(trimCmd));
        JobTaskLexer lexer = new JobTaskLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ParseErrorListener());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JobTaskParser parser = new JobTaskParser(tokenStream);
        parser.addParseListener(new PostProcessor());
        parser.removeErrorListeners();
        parser.addErrorListener(new ParseErrorListener());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        JobTaskAntlr4Visitor cmdVisitor = new JobTaskAntlr4Visitor();
        cmdVisitor.setCommand(trimCmd);
        try {
            try {
                // first, try parsing with potentially faster SLL mode
                cmdVisitor.visit(parser.jobTasks());
                return cmdVisitor.getTableDatas();
            }
            catch (ParseCancellationException e) {
                tokenStream.seek(0); // rewind input stream
                parser.reset();

                // Try Again.
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                cmdVisitor.visit(parser.jobTasks());
                return cmdVisitor.getTableDatas();
            }
        } catch (ParseException e) {
            if(StringUtils.isNotBlank(e.getCommand())) {
                throw e;
            } else {
                throw e.withCommand(trimCmd);
            }
        }
    }
}