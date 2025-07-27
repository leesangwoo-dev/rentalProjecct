package model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RentalHistoryDTO {
	private Long rentalNum;					// 대여 번호 (PK)
	private String loginId;					// 로그인ID
	private String officeName;				// 대여소명
	private String serialNum;				// 장비 시리얼 번호 (FK)
	private String eqName;					// 장비명
	private LocalDateTime rentalDate;		// 대여일
	private LocalDateTime actualReturnDate;	// 실제 반납일
	private String returnStatus;			// 반납 상태
	private Long overdueDays;				// 연체일
	private Long overdueFee;				// 연체료
}
