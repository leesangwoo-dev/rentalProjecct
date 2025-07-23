package dao;

import static util.DBUtil.getConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.EquipmentViewDTO;
import oracle.jdbc.OracleTypes; // Oracle JDBC 드라이버에 포함된 클래스;

public class EquipmentDAO {

	/**
	 * 장비 목록을 DB에서 조회하는 메서드
	 * 
	 * @return EquipmentViewDto 객체 리스트
	 */
	public List<EquipmentViewDTO> getEquipmentList(String gu) {
		List<EquipmentViewDTO> equipmentList = new ArrayList<>();
		String sql = "{call SP_GET_EQUIPMENT_VIEW(?, ?, ?)}";

		try (Connection conn = getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {
			cstmt.setNull(1, java.sql.Types.VARCHAR);
			cstmt.setString(2, gu);
			// 2. 출력 파라미터 설정
			cstmt.registerOutParameter(3, OracleTypes.CURSOR);

			// 3. 프로시저 실행 및 결과 처리
			cstmt.execute();
			try (ResultSet rs = (ResultSet) cstmt.getObject(3)) {
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
<<<<<<< HEAD
	
	/**
     * 특정 상태의 장비 목록을 조회합니다.
     * 이 메서드는 SP_GET_EQUIPMENT_VIEW 프로시저를 호출하며,
     * P_STATE 파라미터에만 값을 전달하고 나머지 검색 조건은 NULL로 설정합니다.
     *
     * @param state 조회할 장비 상태 (예: "사용가능", "대여중", "수리중")
     * @return 해당 상태의 EquipmentViewDTO 리스트
     */
    public List<EquipmentViewDTO> getEquipmentByState(String state) {
        List<EquipmentViewDTO> equipmentList = new ArrayList<>();
        // 프로시저 SP_GET_EQUIPMENT_VIEW는 이제 5개의 IN 파라미터와 1개의 OUT 커서 파라미터를 가집니다.
        // 순서: P_STATE, P_EQ_NAME, P_OFFICE_ID, P_OFFICE_GU, P_RESULT_CURSOR
        String sql = "{call SP_GET_EQUIPMENT_VIEW(?, ?, ?, ?, ?)}"; // 5개의 IN 파라미터 + 1개의 OUT 커서

        try (Connection conn = getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {
            // 1. 입력 파라미터 설정
            // P_STATE (1번째 파라미터): 메서드 인자로 받은 'state' 값 설정
            if (state != null && !state.isEmpty()) {
                cstmt.setString(1, state);
            } else {
                cstmt.setNull(1, java.sql.Types.VARCHAR); // state가 null 또는 비어있으면 DB에 NULL 전달
            }

            // P_EQ_NAME (2번째 파라미터): 이 메서드는 상태만 필터링하므로 NULL 설정
            cstmt.setNull(2, java.sql.Types.VARCHAR);

            // P_OFFICE_ID (3번째 파라미터): 이 메서드는 상태만 필터링하므로 NULL 설정
            cstmt.setNull(3, java.sql.Types.NUMERIC);

            // P_OFFICE_GU (4번째 파라미터): 이 메서드는 상태만 필터링하므로 NULL 설정
            cstmt.setNull(4, java.sql.Types.VARCHAR);

            // 2. 출력 파라미터 설정 (5번째 파라미터는 커서)
            cstmt.registerOutParameter(5, OracleTypes.CURSOR);

            // 3. 프로시저 실행 및 결과 처리
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(5)) { // 5번째 파라미터에서 결과 커서 가져오기
                while (rs.next()) {
                    EquipmentViewDTO dto = new EquipmentViewDTO();
                    // DTO에 값 설정 (프로시저에서 반환하는 모든 컬럼 포함)
                    dto.setOfficeName(rs.getString("OFFICE_NAME"));
                    dto.setEqName(rs.getString("EQ_NAME"));
                    dto.setSerialNum(rs.getString("SERIAL_NUM"));
                    dto.setRentalFee(rs.getInt("RENTAL_FEE")); // DTO의 rentalFee 타입이 Long이라고 가정
                    dto.setState(rs.getString("STATE"));
                    dto.setImg(rs.getString("IMG"));
                    dto.setEqInfo(rs.getString("EQ_INFO"));
                    // 추가된 컬럼들 (DTO에 setter 및 Property가 정의되어 있어야 함)
                    //dto.setOfficeId(rs.getInt("OFFICE_ID")); // 프로시저에서 OFFICE_ID를 반환하도록 수정했어야 함
                    //dto.setOfficeGu(rs.getString("OFFICE_GU")); // 프로시저에서 OFFICE_GU를 반환하도록 수정했어야 함

                    equipmentList.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("장비 상태별 조회 중 데이터베이스 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }
=======

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

>>>>>>> refs/heads/HYUNSEOK
}