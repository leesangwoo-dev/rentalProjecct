package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.RentalHistoryDTO;
import oracle.jdbc.OracleTypes;
import util.DBUtil;

public class RentalDAO {

    public List<RentalHistoryDTO> findRentalsByUserId(String userId) {
        List<RentalHistoryDTO> list = new ArrayList<>();
        String sql = "{call SP_GET_RENTAL_HISTORY(?, ?)}";

        try (Connection conn = DBUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, userId);
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(2)) {
                while (rs.next()) {
                    RentalHistoryDTO dto = new RentalHistoryDTO();
                    dto.setRentalNum(rs.getLong("RENTAL_NUM"));
                    dto.setOfficeName(rs.getString("OFFICE_NAME")); // 오타 조심!
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
}
