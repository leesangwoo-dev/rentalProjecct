// src/model/UserDTO.java
package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	// 사용자 ID
    private String id;
    // 비밀번호
    private String password;
    // 이름
    private String userName;
    // 전화번호
    private String phoneNumber;
    // 사용자 지역구
    private String userGu;
}
