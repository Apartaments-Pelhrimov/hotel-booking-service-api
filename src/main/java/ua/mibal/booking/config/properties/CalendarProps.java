/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.fortuna.ical4j.model.property.ProdId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Validated
@ConfigurationProperties("calendar")
public record CalendarProps(
        ZoneId zoneId,
        ReservationDateTimeProps reservationDateTime,
        ICalProps iCal
) {

    @Validated
    @ConfigurationProperties("calendar.reservation-hours")
    public record ReservationDateTimeProps(

            @Min(0)
            @Max(23)
            Integer start,

            @Min(0)
            @Max(23)
            Integer end
    ) {
        public LocalTime reservationStart() {
            return LocalTime.of(start, 0);
        }

        public LocalTime reservationEnd() {
            return LocalTime.of(end, 0);
        }
    }

    @Validated
    @ConfigurationProperties("calendar.i-cal")
    public static final class ICalProps {
        @NotNull
        @NotBlank
        private final String prodId;

        public ICalProps(@NotNull @NotBlank String prodId) {
            this.prodId = prodId;
        }

        public ProdId prodId() {
            return new ProdId("-//" + prodId + "//iCal4j 1.0//EN");
        }
    }
}
