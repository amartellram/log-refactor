package com.amartellram.log;

import com.amartellram.log.exception.LogException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;

public class JobLoggerTest {

    @Test(expected = LogException.class)
    public void testLogMessage_invalidDatabaseConfig() throws LogException {
        HashMap<String, String> params = new HashMap<>();
        params.put(JobLogger.DATABASE_TYPE, "mysql");
        params.put(JobLogger.SERVER_NAME, "localhost");
        params.put(JobLogger.USER_NAME, "root");
        params.put(JobLogger.PASSWORD_FIELD, "123456");
        JobLogger jobLogger = new JobLogger(true, true, true,
                true, true, true, params);
        jobLogger.logMessage("Mensaje de Prueba", false, false, true);
    }

    @Test
    public void testLogMessage_ok() throws LogException {
        HashMap<String, String> params = new HashMap<>();
        params.put(JobLogger.DATABASE_TYPE, "mysql");
        params.put(JobLogger.SERVER_NAME, "localhost");
        params.put(JobLogger.PORT_NUMBER, "3306");
        params.put(JobLogger.USER_NAME, "root");
        params.put(JobLogger.PASSWORD_FIELD, "123456");
        // Cambiar a una ruta indicada
        params.put(JobLogger.FILE_FOLDER, "/Users/AMARTERA/test/");
        JobLogger jobLogger = new JobLogger(true, true, false,
                true, true, true, params);
        jobLogger.logMessage("Mensaje de Prueba", false, false, true);
    }

    @Test
    public void getLogLevel() {
        Level level1 = JobLogger.getLogLevel(MessageType.ERROR);
        Level level2 = JobLogger.getLogLevel(MessageType.WARNING);
        Level level3 = JobLogger.getLogLevel(MessageType.DEFAULT);
        assertEquals(Level.SEVERE, level1);
        assertEquals(Level.WARNING, level2);
        assertEquals(Level.INFO, level3);
    }
}
