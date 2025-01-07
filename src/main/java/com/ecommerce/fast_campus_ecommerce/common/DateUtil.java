package com.ecommerce.fast_campus_ecommerce.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
    public static Date convertLocalDateTimeToDate(LocalDateTime time) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = time.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }
}
