// src/model/UserDTO.java
package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String password;
    private String userName;
    private String phoneNumber;
    private String locationId;
}
