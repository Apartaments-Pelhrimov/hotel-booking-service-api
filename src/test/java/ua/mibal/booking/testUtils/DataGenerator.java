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

package ua.mibal.booking.testUtils;

import org.junit.jupiter.params.provider.Arguments;
import ua.mibal.booking.model.dto.request.BedDto;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.PhotoDto;
import ua.mibal.booking.model.dto.request.PriceDto;
import ua.mibal.booking.model.dto.request.RoomDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Comment;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static java.util.function.Function.identity;
import static ua.mibal.booking.model.entity.Apartment.ApartmentClass.COMFORT;
import static ua.mibal.booking.model.entity.Apartment.ApartmentClass.STANDARD;
import static ua.mibal.booking.model.entity.Room.RoomType.BEDROOM;
import static ua.mibal.booking.model.entity.Room.RoomType.LIVING_ROOM;
import static ua.mibal.booking.model.entity.embeddable.ApartmentOptions.DEFAULT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class DataGenerator {

    /**
     * @return random events
     */
    public static List<Event> randomEvents() {
        LocalDateTime first = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime twentyFifth = LocalDate.of(2023, 12, 25).atStartOfDay();
        LocalDateTime birthday = LocalDate.of(2004, 9, 18).atStartOfDay();
        return of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @param targetZoneId target wanted to convert {@link ZoneId}
     * @return hardcoded events from {@code classpath:test.ics}
     */
    public static List<Event> testEventsFromTestFile(ZoneId targetZoneId) {
        ZoneId australia = ZoneId.of("Australia/Sydney");
        LocalDateTime first = timeAtToOurZoneId(
                LocalDate.of(2024, 1, 1).atStartOfDay(), australia, targetZoneId);
        LocalDateTime twentyFifth = timeAtToOurZoneId(
                LocalDate.of(2023, 12, 25).atStartOfDay(), ZoneOffset.UTC, targetZoneId);
        LocalDateTime birthday = timeAtToOurZoneId(
                LocalDate.of(2004, 9, 18).atStartOfDay(), australia, targetZoneId);
        return of(
                Event.from(first, first, "New Year"),
                Event.from(twentyFifth, first, "Christmas holidays"),
                Event.from(birthday, birthday, "My Birthday")
        );
    }

    /**
     * @return random {@link List} of {@link Event} Apartment reservations and
     * user intent reservation intervals with expected result of operation
     */
    public static Stream<Arguments> eventsFactory() {
        LocalDateTime first = LocalDate.of(2023, 12, 1).atStartOfDay();
        LocalDateTime third = LocalDate.of(2023, 12, 3).atStartOfDay();
        LocalDateTime fifth = LocalDate.of(2023, 12, 5).atStartOfDay();
        LocalDateTime sixth = LocalDate.of(2023, 12, 6).atStartOfDay();
        return Stream.of(
                Arguments.of(of(), first, fifth, true),
                Arguments.of(of(Event.from(first, third, "")), fifth, sixth, true),
                Arguments.of(of(Event.from(first, fifth, "")), first, fifth, false),
                Arguments.of(of(Event.from(first, third, "")), first, fifth, false),
                Arguments.of(of(Event.from(third, fifth, "")), first, fifth, false)
        );
    }

    public static Stream<Arguments> correctPriceCalculation() {
        LocalDate first = LocalDate.of(2023, 12, 1);
        LocalDate second = LocalDate.of(2023, 12, 2);
        LocalDate fifth = LocalDate.of(2023, 12, 5);
        LocalDate sixth = LocalDate.of(2023, 12, 6);
        return Stream.of(
                Arguments.of(ZERO, first, fifth, ZERO),
                Arguments.of(ONE, first, fifth, valueOf(4)),
                Arguments.of(valueOf(100_000), first, sixth, valueOf(500_000)),
                Arguments.of(valueOf(100_000), first, second, valueOf(100_000))
        );
    }

    public static Stream<Arguments> incorrectPriceCalculation() {
        LocalDate first = LocalDate.of(2023, 12, 1);
        LocalDate fifth = LocalDate.of(2023, 12, 5);
        return Stream.of(
                Arguments.of(ZERO, fifth, fifth),
                Arguments.of(ONE, fifth, first),
                Arguments.of(valueOf(-100_000), first, fifth)
        );
    }

    public static Stream<Arguments> validApartmentDto() {
        return Stream.of(Arguments.of(new CreateApartmentDto("correctName", COMFORT, DEFAULT,
                of(new PriceDto(1, valueOf(10000))),
                of(new PhotoDto("https://apple.com")),
                of(new RoomDto(of(new BedDto(1, Bed.BedType.TRANSFORMER)), LIVING_ROOM)),
                of()
        )));
    }

    public static Stream<Arguments> invalidApartmentDto() {
        Stream<Arguments> simpleArgs = Stream.of(
                // incorrect name
                Arguments.of(new CreateApartmentDto("", COMFORT, DEFAULT, of(), of(), of(), of())),
                Arguments.of(new CreateApartmentDto(null, COMFORT, DEFAULT, of(), of(), of(), of())),

                // incorrect type
                Arguments.of(new CreateApartmentDto("correct_name", null, DEFAULT, of(), of(), of(), of())),

                // incorrect prices
                Arguments.of(new CreateApartmentDto("correct_name", COMFORT, DEFAULT, incorrectPrices(), of(), of(), of())),


                // incorrect rooms
                Arguments.of(new CreateApartmentDto("correct_name", COMFORT, DEFAULT, of(), of(), incorrectRooms(), of()))
        );
        return of(
                simpleArgs,
                invalidApartmentDtoWithInvalidPrices(),
                invalidApartmentDtoWithInvalidPhotos(),
                invalidApartmentDtoWithInvalidRooms()
        ).parallelStream()
                .flatMap(identity());
    }

    private static Stream<Arguments> invalidApartmentDtoWithInvalidPrices() {
        return incorrectPrices().stream()
                .map(price -> Arguments.of(new CreateApartmentDto(
                        "correct_name", COMFORT, DEFAULT, of(price), of(), of(), of())
                ));
    }

    private static Stream<Arguments> invalidApartmentDtoWithInvalidPhotos() {
        return incorrectPhotos().stream()
                .map(photo -> Arguments.of(new CreateApartmentDto(
                        "correct_name", COMFORT, DEFAULT, of(), of(photo), of(), of())
                ));
    }

    private static Stream<Arguments> invalidApartmentDtoWithInvalidRooms() {
        return incorrectRooms().stream()
                .map(room -> Arguments.of(new CreateApartmentDto(
                        "correct_name", COMFORT, DEFAULT, of(), of(), of(room), of())
                ));
    }

    public static List<PriceDto> incorrectPrices() {
        return of(
                // incorrect person number
                new PriceDto(null, ZERO),
                new PriceDto(-1, ZERO),
                new PriceDto(0, valueOf(100_000)),

                // incorrect cost
                new PriceDto(1, null),
                new PriceDto(1, valueOf(-1)),
                new PriceDto(1, valueOf(100_001))
        );
    }

    public static List<PhotoDto> incorrectPhotos() {
        return of(
                new PhotoDto(null),
                new PhotoDto(""),
                new PhotoDto("invalid.com"),
                new PhotoDto("http://invalid"),
                new PhotoDto("https://invalid"),
                new PhotoDto("https://invalid/fjdskl"),
                new PhotoDto("https://invalid.com\\")
        );
    }

    public static List<RoomDto> incorrectRooms() {
        List<RoomDto> rooms = new java.util.ArrayList<>(
                incorrectBeds().stream()
                        .map(bed -> new RoomDto(of(bed), BEDROOM))
                        .toList()
        );
        rooms.add(new RoomDto(of(), null));
        return rooms;
    }

    public static List<BedDto> incorrectBeds() {
        return of(
                new BedDto(null, Bed.BedType.CONNECTED),
                new BedDto(0, Bed.BedType.CONNECTED),

                new BedDto(1, null)
        );
    }

    private static LocalDateTime timeAtToOurZoneId(LocalDateTime localDateTime, ZoneId original, ZoneId wanted) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, original);
        return zonedDateTime.toOffsetDateTime().atZoneSameInstant(wanted).toLocalDateTime();
    }

    public static User testUser() {
        User user = new User(
                "Test",
                "User",
                "test@mail.com",
                "test",
                "+380951234567"
        );
        user.setPhoto(new Photo("test"));
        return user;
    }

    public static Comment testComment() {
        return new Comment(
                "Test body",
                1.7
        );
    }

    public static Apartment testApartment() {
        return new Apartment(
                "Test Apartment",
                STANDARD
        );
    }

    public static Reservation testReservation() {
        Reservation reservation = new Reservation();
        reservation.setDateTime(now());
        ReservationDetails reservationDetails = new ReservationDetails(
                now().minusDays(1), now(),
                valueOf(10_000), new Price(1, valueOf(10_000))
        );
        reservation.setDetails(reservationDetails);
        return reservation;
    }

    public static ApartmentInstance testApartmentInstance() {
        ApartmentInstance instance = new ApartmentInstance();
        instance.setName("Name");
        instance.setBookingICalUrl("link://");
        return instance;
    }
}
