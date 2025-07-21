package model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Each_EquipmentDTO {
	private final String serialNum;
	private final int officeId;
	private final String state;
	private final int eqNum;
	private final LocalDateTime get_date;
	private final String img_url;
}