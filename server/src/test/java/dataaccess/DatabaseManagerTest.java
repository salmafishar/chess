package dataaccess;

import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseManagerTest {

    @Test
    void createDatabase() {
        assertDoesNotThrow(DatabaseManager::createDatabase);
    }

    @Test
    /* open connection -> create a statement -> execute -> move to first row ->
        get int -> assert it to 1
     */
    void getConnection() throws Exception {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT 1");
             var execute = statement.executeQuery()) {
            execute.next();
            int res = execute.getInt(1);
            assertEquals(1, res);
        }
    }
}