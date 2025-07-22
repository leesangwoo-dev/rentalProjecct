package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.EquipmentViewDTO;
import oracle.jdbc.OracleTypes; // Oracle JDBC 드라이버에 포함된 클래스
import util.DBUtil;

public class EquipmentDAO {

	/**
	 * 장비 목록을 DB에서 조회하는 메서드
	 * 
	 * @return EquipmentViewDto 객체 리스트
	 */
	public List<EquipmentViewDTO> findEquipmentByState() {
		List<EquipmentViewDTO> equipmentList = new ArrayList<>();
		String sql = "{call SP_GET_EQUIPMENT_VIEW(?, ?)}";

		try (Connection conn = DBUtil.getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {
			cstmt.setNull(1, java.sql.Types.VARCHAR);
			// 2. 출력 파라미터 설정
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);

			// 3. 프로시저 실행 및 결과 처리
			cstmt.execute();
			try (ResultSet rs = (ResultSet) cstmt.getObject(2)) {
				while (rs.next()) {
					EquipmentViewDTO dto = new EquipmentViewDTO();
					// ... DTO에 값 설정 (이전과 동일)
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setEqName(rs.getString("EQ_NAME"));
					dto.setSerialNum(rs.getString("SERIAL_NUM"));
					dto.setRentalFee(rs.getLong("RENTAL_FEE"));
					dto.setState(rs.getString("STATE"));
					dto.setImg(rs.getString("IMG"));
					dto.setEqInfo(rs.getString("EQ_INFO"));
					equipmentList.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return equipmentList;
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