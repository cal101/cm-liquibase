package liquibase.database;

import liquibase.exception.DatabaseException;
import liquibase.servicelocator.PrioritizedService;

import java.sql.Driver;
import java.util.Properties;

/**
 * A liquibase abstraction over the normal Connection that is available in
 * java.sql. This interface allows wrappers and aspects over the basic 
 * connection.
 * 
 */
public interface DatabaseConnection extends PrioritizedService {

    void open(String url, Driver driverObject, Properties driverProperties)
            throws DatabaseException;

    void close() throws DatabaseException;

    void commit() throws DatabaseException;

    boolean getAutoCommit() throws DatabaseException;

    String getCatalog() throws DatabaseException;

    String nativeSQL(String sql) throws DatabaseException;

    void rollback() throws DatabaseException;

    void setAutoCommit(boolean autoCommit) throws DatabaseException;

    String getDatabaseProductName() throws DatabaseException;

    String getDatabaseProductVersion() throws DatabaseException;

    int getDatabaseMajorVersion() throws DatabaseException;

    int getDatabaseMinorVersion() throws DatabaseException;

    String getURL();

    String getConnectionUserName();

    boolean isClosed() throws DatabaseException;

    void attached(Database database);
}
