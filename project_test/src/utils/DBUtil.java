package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
	private static final String URL = "jdbc:oracle:thin:@localhost:1521/xepdb1";
	private static final String USER = "hr";
	private static final String PASS = "hr";

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB 연결 객체 가져오기
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
	
	// finally 블록에서 일일이 close()를 호출할 필요 없이, try-with-resources 사용 권장
	public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
	    try {
	        if (rs != null)
	            rs.close();
	    } catch (Exception e) {
	        System.err.println("ResultSet 닫기 중 오류: " + e.getMessage());
	    }
	    try {
	        if (pstmt != null)
	            pstmt.close();
	    } catch (Exception e) {
	        System.err.println("PreparedStatement 닫기 중 오류: " + e.getMessage());
	    }
	    try {
	        if (conn != null)
	            conn.close();
	    } catch (Exception e) {
	        System.err.println("Connection 닫기 중 오류: " + e.getMessage());
	    }
	}
}