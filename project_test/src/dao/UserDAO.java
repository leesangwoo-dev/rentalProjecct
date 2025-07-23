package dao;

import static util.DBUtil.getConnection;
import static util.Session.userGu;
import static util.Session.userLoginId;
import static util.Session.userName;
import static util.Session.userPassword;
import static util.Session.userPhoneNumber;
import static util.Session.userRole;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import model.UserDTO;

public class UserDAO {

	// 사용자 추가
	public void addUser(UserDTO user) {
		try (Connection conn = getConnection();
				CallableStatement cstmt = conn.prepareCall("{call SP_ADD_USER(?, ?, ?, ?, ?)}")) {
			cstmt.setString(1, user.getLoginId());
			cstmt.setString(2, user.getPassword());
			cstmt.setString(3, user.getUserName());
			cstmt.setString(4, user.getPhoneNumber());
			cstmt.setString(5, user.getUserGu());

			cstmt.execute();
			System.out.println("사용자 등록 성공");
		} catch (SQLException e) {
			System.err.println("사용자 등록 실패: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// ID 중복 확인
	public boolean isIdDuplicated(String loginId) {
		boolean result = false;
		String sql = "SELECT COUNT(*) FROM USERS WHERE LOGIN_ID = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, loginId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					result = count > 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 로그인 검증 및 사용자 세션 정보 설정
	public boolean isLoginValid(String loginId, String password) {
		boolean isValid = false;
		String sql = "SELECT * FROM USERS WHERE LOGIN_ID = ? AND PASSWORD = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, loginId);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					isValid = true;
					userLoginId = rs.getString("LOGIN_ID");
					userPassword = rs.getString("PASSWORD");
					userName = rs.getString("USER_NAME");
					userPhoneNumber = rs.getString("PHONE_NUMBER");
					userGu = rs.getString("USER_GU");
					userRole = rs.getString("ROLE");
					System.out.println(userLoginId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValid;
	}

	// 사용자 정보 업데이트
	public String updateUserInfo(String loginId, String oldPassword, String newName, String newPhoneNumber,
			String newGu, String newPassword) {
		String resultStatus = "UNKNOWN_ERROR";
		try (Connection conn = getConnection();
				CallableStatement cstmt = conn.prepareCall("{call SP_UPDATE_USER_INFO(?, ?, ?, ?, ?, ?, ?)}")) {

			cstmt.setString(1, loginId);
			cstmt.setString(2, oldPassword);
			cstmt.setString(3, newName);
			cstmt.setString(4, newPhoneNumber);
			cstmt.setString(5, newGu);
			cstmt.setString(6, (newPassword != null && !newPassword.isEmpty()) ? newPassword : null);
			cstmt.registerOutParameter(7, Types.VARCHAR);

			cstmt.execute();
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
		return resultStatus;
	}

	// loginId로 user_id 조회
	public Long getUserIdByLoginID(String loginId) {
		String sql = "{ call SP_GET_USER_ID_BY_LOGIN_ID(?, ?) }";
		try (Connection conn = getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
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
