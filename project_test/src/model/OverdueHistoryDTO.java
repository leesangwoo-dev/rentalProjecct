package model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OverdueHistoryDTO {
	private Long rentalNum; 				// 대여 번호
	private String loginId; 				// 사용자 로그인 ID
	private String userName; 				// 사용자 이름
	private String phoneNumber; 			// 사용자 연락처
	private String officeName; 				// 대여소명
	private String eqName; 					// 장비명
	private String serialNum; 				// 개별 장비 시리얼 번호
	private LocalDateTime rentalDate; 		// 대여 시작일
	private LocalDateTime returnDate; 		// 반납 예정일
	private LocalDateTime actualReturnDate; // 실제 반납일
	private String returnStatus; 			// 반납 상태 (e.g., '대여중', '반납완료')
	private Long overdueDays; 				// 연체일
	private Long overdueFee; 				// 연체료
	private String userGu;					// 사용자 지역
}