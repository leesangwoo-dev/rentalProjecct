package model;

import lombok.Data;

@Data
public class RentalOfficeDTO {
	// 대여소 고유번호 (PK)
	private Long officeId;
	// 대여소 이름
	private String officeName;
	// 대여소 전화번호
	private String officeNumber;
	// 대여소 지역 (구)
	private String officeGu;
	// 대여소 상세 주소
	private String officeAddress;
}
