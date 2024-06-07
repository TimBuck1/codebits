import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CsvDirectoryToDatabaseLoader {

    public static void main(String[] args) {
        String sequenceFilePath = "refdata/file_sequence.txt"; // Path to the sequence file in resources
        String directoryPath = "refdata"; // Directory containing the CSV files

        try (Connection connection = getConnection()) {
            List<String> files = getFileSequence(sequenceFilePath);
            for (String fileName : files) {
                String tableName = getTableNameFromFileName(fileName);
                InputStream fileInputStream = CsvDirectoryToDatabaseLoader.class.getClassLoader().getResourceAsStream(directoryPath + "/" + fileName);
                if (fileInputStream != null) {
                    readAndInsertData(fileInputStream, tableName, connection);
                } else {
                    System.err.println("File not found: " + fileName);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getFileSequence(String sequenceFilePath) throws IOException {
        List<String> fileList = new ArrayList<>();
        try (InputStream inputStream = CsvDirectoryToDatabaseLoader.class.getClassLoader().getResourceAsStream(sequenceFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileList.add(line.trim());
            }
        }
        return fileList;
    }

    private static String getTableNameFromFileName(String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        return baseName;
    }

    private static void readAndInsertData(InputStream inputStream, String tableName, Connection connection) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Empty file: " + tableName);
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
