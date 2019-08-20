package com.amartellram.log;

public class LogProperties {
    private String databaseType;
    private String serverName;
    private String portNumber;
    private String userName;
    private String password;
    private String logFileFolder;

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogFileFolder() {
        return logFileFolder;
    }

    public void setLogFileFolder(String logFileFolder) {
        this.logFileFolder = logFileFolder;
    }
}
