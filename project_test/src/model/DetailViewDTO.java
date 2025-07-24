package model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 장비상세정보
public class DetailViewDTO {
	private String eqName;	// 장비명
	private String state;	// 상태
	private Date getDate;	// 장비 취득일
	private String eqInfo;	// 장비 설명 정보
	private int fee;		// 대여료
	private String rentalOffice;	// 대여소
	private String phone;	// 대여소 전화번호
	private String serialNum; // 장비 일련번호
	private String imgPath; // img 경로
}
