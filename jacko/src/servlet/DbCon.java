package servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbCon{

    private DbCon(){}

    public static Connection con()throws SQLException, ClassNotFoundException {
	Class.forName("com.mysql.jdbc.Driver");
        return(DriverManager.getConnection("jdbc:mysql://localhost/jacko", "a", "a"));
    }
}
