// src/model/UserDTO.java
package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private int userID;			// ID(PK)
	private String loginId;		// 사용자 ID
	private String password;	// 비밀번호
	private String userName;	// 이름
	private String phoneNumber;	// 전화번호
	private String userGu;		// 사용자 지역구
	private String role;		// 권한

	public UserDTO(String loginId, String password, String userName, String phoneNumber, String userGu) {
		this.loginId = loginId;
		this.password = password;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.userGu = userGu;
	}
}
