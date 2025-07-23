package dao;

import static util.DBUtil.getConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import model.EquipmentViewDTO;
import oracle.jdbc.OracleTypes;
import util.DBUtil; // Oracle JDBC 드라이버에 포함된 클래스;

public class EquipmentDAO {

	/**
	 * 장비 목록을 DB에서 조회하는 메서드
	 * 
	 * @return EquipmentViewDto 객체 리스트
	 */
	public List<EquipmentViewDTO> getEquipmentList(String gu, String office, String searchText) {
		List<EquipmentViewDTO> equipmentList = new ArrayList<>();
		String sql = "{call SP_GET_EQUIPMENT_VIEW(?, ?, ?, ?, ?)}";

		try (Connection conn = getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {
			// 입력 파라미터 설정
			cstmt.setNull(1, java.sql.Types.VARCHAR);
			cstmt.setString(2, gu);
			cstmt.setString(3, office);
			cstmt.setString(4, searchText);
			// 출력 파라미터 설정
			cstmt.registerOutParameter(5, OracleTypes.CURSOR);
			// 프로시저 실행 및 결과 처리
			cstmt.execute();
			try (ResultSet rs = (ResultSet) cstmt.getObject(5)) {
				while (rs.next()) {
					EquipmentViewDTO dto = new EquipmentViewDTO();
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setEqName(rs.getString("EQ_NAME"));
					dto.setSerialNum(rs.getString("SERIAL_NUM"));
					dto.setRentalFee(rs.getInt("RENTAL_FEE"));
					dto.setState(rs.getString("STATE"));
					dto.setImg(rs.getString("IMG"));
					dto.setEqInfo(rs.getString("EQ_INFO"));
					dto.setOfficeGu(rs.getString("OFFICE_GU"));
					equipmentList.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return equipmentList;
	}
	
	public int insertEquipmentAndGetId(String name, String info, int unitPrice, int rentalFee) {
	    String sql = "INSERT INTO equipment (eq_name, eq_info, unit_price, rental_fee) VALUES (?, ?, ?, ?)";
	    int generatedId = -1;

	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql, new String[] { "eq_num" })) {

	        ps.setString(1, name);
	        ps.setString(2, info);
	        ps.setInt(3, unitPrice);
	        ps.setInt(4, rentalFee);
	        ps.executeUpdate();

	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return generatedId;
	}

	public boolean insertEachEq(String serial, int eqNum, int officeId, String state, LocalDate getDate, String imgPath) {
	    String sql = "INSERT INTO each_eq (serial_num, eq_num, office_id, state, get_date, img) VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, serial);
	        ps.setInt(2, eqNum);
	        ps.setInt(3, officeId);
	        ps.setString(4, state);
	        ps.setDate(5, Date.valueOf(getDate));
	        ps.setString(6, imgPath);
	        return ps.executeUpdate() == 1;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	// 장비 상태변경
	public boolean updateState(String serialNum, String newState) {
		String sql = "UPDATE each_eq SET state = ? WHERE serial_num = ?";

		try (Connection conn = DBUtil.getConnection(); 
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newState);
			pstmt.setString(2, serialNum);
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}