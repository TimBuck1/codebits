import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static void readAndInsertData(InputStream inputStream, String tableName, Connection connection) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Empty file: " + tableName);
            }

            String[] columns = headerLine.split(",");
            String insertSqlTemplate = generateInsertSqlTemplate(tableName, columns);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSqlTemplate)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    setPreparedStatementValues(preparedStatement, columns, values);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
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

    private static void setPreparedStatementValues(PreparedStatement preparedStatement, String[] columns, String[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            if ("todaysdate".equalsIgnoreCase(value)) {
                preparedStatement.setString(i + 1, getCurrentDate());
            } else if ("\"\"".equals(value) || value.isEmpty()) {
                preparedStatement.setNull(i + 1, java.sql.Types.NULL);
            } else {
                value = value.replaceAll("^\"|\"$", ""); // Remove surrounding double quotes if any
                if (isDateColumn(columns[i])) {
                    preparedStatement.setString(i + 1, value);
                } else {
                    preparedStatement.setString(i + 1, value);
                }
            }
        }
    }

    private static boolean isDateColumn(String columnName) {
        // List all date columns here
        return "date".equalsIgnoreCase(columnName);
    }

    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
        return sdf.format(new Date());
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your Oracle DB connection string
        String user = "yourusername"; // Replace with your Oracle DB username
        String password = "yourpassword"; // Replace with your Oracle DB password
        return DriverManager.getConnection(url, user, password);
    }
}
