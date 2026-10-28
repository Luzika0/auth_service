package cam_sound.auth_service.dto.request;

import lombok.Data;

@Data
public class Login {

    private String emailOrUserName;
    private String password;
}
