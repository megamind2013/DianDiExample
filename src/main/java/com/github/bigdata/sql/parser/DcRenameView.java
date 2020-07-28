package com.github.bigdata.sql.parser;

public class DcRenameView extends Statement{
    private String databaseName;
    private String oldName;
    private String newName;

    public DcRenameView(){}

    public DcRenameView(String databaseName,String oldName,String newName){
        this.databaseName = databaseName;
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
