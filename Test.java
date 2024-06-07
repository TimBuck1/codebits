import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CsvDirectoryToDatabaseLoader {

    public static void main(String[] args) {
        String directoryPath = "/path/to/directory"; // Replace with the directory containing your CSV files

        try (Connection connection = getConnection()) {
            File directory = new File(directoryPath);
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            if (files != null) {
                for (File file : files) {
                    String tableName = getTableNameFromFileName(file.getName());
                    readAndInsertData(file, tableName, connection);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTableNameFromFileName(String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        return baseName;
    }

    private static void readAndInsertData(File file, String tableName, Connection connection) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Empty file: " + file.getName());
            }

            String[] columns = headerLine.split(",");
            String insertSqlTemplate = generateInsertSqlTemplate(tableName, columns);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String insertSql = createInsertSql(insertSqlTemplate, values);
                executeSql(connection, insertSql);
            }
        }
    }

    private static String generateInsertSqlTemplate(String tableName, String[] columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return sql.toString();
    }

    private static String createInsertSql(String template, String[] values) {
        for (String value : values) {
            template = template.replaceFirst("\\?", "'" + value + "'");
        }
        return template;
    }

    private static void executeSql(Connection connection, String sql) throws SQLException {
        System.out.println("Executing SQL: " + sql);
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error executing SQL: " + sql);
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Message: " + e.getMessage());
            throw e;
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your Oracle DB connection string
        String user = "yourusername"; // Replace with your Oracle DB username
        String password = "yourpassword"; // Replace with your Oracle DB password
        return DriverManager.getConnection(url, user, password);
    }
}
