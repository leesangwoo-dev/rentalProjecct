package model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RentalDTO {
	// 대여 번호 (PK)
	private Long rentalNum;
	// 사용자 고유번호 (FK)
	private Long userId;
	// 장비 시리얼 번호 (FK)
	private String serialNum;
	// 대여일
	private LocalDateTime rentalDate;
	// 반납 예정일
	private LocalDateTime returnDate;
	// 실제 반납일
	private LocalDateTime actualReturnDate;
	// 반납 상태
	private String returnStatus;
	// 연체료
	private Long overdueFee;
}