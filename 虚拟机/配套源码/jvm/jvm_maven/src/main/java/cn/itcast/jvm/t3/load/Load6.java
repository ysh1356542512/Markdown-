package cn.itcast.jvm.t3.load;

import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ServiceLoader;

public class Load6 {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/test";
        Connection conn = DriverManager.getConnection(url,"root","root");
        System.out.println(conn.isClosed());

        System.out.println(DriverManager.class.getClassLoader());
        System.out.println(Driver.class.getClassLoader());

        System.out.println(Thread.currentThread().getContextClassLoader() == ClassLoader.getSystemClassLoader());
    }
}
