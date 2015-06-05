package mobi.nowtechnologies.cleaner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Author: Gennadii Cherniaiev Date: 6/5/2015
 */
public class Remover {

    private static final int SUCCESS_CODE = 1;
    private static final int ERROR_CODE = 2;

    // -Djdbc.userName=root -Djdbc.password=12345 -Djdbc.host=localhost -Djdbc.port=3306 -Djdbc.schema=cn_service -DbatchSize=10000 -Ddelay=1000
    // java -Djdbc.userName=root -Djdbc.password=12345 -Djdbc.host=localhost -Djdbc.port=3306 -Djdbc.schema=cn_service -DbatchSize=10000 -Ddelay=1000 -jar cleaner.jar
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);

        String batchSize = System.getProperty("batchSize", "10000");
        long delay = Long.valueOf(System.getProperty("delay", "500"));

        PreparedStatement selectStatement = connection.prepareStatement("select id from users_to_remove where success = 0 limit " + batchSize);
        PreparedStatement confirmStatement = connection.prepareStatement("update users_to_remove set success = ? where id = ?");

        List<PreparedStatement> statements = getPreparedStatements(connection);


        final ResultSet resultSet = selectStatement.executeQuery();
        while (resultSet.next()) {
            Thread.sleep(delay);
            final int currentUserId = resultSet.getInt("id");
            System.out.println("*****************************");
            System.out.println(new Date() + " : start removing user " + currentUserId);
            try {
                processRemoving(statements, currentUserId);

                markRecordAsSuccess(confirmStatement, currentUserId);

                System.out.println(new Date() + " : complete removing user " + currentUserId);

                connection.commit();
            } catch (SQLException e) {
                System.out.println(e.getMessage() + " for " + currentUserId);
                connection.rollback();

                markRecordAsError(connection, confirmStatement, currentUserId);
            }
        }
        connection.setAutoCommit(true);

        close(statements);
        close(selectStatement);
        close(confirmStatement);
        close(connection);
    }

    private static void processRemoving(List<PreparedStatement> statements, int currentUserId) throws SQLException {
        for (PreparedStatement statement : statements) {
            statement.setInt(1, currentUserId);
            final int i = statement.executeUpdate();
            System.out.println(new Date() + " : " + statement.toString() + " : " + i + " records");
        }
    }

    private static void markRecordAsSuccess(PreparedStatement confirmStatement, int currentUserId) throws SQLException {
        confirmStatement.setInt(1, SUCCESS_CODE);
        confirmStatement.setInt(2, currentUserId);
        final int i = confirmStatement.executeUpdate();
        System.out.println(new Date() + " : " + confirmStatement.toString() + " : " + i + " records");
    }

    private static void markRecordAsError(Connection connection, PreparedStatement confirmStatement, int currentUserId) {
        try {
            confirmStatement.setInt(1, ERROR_CODE);
            confirmStatement.setInt(2, currentUserId);
            final int i = confirmStatement.executeUpdate();
            System.out.println(new Date() + " : " + confirmStatement.toString() + " : " + i + " records");
            connection.commit();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private static List<PreparedStatement> getPreparedStatements(Connection connection) throws IOException, SQLException {
        List<PreparedStatement> statements = new ArrayList<PreparedStatement>();

        InputStreamReader fileReader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("delete.sql"));
        BufferedReader reader = new BufferedReader(fileReader);
        String line = null;
        while ((line = reader.readLine()) != null) {
            statements.add(connection.prepareStatement(line));
        }
        return statements;
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        String userName = System.getProperty("jdbc.userName");
        String password = System.getProperty("jdbc.password");
        String host = System.getProperty("jdbc.host");
        String port = System.getProperty("jdbc.port");
        String schema = System.getProperty("jdbc.schema");

        Class.forName("com.mysql.jdbc.Driver");

        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema + "?useUnicode=yes&amp;characterEncoding=UTF-8", userName, password);
    }

    private static void close(List<PreparedStatement> statements) {
        for (PreparedStatement statement : statements) {
            close(statement);
        }
    }

    private static void close(PreparedStatement selectStatement) {
        try {
            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
