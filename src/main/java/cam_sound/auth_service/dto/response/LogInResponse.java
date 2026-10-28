package cam_sound.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogInResponse {
    private InnerLogInResponse token;
    private String tokenType;

    public record InnerLogInResponse(String permission, Integer userId, String acessToken, String refreshToken) {
    }
}
