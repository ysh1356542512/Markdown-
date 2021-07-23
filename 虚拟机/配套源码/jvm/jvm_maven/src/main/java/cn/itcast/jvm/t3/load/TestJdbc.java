package cn.itcast.jvm.t3.load;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestJdbc {
	
	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test??useAffectedRows=true", "root", "root");
		PreparedStatement psmt = conn.prepareStatement("update sys_user set password=?");
		psmt.setString(1, "444");
		int rows = psmt.executeUpdate();
		System.out.println(psmt.getUpdateCount());
		System.out.println(rows);
		System.out.println("ok!!!");
	}

}
