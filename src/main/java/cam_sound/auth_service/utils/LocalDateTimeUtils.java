package cam_sound.auth_service.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeUtils {

    private LocalDateTimeUtils() {

    }

    public static LocalDateTime KHMER_LOCAL_DATE_TIME() {
        return LocalDateTime.now(ZoneId.of("Asia/Phnom_Penh"));
    }

}
