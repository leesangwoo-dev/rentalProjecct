package dao;

import static utils.DBUtil.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.RentalOfficeDTO;

public class RentalOfficeDAO {

	// 구별로 대여소 아이디와 이름을 가져오는 메서드
	public List<RentalOfficeDTO> getOfficesByGu(String gu) {
		List<RentalOfficeDTO> offices = new ArrayList<>();
		String sql = "SELECT OFFICE_ID, OFFICE_NAME FROM RENTAL_OFFICE WHERE OFFICE_GU = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, gu);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					RentalOfficeDTO office = new RentalOfficeDTO();
					office.setOfficeId(rs.getInt("OFFICE_ID"));
					office.setOfficeName(rs.getString("OFFICE_NAME"));
					offices.add(office);
				}
			}
		} catch (SQLException e) {
			System.err.println("대여소 정보 조회 중 데이터베이스 오류 발생 (구: " + gu + "): " + e.getMessage());
			e.printStackTrace();
		}

		return offices;
	}

	// 모든 대여소 이름을 가져오는 메서드
	public List<String> getAllOfficeNames() {
		List<String> officeList = new ArrayList<>();
		String sql = "SELECT office_name FROM rental_office ORDER BY office_name";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				officeList.add(rs.getString("office_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return officeList;
	}

	// 구 리스트를 가져오는 메서드
	public List<String> getDistinctGuList() {
		List<String> guList = new ArrayList<>();
		String sql = "SELECT DISTINCT office_gu FROM rental_office ORDER BY office_gu";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				guList.add(rs.getString("office_gu"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return guList;
	}

	// 구별로 대여소 이름을 가져오는 메서드
	public List<String> getOfficeNamesByGu(String officeGu) {
		List<String> nameList = new ArrayList<>();
		String sql = "SELECT office_name FROM rental_office WHERE office_gu = ? ORDER BY office_name";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, officeGu);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					nameList.add(rs.getString("office_name"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	// 대여소 이름의 번호를 가져오는 메서드
	public String getPhoneByOfficeName(String officeName) {
		String phone = "";
		String sql = "SELECT office_number FROM rental_office WHERE office_name = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, officeName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					phone = rs.getString("office_number");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return phone;
	}

	// 대여소 이름의 아이디를 가져오는 메서드
	public int getOfficeIdByName(String name) {
		String sql = "SELECT office_id FROM rental_office WHERE office_name = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
