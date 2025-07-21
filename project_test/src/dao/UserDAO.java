package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import model.UserDTO;
import util.DBUtil;
import static util.Session.*;

public class UserDAO {
	public void addUser(UserDTO user) {
	    Connection conn = null;
	    CallableStatement cstmt = null;

	    try {
	        conn = DBUtil.getConnection();
	        String sql = "{call ADD_USER(?, ?, ?, ?, ?)}";
	        cstmt = conn.prepareCall(sql);

	        cstmt.setString(1, user.getId());
	        cstmt.setString(2, user.getPassword());
	        cstmt.setString(3, user.getUserName());
	        cstmt.setString(4, user.getPhoneNumber());
	        cstmt.setString(5, user.getUserGu());

	        cstmt.execute();
	        System.out.println("사용자 등록 성공");

	    } catch (SQLException e) {
	        System.err.println("사용자 등록 실패: " + e.getMessage());
	    } finally {
	        try {
	            if (cstmt != null) cstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	public boolean isIdDuplicated(String loginId) {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginId);
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
	
	public boolean isLoginValid(Connection conn, String loginId, String password) {
	    boolean isValid = false;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        String sql = "SELECT * FROM USERS WHERE ID = ? AND PASSWORD = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, loginId);
	        pstmt.setString(2, password);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            // 로그인 성공 (결과가 있음)
	            isValid = true;
	            userPassword = rs.getString("PASSWORD");
	            userName = rs.getString("USER_NAME");
	            userPhoneNumber = rs.getString("PHONE_NUMBER");
	            userGu = rs.getString("USER_GU");
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
	
	public String updateUserInfo(String loginId, String oldPassword, String newName, String newPhoneNumber, String newGu, String newPassword) {
        Connection conn = null;
        CallableStatement cstmt = null;
        String resultStatus = "UNKNOWN_ERROR";

        try {
            conn = DBUtil.getConnection();

            // 저장 프로시저 호출 구문: UPDATE_USER_INFO_PLAIN 사용
            String sql = "{call UPDATE_USER_INFO(?, ?, ?, ?, ?, ?, ?)}"; // 프로시저명 변경
            cstmt = conn.prepareCall(sql);

            // IN 파라미터 설정 (비밀번호를 평문 그대로 전달)
            cstmt.setString(1, loginId);
            cstmt.setString(2, oldPassword);
            cstmt.setString(3, newName);
            cstmt.setString(4, newPhoneNumber);
            cstmt.setString(5, newGu);
            cstmt.setString(6, (newPassword != null && !newPassword.isEmpty()) ? newPassword : null);

            // OUT 파라미터 등록
            cstmt.registerOutParameter(7, Types.VARCHAR);

            // 프로시저 실행
            cstmt.execute();

            // OUT 파라미터 값 가져오기
            resultStatus = cstmt.getString(7);

        } catch (SQLException e) {
            System.err.println("데이터베이스 오류 발생: " + e.getMessage());
            e.printStackTrace();
            resultStatus = "ERROR: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("일반 오류 발생: " + e.getMessage());
            e.printStackTrace();
            resultStatus = "ERROR: " + e.getMessage();
        }
        finally {
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("자원 해제 중 오류 발생: " + e.getMessage());
            }
        }
        return resultStatus;
    }
	
	// 사용자 ID 조회
	public Long getUserIdByLoginID(String loginId) {
	    String sql = "{ call get_user_id_by_login_id(?, ?) }";
	    try (Connection conn = DBUtil.getConnection();
	         CallableStatement cs = conn.prepareCall(sql)) {

	        cs.setString(1, loginId);
	        cs.registerOutParameter(2, Types.NUMERIC);

	        cs.execute();
	        return cs.getLong(2);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
