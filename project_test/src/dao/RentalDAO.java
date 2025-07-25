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
import utils.DBUtil;

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
					dto.setLoginId(rs.getString("LOGIN_ID"));
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

	/* 장비를 반납 처리만 하는 프로시저 호출
	 	@param rentalNum 반납할 대여 번호
	 */
	public String processReturn(Long rentalNum) {
		String status = "ERROR";
		String sql = "{call SP_PROCESS_RETURN(?, ?)}"; 
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

	// 연체료를 0으로 업데이트하는 프로시저 호출 (결제 완료 시 사용).
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
	
	// 대여 테이블 INSERT
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
	
	// 특정 대여소의 모든 대여 기록 가져옴
	public List<OverdueHistoryDTO> findAllRentalsForAdmin() {
		List<OverdueHistoryDTO> rentals = new ArrayList<>();
		String sql = "{call SP_GET_ALL_RENTALS_ADMIN_BY_GU(?)}";

		try (Connection con = DBUtil.getConnection(); CallableStatement cstmt = con.prepareCall(sql)) {

			cstmt.registerOutParameter(1, OracleTypes.CURSOR); // OUT 커서 등록
			cstmt.execute();

			try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
				while (rs.next()) {
					OverdueHistoryDTO dto = new OverdueHistoryDTO();
					dto.setRentalNum(rs.getLong("RENTAL_NUM"));
					dto.setLoginId(rs.getString("LOGIN_ID"));
					dto.setUserName(rs.getString("USER_NAME"));
					dto.setPhoneNumber(rs.getString("PHONE_NUMBER"));
					dto.setOfficeName(rs.getString("OFFICE_NAME"));
					dto.setUserGu(rs.getString("OFFICE_GU"));
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