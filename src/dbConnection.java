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
            Class.forName("com.mysql.jdbc.Driver");

            Properties connectionProps = new Properties();
            connectionProps.put("username", USERNAME);
            connectionProps.put("password", PASSWORD);

            con = DriverManager.getConnection(URL, connectionProps);
            System.out.println("Connection Successful...");
        }
        catch(SQLException sql) {
            System.out.println("Database Connection has Failed...");
            //display the error
            sql.printStackTrace();

        }
        catch (ClassNotFoundException sql){
            System.out.println("Driver is not found!");
            sql.printStackTrace();
        }
        finally{
            return con;
        }


    }

    public static boolean LoginUser(String Username, String Password) {
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, Username);
            preparedStatement.setString(2, Password); // Use hashing if needed

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If a record exists, login is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }






}





