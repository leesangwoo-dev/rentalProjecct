package dao;

import static utils.DBUtil.getConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.EquipmentViewDTO;
import oracle.jdbc.OracleTypes;

public class EquipmentDAO {
	
	//장비 목록을 DB에서 조회하는 메서드
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
					dto.setUnitPrice(rs.getInt("UNIT_PRICE"));
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

	// 장비 추가 및 개별 장비 추가를 위한 장비 아이디 가져오기
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

	// 개별장비 INSERT
	public boolean insertEachEq(String serial, int eqNum, int officeId, String state, LocalDate getDate,
			String imgPath) {
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

	/** 
	 * 저장 프로시저 SP_UPDATE_EQUIPMENT_BY_SERIAL 호출로 장비 수정
	 * @return true  : SUCCESS
	 *         false : NOT_FOUND 또는 ERROR
	 */
	public boolean updateEquipment(String serialNum,
	                               String state,
	                               String imgPath,
	                               String info,
	                               Integer fee,
	                               Integer unitPrice) {
	    String sql = "{call SP_UPDATE_EQUIPMENT_BY_SERIAL(?,?,?,?,?,?,?)}"; // 7번째는 OUT 파라미터
	    try (Connection conn = getConnection();
	         CallableStatement cstmt = conn.prepareCall(sql)) {
	        cstmt.setString(1, serialNum);
	        if (info != null)        cstmt.setString(2, info);
	        else                     cstmt.setNull(2, Types.CLOB);

	        if (fee != null)         cstmt.setInt(3, fee);
	        else                     cstmt.setNull(3, Types.NUMERIC);

	        if (unitPrice != null)   cstmt.setInt(4, unitPrice);
	        else                     cstmt.setNull(4, Types.NUMERIC);

	        if (state != null)       cstmt.setString(5, state);
	        else                     cstmt.setNull(5, Types.VARCHAR);

	        if (imgPath != null)     cstmt.setString(6, imgPath);
	        else                     cstmt.setNull(6, Types.VARCHAR);

	        cstmt.registerOutParameter(7, Types.VARCHAR);

	        cstmt.execute();
	        String status = cstmt.getString(7);   // SUCCESS / NOT_FOUND / ERROR:...

	        return "SUCCESS".equals(status);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// 모든 장비이름을 가져오는 List
	public List<String> getAllEqNames() {
	    List<String> eqNames = new ArrayList<>();
	    String sql = "SELECT eq_name FROM equipment ORDER BY eq_name";

	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            eqNames.add(rs.getString("eq_name"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return eqNames;
	}
	
	// 장비이름에 맞는 장비정보 가져오는 메서드
	public EquipmentViewDTO findByName(String name) {
	    String sql = "SELECT eq_name, eq_info, unit_price, rental_fee FROM equipment WHERE eq_name = ?";
	    EquipmentViewDTO dto = new EquipmentViewDTO();

	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, name);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                dto.setEqName(rs.getString("eq_name"));
	                dto.setEqInfo(rs.getString("eq_info"));
	                dto.setUnitPrice(rs.getInt("unit_price"));
	                dto.setRentalFee(rs.getInt("rental_fee"));
	            } else {
	                return null;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return dto;
	}

	// 장비이름에 따른 장비번호 가져오는 메서드
	public Integer getEqNumByName(String name) {
	    String sql = "SELECT eq_num FROM equipment WHERE eq_name = ?";
	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, name);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("eq_num");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;  // 해당 이름이 없을 경우
	}

	
}