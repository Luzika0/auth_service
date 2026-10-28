package cam_sound.auth_service.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Register {
    private String name;
    private String userName;
    private String email;
    private String password;
    private LocalDate dob;
}
