package cam_sound.auth_service.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cam_sound.auth_service.dto.GeneralResponseData;

@ControllerAdvice

public class GloubleHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<GeneralResponseData> appException(AppException ex) {
        final var errorCode = ex.getErrorCode();
        final var response = new GeneralResponseData(errorCode.getStatus(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponseData> handleException(Exception ex) {
        final var response = new GeneralResponseData(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
