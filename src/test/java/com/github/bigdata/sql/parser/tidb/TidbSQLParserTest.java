package com.github.bigdata.sql.parser.tidb;

import com.github.bigdata.sql.parser.tidb.*;

public class TidbSQLParserTest {
	public static void main(String[] args) {
		
	}
	
	public static void selectTest1() {
        String sql = "select * from users a left outer join address b on a.address_id = b.id limit 100, 10";

        val statementData = TidbSQLHelper.getStatementData(sql);
        val statement = statementData.statement
        if(statement is TableData) {
            Assert.assertEquals(StatementType.SELECT, statementData.type)
            Assert.assertEquals(2, statement.inputTables.size)
            Assert.assertEquals(10, statement.limit)
        } else {
            Assert.fail()
        }
    }
}
