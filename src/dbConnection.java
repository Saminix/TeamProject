import java.sql.*;
import java.util.Properties;
import java.sql.DriverManager;
//This class is

/**
 * Establishes Connection with the Database
 *
 */

public class dbConnection {

    private static final String URL = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t10";
    private static final String USERNAME = "in2033t10_a";
    private static final String PASSWORD = "lonmF2uLJSc";



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





