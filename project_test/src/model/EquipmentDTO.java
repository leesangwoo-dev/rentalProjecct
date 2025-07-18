package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentDTO {
	private final int eqNum;
	private final String name;
	private final String info;
	private final String price;
	private final String fee;
}