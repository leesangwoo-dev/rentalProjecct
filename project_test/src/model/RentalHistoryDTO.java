package model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RentalHistoryDTO {
	// 대여 번호 (PK)
	private Long rentalNum;
	// 로그인ID
	private String loginId;
	// 대여소명
	private String officeName;
	// 장비 시리얼 번호 (FK)
	private String serialNum;
	// 장비명
	private String eqName;
	// 대여일
	private LocalDateTime rentalDate;
	// 실제 반납일
	private LocalDateTime actualReturnDate;
	// 반납 상태
	private String returnStatus;
	// 연체일
	private Long overdueDays;
	// 연체료
	private Long overdueFee;
}
