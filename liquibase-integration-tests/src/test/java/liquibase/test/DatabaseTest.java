package liquibase.test;

import liquibase.database.Database;

public interface DatabaseTest {
    void performTest(Database database) throws Exception;
}
