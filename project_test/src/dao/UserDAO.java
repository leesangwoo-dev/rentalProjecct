package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBUtil;

public class UserDAO {
	public void addUser(String id, String password, String userName, String phoneNumber, String locationId) {
		Connection conn = null;
		CallableStatement cstmt = null;

		try {
			conn = DBUtil.getConnection();

			String sql = "{call ADD_USER(?, ?, ?, ?, ?)}";
			cstmt = conn.prepareCall(sql);

			cstmt.setString(1, id);
			cstmt.setString(2, password);
			cstmt.setString(3, userName);
			cstmt.setString(4, phoneNumber);
			cstmt.setString(5, locationId);

			cstmt.execute();
			System.out.println("사용자 등록 성공");

		} catch (SQLException e) {
			System.err.println("사용자 등록 실패: " + e.getMessage());
		} finally {
			try {
				if (cstmt != null)
					cstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isIdDuplicated(String userID) {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				result = true; // 중복됨
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return result;
	}
	
	public boolean isLoginValid(Connection conn, String userId, String password) {
	    boolean isValid = false;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        String sql = "SELECT * FROM USERS WHERE ID = ? AND PASSWORD = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userId);
	        pstmt.setString(2, password);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            // 로그인 성공 (결과가 있음)
	            isValid = true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { if (rs != null) rs.close(); } catch (Exception e) {}
	        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
	        try { if (conn != null) conn.close(); } catch (Exception e) {}
	    }

	    return isValid;
	}


}
