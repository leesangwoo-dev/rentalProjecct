package model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RentalDTO {
	private Long rentalNum;					// 대여 번호 (PK)
	private Long userId;					// 사용자 고유번호 (FK)
	private String serialNum;				// 장비 시리얼 번호 (FK)
	private LocalDateTime rentalDate;		// 대여일
	private LocalDateTime returnDate;		// 반납 예정일
	private LocalDateTime actualReturnDate;	// 실제 반납일
	private String returnStatus;			// 반납 상태
	private Long overdueFee;				// 연체료
}