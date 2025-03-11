import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
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


}
