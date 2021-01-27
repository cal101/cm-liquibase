package liquibase.sqlgenerator.core;

import liquibase.change.ColumnConfig;
import liquibase.database.Database;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.database.core.AbstractDb2Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.SelectFromDatabaseChangeLogStatement;
import liquibase.util.StringUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SelectFromDatabaseChangeLogGenerator extends AbstractSqlGenerator<SelectFromDatabaseChangeLogStatement> {

    @Override
    public ValidationErrors validate(SelectFromDatabaseChangeLogStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors errors = new ValidationErrors();
        errors.checkRequiredField("columnToSelect", statement.getColumnsToSelect());

        return errors;
    }

    @Override
    public Sql[] generateSql(SelectFromDatabaseChangeLogStatement statement, final Database database, SqlGeneratorChain sqlGeneratorChain) {
        List<ColumnConfig> columnsToSelect = Arrays.asList(statement.getColumnsToSelect());
        // use LEGACY quoting since we're dealing with system objects
        ObjectQuotingStrategy currentStrategy = database.getObjectQuotingStrategy();
        database.setObjectQuotingStrategy(ObjectQuotingStrategy.LEGACY);
        try {
            StringBuilder sql = new StringBuilder("SELECT " + (database instanceof MSSQLDatabase && statement.getLimit() != null ? "TOP "+statement.getLimit()+" " : "") + StringUtil.join(columnsToSelect, ",", new StringUtil.StringUtilFormatter<ColumnConfig>() {
                @Override
                public String toString(ColumnConfig column) {
                    if ((column.getComputed() != null) && column.getComputed()) {
                        return column.getName();
                    } else {
                        return database.escapeColumnName(null, null, null, column.getName());
                    }
                }
            }).toUpperCase() + " FROM " +
                    database.escapeTableName(database.getLiquibaseCatalogName(), database.getLiquibaseSchemaName(), database.getDatabaseChangeLogTableName()));

            SelectFromDatabaseChangeLogStatement.WhereClause whereClause = statement.getWhereClause();
            if (whereClause != null) {
                if (whereClause instanceof SelectFromDatabaseChangeLogStatement.ByTag) {
                    sql.append(" WHERE ").append(database.escapeColumnName(null, null, null, "TAG")).append("='").append(((SelectFromDatabaseChangeLogStatement.ByTag) whereClause).getTagName()).append("'");
                } else if (whereClause instanceof SelectFromDatabaseChangeLogStatement.ByNotNullCheckSum) {
                    sql.append(" WHERE ").append(database.escapeColumnName(null, null, null, "MD5SUM")).append(" IS NOT NULL");
                } else {
                    throw new UnexpectedLiquibaseException("Unknown where clause type: " + whereClause.getClass().getName());
                }
            }

            if ((statement.getOrderByColumns() != null) && (statement.getOrderByColumns().length > 0)) {
                sql.append(" ORDER BY ");
                Iterator<String> orderBy = Arrays.asList(statement.getOrderByColumns()).iterator();

                while (orderBy.hasNext()) {
                    String orderColumn = orderBy.next();
                    String[] orderColumnData = orderColumn.split(" ");
                    sql.append(database.escapeColumnName(null, null, null, orderColumnData[0]));
                    if (orderColumnData.length == 2) {
                        sql.append(" ");
                        sql.append(orderColumnData[1].toUpperCase());
                    }
                    if (orderBy.hasNext()) {
                        sql.append(", ");
                    }
                }
            }

            if (statement.getLimit() != null) {
                if (database instanceof OracleDatabase) {
                    if (whereClause == null) {
                        sql.append(" WHERE ROWNUM=").append(statement.getLimit());
                    } else {
                        sql.append(" AND ROWNUM=").append(statement.getLimit());
                    }
                } else if ((database instanceof MySQLDatabase) || (database instanceof PostgresDatabase)) {
                    sql.append(" LIMIT ").append(statement.getLimit());
                } else if (database instanceof AbstractDb2Database) {
                    sql.append(" FETCH FIRST ").append(statement.getLimit()).append(" ROWS ONLY");
                }
            }

            return new Sql[]{
                    new UnparsedSql(sql.toString())
            };
        } finally {
            database.setObjectQuotingStrategy(currentStrategy);
        }
    }
}
