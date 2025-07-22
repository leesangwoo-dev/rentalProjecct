package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalOfficeDTO {
	
	private Integer officeId;
    private String officeName;
    private String officeNumber;
    private String officeGu;
    private String officeAddress;

    // ComboBox에 이 객체가 표시될 때 어떤 문자열로 보일지 정의합니다.
    @Override
    public String toString() {
        return this.officeName;
    }
}
