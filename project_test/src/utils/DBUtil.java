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

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
	
	//try-with-resources 사용 권장
	//finally 블록에서 일일이 close()를 호출할 필요 없이, try 블록이 종료될 때 자원들이 자동으로 닫히므로 코드가 훨씬 간결해지고 자원 누수 위험이 줄어듭니다.
	public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
	    try {
	        if (rs != null) // ResultSet 닫는 코드 추가
	            rs.close();
	    } catch (Exception e) {
	        // 예외 처리 (로그를 남기는 것이 좋음)
	        System.err.println("ResultSet 닫기 중 오류: " + e.getMessage());
	    }

	    try {
	        if (pstmt != null)
	            pstmt.close();
	    } catch (Exception e) {
	        // 예외 처리 (로그를 남기는 것이 좋음)
	        System.err.println("PreparedStatement 닫기 중 오류: " + e.getMessage());
	    }

	    try {
	        if (conn != null)
	            conn.close();
	    } catch (Exception e) {
	        // 예외 처리 (로그를 남기는 것이 좋음)
	        System.err.println("Connection 닫기 중 오류: " + e.getMessage());
	    }
	}
}