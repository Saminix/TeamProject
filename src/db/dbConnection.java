package db;

import java.sql.*;
import java.util.Properties;
import java.sql.DriverManager;

/**
 * Establishes Connection with the Database
 * Manages database connectivity
 * verify credentials against data
 */

public class dbConnection {

    private static final String URL = "jdbc:mysql://sst-stuproj00.city.ac.uk:3306/in2033t10";
    private static final String USERNAME = "in2033t10_a";
    private static final String PASSWORD = "lonmF2uLJSc";

    /**
     * Establishes a connection to the MySQL database using team 10's username and password
     * uses the MySQL JDBC driver
     * success/failure messages upon if connection fails
     * @throws SQLException if a database access error occurs or the JDBC driver is not found
     */

    public static Connection getConnection() throws SQLException {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties connectionProps = new Properties();
            connectionProps.put("user", USERNAME);
            connectionProps.put("password", PASSWORD);

            con = DriverManager.getConnection(URL, connectionProps);
            System.out.println("Connection Successful...");
            return con;
        }
        catch(SQLException sql) {
            System.out.println("Database Connection has Failed...");
            sql.printStackTrace();
            throw sql;
        }
        catch (ClassNotFoundException sql){
            System.out.println("Driver is not found!");
            sql.printStackTrace();
            throw new SQLException("JDBC Driver not found", sql);
        }
    }

    /**
     * Authenticates the user.
     * Checks the user database to match credentials stored in the database.
     * @param Username the username
     * @param Password the password
     * @return true if the credentials match a in the user table.
     */
    public static boolean LoginUser(String Username, String Password) {
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, Username);
            preparedStatement.setString(2, Password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}




