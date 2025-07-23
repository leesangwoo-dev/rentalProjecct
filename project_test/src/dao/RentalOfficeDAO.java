package dao;

import static util.DBUtil.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.RentalOfficeDTO;

public class RentalOfficeDAO {

	public List<RentalOfficeDTO> getOfficesByGu(String gu) {
		List<RentalOfficeDTO> offices = new ArrayList<>();
		// SQL 쿼리 문자열: OFFICE_ID도 함께 가져옵니다.
		String sql = "SELECT OFFICE_ID, OFFICE_NAME FROM RENTAL_OFFICE WHERE OFFICE_GU = ?"; // OFFICE_ID 추가

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, gu); // 바인딩 변수 설정
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					RentalOfficeDTO office = new RentalOfficeDTO();
					office.setOfficeId(rs.getInt("OFFICE_ID")); // OFFICE_ID 설정
					office.setOfficeName(rs.getString("OFFICE_NAME")); // OFFICE_NAME 설정
					offices.add(office);
				}
			}
		} catch (SQLException e) {
			System.err.println("대여소 정보 조회 중 데이터베이스 오류 발생 (구: " + gu + "): " + e.getMessage());
			e.printStackTrace();
		}

		return offices;
	}

}
