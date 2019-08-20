package com.amartellram.log;

import com.amartellram.log.exception.LogException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
    private final boolean logToFile;
    private final boolean logToConsole;
    private final boolean logMessage;
    private final boolean logWarning;
    private final boolean logError;
    private final boolean logToDatabase;
    private final LogProperties logProperties;
    private final Logger logger;

    public static final String DATABASE_TYPE = "dbms";
    public static final String SERVER_NAME = "serverName";
    public static final String PORT_NUMBER = "portNumber";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD_FIELD = "password";
    public static final String FILE_FOLDER = "logFileFolder";

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                     boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        logProperties = getLogProperties(dbParamsMap);
    }

    public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws LogException {
        if (messageText == null || messageText.trim().length() == 0) {
            return;
        }

        messageText = messageText.trim();
        validateMessageAndLogTypes(message, warning, error);
        MessageType messageType = getMessageType(message, warning, error);
        handleLogs(messageText, message, messageType);
    }

    private void validateMessageAndLogTypes(boolean message, boolean warning, boolean error) throws LogException {
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new LogException("Invalid configuration");
        }

        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new LogException("Error or Warning or Message must be specified");
        }
    }

    public MessageType getMessageType(boolean message, boolean warning, boolean error) {
        if (message && logMessage)
            return MessageType.MESSAGE;
        else if (error && logError)
            return MessageType.ERROR;
        else if (warning && logWarning)
            return MessageType.WARNING;
        return MessageType.DEFAULT;
    }

    private void handleLogs(String messageText, boolean message, MessageType messageType) throws LogException {
        try {
            Level logLevel = getLogLevel(messageType);
            loggingToFile(messageText, logProperties.getLogFileFolder(), logLevel);
            loggingToConsole(messageText, logLevel);
            loggingToDatabase(message, messageType);
        } catch (SQLException e) {
            throw new LogException("Error on log database");
        } catch (IOException e) {
            throw new LogException("Error on log file");
        }
    }

    private void loggingToFile(String messageText, String fileFolder, Level level) throws IOException, LogException {
        if (logToFile) {
            if (fileFolder == null)
                throw new LogException("Invalid file folder");
            String filePath = fileFolder + "/logFile.txt";
            File logFile = new File(filePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileHandler fh = new FileHandler(filePath);
            logger.addHandler(fh);
            logger.log(level, messageText);
        }
    }

    private void loggingToConsole(String messageText, Level level) {
        if (logToConsole) {
            ConsoleHandler ch = new ConsoleHandler();
            logger.addHandler(ch);
            logger.log(level, messageText);
        }
    }

    private void loggingToDatabase(boolean message, MessageType messageType) throws SQLException, LogException {
        if (logToDatabase) {
            if (isInvalidConnectionProperties(logProperties))
                throw new LogException("Invalid database properties");
            try (Connection connection = getDatabaseConnection(logProperties)) {
                String query = "insert into Log_Values(?,?)";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, String.valueOf(message));
                    pstmt.setString(2, messageType.getType());
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private Connection getDatabaseConnection(LogProperties logProperties) throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.setProperty(USER_NAME, logProperties.getUserName());
        connectionProps.setProperty(PASSWORD_FIELD, logProperties.getPassword());

        String connectionString = new StringBuilder("jdbc:").append(logProperties.getDatabaseType()).append("://").append(logProperties.getServerName())
                .append(":").append(logProperties.getPortNumber()).append("/").toString();
        return DriverManager.getConnection(connectionString, connectionProps);
    }

    public static Level getLogLevel(MessageType messageType) {
        switch (messageType) {
            case WARNING:
                return Level.WARNING;
            case ERROR:
                return Level.SEVERE;
            case DEFAULT:
            case MESSAGE:
            default:
                return Level.INFO;
        }
    }

    public LogProperties getLogProperties(Map dbParams) {
        LogProperties properties = new LogProperties();
        if (dbParams == null) return null;
        if (dbParams.get(DATABASE_TYPE) != null)
            properties.setDatabaseType(dbParams.get(DATABASE_TYPE).toString());

        if (dbParams.get(SERVER_NAME) != null)
            properties.setServerName(dbParams.get(SERVER_NAME).toString());

        if (dbParams.get(PORT_NUMBER) != null)
            properties.setPortNumber(dbParams.get(PORT_NUMBER).toString());

        if (dbParams.get(USER_NAME) != null)
            properties.setUserName(dbParams.get(USER_NAME).toString());

        if (dbParams.get(PASSWORD_FIELD) != null)
            properties.setPassword(dbParams.get(PASSWORD_FIELD).toString());

        if (dbParams.get(FILE_FOLDER) != null)
            properties.setLogFileFolder(dbParams.get(FILE_FOLDER).toString());

        return properties;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private boolean isInvalidConnectionProperties(LogProperties logProperties) {
        return isEmpty(logProperties.getDatabaseType()) || isEmpty(logProperties.getServerName()) ||
                isEmpty(logProperties.getPortNumber()) || isEmpty(logProperties.getUserName()) ||
                isEmpty(logProperties.getPassword());
    }

}

