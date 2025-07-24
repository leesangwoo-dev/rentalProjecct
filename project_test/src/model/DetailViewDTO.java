package model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailViewDTO {
	private String eqName;
	private String state;
	private Date getDate;
	private String eqInfo;
	private int fee;
	private String rentalOffice;
	private String phone;
	private String serialNum; // 전달용
	private String imgPath; // Optional, img 경로
}
