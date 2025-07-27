package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalOfficeDTO {
	private Integer officeId;		// 대여소ID(PK)
	private String officeName;		// 대여소 명
	private String officeNumber; 	// 대여소 전화번호
	private String officeGu;		// 대여소 지역
	private String officeAddress;	// 대여소 주소

	@Override
	public String toString() {
		return this.officeName;
	}
}
