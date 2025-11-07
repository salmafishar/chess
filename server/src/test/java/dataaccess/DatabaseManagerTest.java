package dataaccess;

import org.junit.jupiter.api.Test;

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

    @Test
    void createTablesPositive() {
        assertDoesNotThrow(DatabaseManager::createTables);
    }

    @Test
    void userTableExists() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables " +
                     "WHERE table_schema = DATABASE() AND table_name = 'user'")) {
            var rs = statement.executeQuery();
            rs.next();
            assertEquals(1, rs.getInt(1), "user table was not created");
        }
    }
}