package model; // 패키지 경로는 실제 프로젝트에 맞게 수정하세요

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data            
@NoArgsConstructor 
@AllArgsConstructor
public class EquipmentViewDTO {

	private int eqId;
	private String officeName;
	private String eqName;
	private String serialNum;
	private int rentalFee;
	private int unitPrice;
	private String state;
	private String img;
	private String eqInfo;
	private String officeGu;
}