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

import org.instancio.Instancio;
import org.junit.jupiter.params.provider.Arguments;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.request.BedDto;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
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
import static java.util.List.of;
import static java.util.function.Function.identity;
import static org.instancio.Select.field;
import static ua.mibal.booking.model.entity.Apartment.ApartmentClass.COMFORT;
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

    public static Stream<Arguments> incorrectPriceForCalculation() {
        LocalDate first = LocalDate.of(2023, 12, 1);
        LocalDate fifth = LocalDate.of(2023, 12, 5);
        return Stream.of(
                Arguments.of(valueOf(-100_000), first, fifth),
                Arguments.of(valueOf(-1), first, fifth)
        );
    }

    public static Stream<Arguments> incorrectDateRangeForCalculation() {
        LocalDate first = LocalDate.of(2023, 12, 1);
        LocalDate fifth = LocalDate.of(2023, 12, 5);
        return Stream.of(
                Arguments.of(ZERO, fifth, fifth),
                Arguments.of(ONE, fifth, first)
        );
    }

    public static Stream<Arguments> validApartmentDto() {
        return Stream.of(Arguments.of(new CreateApartmentDto("correctName", COMFORT, DEFAULT,
                of(new PriceDto(1, valueOf(10000))),
                of(new PhotoDto("https://apple.com")),
                of(new RoomDto("correctRoomName", of(new BedDto(1, Bed.BedType.TRANSFORMER)), LIVING_ROOM)),
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
                        .map(bed -> new RoomDto("correctRoomName", of(bed), BEDROOM))
                        .toList()
        );
        rooms.add(new RoomDto("correctRoomName", of(), null));
        rooms.add(new RoomDto("", of(), BEDROOM)); // incorrect names
        rooms.add(new RoomDto("b", of(), BEDROOM));
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
        return Instancio.of(User.class)
                .set(field(User::getId), null)
                .set(field(User::getComments), null)
                .set(field(User::getReservations), null)
                .create();
    }

    public static Comment testComment() {
        return Instancio.of(Comment.class)
                .set(field(Comment::getId), null)
                .create();
    }

    public static Apartment testApartment() {
        return Instancio.of(Apartment.class)
                .set(field(Apartment::getId), null)
                .set(field(Apartment::getComments), null)
                .set(field(Apartment::getApartmentInstances), null)
                .create();
    }

    public static Reservation testReservation() {
        return Instancio.of(Reservation.class)
                .set(field(Reservation::getId), null)
                .set(field(Reservation::getRejections), null)
                .create();
    }

    public static ApartmentInstance testApartmentInstance() {
        return Instancio.of(ApartmentInstance.class)
                .set(field(ApartmentInstance::getId), null)
                .create();
    }

    public static Stream<Arguments> invalidCreateApartmentInstanceDto() {
        return Stream.of(
                Arguments.of(new CreateApartmentInstanceDto("na", null)),
                Arguments.of(new CreateApartmentInstanceDto("       ", null)),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "invalid.com")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "http://invalid")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "https://invalid")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "https://invalid/fjdskl")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "https://invalid.com\\"))
        );
    }

    public static Stream<Arguments> validCreateApartmentInstanceDto() {
        return Stream.of(
                Arguments.of(new CreateApartmentInstanceDto("correct_name ", null)),
                Arguments.of(new CreateApartmentInstanceDto("correct name", null)),
                Arguments.of(new CreateApartmentInstanceDto("aaa", null)),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "https://valid.com")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "https://valid.com/page")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "http://valid.com")),
                Arguments.of(new CreateApartmentInstanceDto("correct_name", "http://valid.com/page"))
        );
    }

    public static RegistrationDto testRegistrationDto(String email) {
        return Instancio.of(RegistrationDto.class)
                .set(field(RegistrationDto::email), email)
                .generate(field(RegistrationDto::phone), gen -> gen.text().pattern("+#d#d#d#d#d#d#d#d#d#d#d#d#d#d"))
                .generate(field(RegistrationDto::password), gen -> gen.oneOf("password1", "password123", "qwerty1234Michael"))
                .create();
    }

    public static RegistrationDto invalidRegistrationDto() {
        return Instancio.of(RegistrationDto.class).create();
    }
}
