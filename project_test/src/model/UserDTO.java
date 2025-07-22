// src/model/UserDTO.java
package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	// ID(PK) 
	private int userID;
	// 사용자 ID
    private String loginId;
    // 비밀번호
    private String password;
    // 이름
    private String userName;
    // 전화번호
    private String phoneNumber;
    // 사용자 지역구
    private String userGu;
    // 권한
    private String role;
    
    public UserDTO(String loginId, String password, String userName,
    		String phoneNumber, String userGu)
    {
    	this.loginId = loginId;
    	this.password = password;
    	this.userName = userName;
    	this.phoneNumber = phoneNumber;
    	this.userGu = userGu;
    }
}

