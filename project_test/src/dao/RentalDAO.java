package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import model.RentalDTO;
import util.DBUtil;

public class RentalDAO {

    public boolean insertRental(RentalDTO rental) {
        String sql = "{ call insert_rental_proc(?, ?, ?, ?) }";

        try (Connection conn = DBUtil.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

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
}
