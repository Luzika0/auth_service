package cam_sound.auth_service.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import cam_sound.auth_service.constant.Constant;
import lombok.Data;

@Component
@Data
@JsonPropertyOrder({
        "statusCode",
        "message",
        "data"
})
public class GeneralResponseData {
    private Integer statusCode;
    private String message;
    private Object data;

    @Autowired
    public GeneralResponseData() {
        this.statusCode = 200;
        this.message = Constant.SUCESS;
        this.data = null;
    }

    public GeneralResponseData(HttpStatus statusCode, String message) {
        this.statusCode = statusCode.value();
        this.message = message;
        this.data = null;
    }

    public GeneralResponseData(Object data) {
        this.statusCode = 200;
        this.message = Constant.SUCESS;
        this.data = data;
    }
}
