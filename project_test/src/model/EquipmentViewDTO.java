package model; // 패키지 경로는 실제 프로젝트에 맞게 수정하세요

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data            
@NoArgsConstructor 
@AllArgsConstructor
public class EquipmentViewDTO {
	private int eqId;			// 장비번호(PK)
	private String officeName;	// 대여소 명
	private String eqName;		// 장비명
	private String serialNum;	// 장비 개별 일련번호
	private int rentalFee;		// 대여료
	private int unitPrice;		// 장비 단가
	private String state;		// 상태
	private String img;			// 이미지 경로
	private String eqInfo;		// 장비 설명 정보
	private String officeGu;	// 대여소 지역
}