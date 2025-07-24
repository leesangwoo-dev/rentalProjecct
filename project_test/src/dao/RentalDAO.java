package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.OverdueHistoryDTO;
import model.RentalDTO;
import model.RentalHistoryDTO;
import oracle.jdbc.OracleTypes;
import util.DBUtil;

public class RentalDAO {
	// 대여내역 가져오기 (SP_GET_RENTAL_HISTORY가 RENTAL 테이블을 사용하고 OVERDUE_FEE 등을 정확히 가져와야 함)
	public List<RentalHistoryDTO> findRentalsByUserId(String loginId) {
		List<RentalHistoryDTO> list = new ArrayList<>();
		String sql = "{call SP_GET_RENTAL_HISTORY(?, ?)}";

		try (Connection conn = DBUtil.getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

			cstmt.setString(1, loginId);
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.execute();

			try (ResultSet rs = (ResultSet) cstmt.getObject(2)) {
				while (rs.next()) {
					RentalHistoryDTO dto = new RentalHistoryDTO();
					dto.setRentalNum(rs.getLong("RENTAL_NUM"));
					// SerialNum도 DTO에 추가하여 사용하면 유용합니다. (alert 메시지 등에)
					dto.setSerialNum(rs.getString("SERIAL_NUM"));
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setEqName(rs.getString("EQ_NAME"));
					dto.setRentalDate(rs.getTimestamp("RENTAL_DATE").toLocalDateTime());

					Timestamp actualReturnTS = rs.getTimestamp("ACTUAL_RETURN_DATE");
					if (actualReturnTS != null) {
						dto.setActualReturnDate(actualReturnTS.toLocalDateTime());
					}
					dto.setReturnStatus(rs.getString("RETURN_STATUS"));
					dto.setOverdueDays(rs.getLong("OVERDUE_DAYS"));
					dto.setOverdueFee(rs.getLong("OVERDUE_FEE"));
					list.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 장비를 반납 처리만 하는 프로시저 호출
	 * @param rentalNum 반납할 대여 번호
	 * @return 프로시저에서 반환하는 결과 상태 문자열 (SUCCESS, NOT_FOUND, ALREADY_RETURNED, ERROR)
	 */
	public String processReturn(Long rentalNum) {
		String status = "ERROR";
		String sql = "{call SP_PROCESS_RETURN(?, ?)}"; // 파라미터 2개: p_rental_num, o_status

		try (Connection conn = DBUtil.getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

			cstmt.setLong(1, rentalNum);
			cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
			cstmt.execute();

			status = cstmt.getString(2);

		} catch (SQLException e) {
			e.printStackTrace();
			status = "ERROR: " + e.getMessage();
		}
		return status;
	}

	/**
	 * 연체료를 0으로 업데이트하는 프로시저 호출 (결제 완료 시 사용).
	 * 
	 * @param rentalNum 연체료를 0으로 업데이트할 대여 번호
	 * @return 성공 여부 (SUCCESS, FAIL_NOT_FOUND, FAIL_UNKNOWN)
	 */
	public String updateOverdueFeeToZero(Long rentalNum) {
		String status = "FAIL_UNKNOWN";
		String sql = "{call SP_UPDATE_OVERDUE_FEE_TO_ZERO(?, ?)}";

		try (Connection conn = DBUtil.getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

			cstmt.setLong(1, rentalNum);
			cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
			cstmt.execute();

			status = cstmt.getString(2);

		} catch (SQLException e) {
			e.printStackTrace();
			status = "ERROR: " + e.getMessage();
		}
		return status;
	}

	public boolean insertRental(RentalDTO rental) {
		String sql = "{ call SP_INSERT_RENTAL(?, ?, ?, ?) }";

		try (Connection conn = DBUtil.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

			cs.setLong(1, rental.getUserId());
			cs.setString(2, rental.getSerialNum());
			cs.setTimestamp(3, Timestamp.valueOf(rental.getRentalDate()));
			cs.setTimestamp(4, Timestamp.valueOf(rental.getReturnDate()));

			cs.execute();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 관리자용 연체 장비 목록을 가져옵니다. SP_GET_OVERDUE_RENTALS_ADMIN 프로시저를 호출합니다.
	 * 
	 * @return 연체된 장비 목록 (OverdueHistoryAdminDTO 리스트)
	 */
	public List<OverdueHistoryDTO> findOverdueRentalsForAdmin() {
		List<OverdueHistoryDTO> list = new ArrayList<>();
		// SP_GET_OVERDUE_RENTALS_ADMIN은 인자가 없고 OUT 커서만 반환합니다.
		String sql = "{call SP_GET_OVERDUE_RENTALS_ADMIN(?)}";

		try (Connection conn = DBUtil.getConnection(); CallableStatement cstmt = conn.prepareCall(sql)) {

			cstmt.registerOutParameter(1, OracleTypes.CURSOR); // 첫 번째 파라미터가 OUT 커서입니다.
			cstmt.execute();

			try (ResultSet rs = (ResultSet) cstmt.getObject(1)) { // 커서 인덱스도 1로 변경
				while (rs.next()) {
					OverdueHistoryDTO dto = new OverdueHistoryDTO();
					dto.setRentalNum(rs.getLong("RENTAL_NUM"));
					dto.setLoginId(rs.getString("LOGIN_ID"));
					dto.setUserName(rs.getString("USER_NAME"));
					dto.setPhoneNumber(rs.getString("PHONE_NUMBER"));
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setEqName(rs.getString("EQ_NAME"));
					dto.setSerialNum(rs.getString("SERIAL_NUM"));
					dto.setRentalDate(rs.getTimestamp("RENTAL_DATE").toLocalDateTime());

					Timestamp returnTS = rs.getTimestamp("RETURN_DATE"); // 반납 예정일
					if (returnTS != null) {
						dto.setReturnDate(returnTS.toLocalDateTime());
					}

					Timestamp actualReturnTS = rs.getTimestamp("ACTUAL_RETURN_DATE");
					if (actualReturnTS != null) {
						dto.setActualReturnDate(actualReturnTS.toLocalDateTime());
					}
					dto.setReturnStatus(rs.getString("RETURN_STATUS"));
					dto.setOverdueDays(rs.getLong("OVERDUE_DAYS"));
					dto.setOverdueFee(rs.getLong("OVERDUE_FEE"));
					list.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// 실제 애플리케이션에서는 사용자에게 친숙한 메시지로 변환하여 알리는 것이 좋습니다.
		}
		return list;
	}

	public List<OverdueHistoryDTO> findAllRentalsForAdmin() { // DTO는 상황에 따라 AllRentalHistoryDTO 등으로 변경 가능
		List<OverdueHistoryDTO> rentals = new ArrayList<>();
		// 모든 대여 기록을 가져오는 프로시저 호출
		String sql = "{call SP_GET_ALL_RENTALS_ADMIN(?)}"; // SP_GET_ALL_RENTALS_ADMIN 프로시저 호출

		try (Connection con = DBUtil.getConnection(); CallableStatement cstmt = con.prepareCall(sql)) {

			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.execute();

			try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
				while (rs.next()) {
					// OverdueHistoryDTO는 연체 관련 필드를 포함하고 있으므로, 모든 대여 기록에도 사용 가능
					// 단, 실제 반납이 된 건은 overdueDays와 overdueFee가 0으로 나올 것입니다.
					OverdueHistoryDTO dto = new OverdueHistoryDTO();
					dto.setRentalNum(rs.getLong("RENTAL_NUM"));
					dto.setLoginId(rs.getString("LOGIN_ID"));
					dto.setUserName(rs.getString("USER_NAME"));
					dto.setPhoneNumber(rs.getString("PHONE_NUMBER"));
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setEqName(rs.getString("EQ_NAME"));
					dto.setSerialNum(rs.getString("SERIAL_NUM"));
					dto.setRentalDate(rs.getTimestamp("RENTAL_DATE").toLocalDateTime());
					dto.setReturnDate(rs.getTimestamp("RETURN_DATE").toLocalDateTime());

					Timestamp actualReturnTs = rs.getTimestamp("ACTUAL_RETURN_DATE");
					dto.setActualReturnDate(actualReturnTs != null ? actualReturnTs.toLocalDateTime() : null);

					dto.setReturnStatus(rs.getString("RETURN_STATUS"));
					dto.setOverdueDays(rs.getLong("OVERDUE_DAYS"));
					dto.setOverdueFee(rs.getLong("OVERDUE_FEE"));
					rentals.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rentals;
	}
}
