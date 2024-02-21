/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.test.util;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.params.provider.Arguments;
import ua.mibal.booking.application.dto.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.dto.ChangeUserForm;
import ua.mibal.booking.application.dto.auth.RegistrationForm;
import ua.mibal.booking.application.dto.request.BedDto;
import ua.mibal.booking.application.dto.request.CreateApartmentDto;
import ua.mibal.booking.application.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.application.dto.request.PhotoDto;
import ua.mibal.booking.application.dto.request.PriceDto;
import ua.mibal.booking.application.dto.request.RoomDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentOptionsDto;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.ApartmentOptions;
import ua.mibal.booking.domain.Bed;
import ua.mibal.booking.domain.Comment;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.domain.Reservation;
import ua.mibal.booking.domain.ReservationDetails;
import ua.mibal.booking.domain.TurningOffTime;
import ua.mibal.booking.domain.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.unmodifiableList;
import static java.util.List.of;
import static java.util.function.Function.identity;
import static org.instancio.Select.field;
import static org.instancio.settings.Keys.BOOLEAN_NULLABLE;
import static org.instancio.settings.Keys.STRING_NULLABLE;
import static ua.mibal.booking.domain.Apartment.ApartmentClass.COMFORT;
import static ua.mibal.booking.domain.ApartmentOptions.DEFAULT;
import static ua.mibal.booking.domain.Room.Type.BEDROOM;
import static ua.mibal.booking.domain.Room.Type.LIVING_ROOM;

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
        return Stream.of(Arguments.of(new CreateApartmentDto(
                "correctName",
                COMFORT,
                DEFAULT,
                of(new PriceDto(1, valueOf(10000))),
                of(new RoomDto("correctRoomName", of(new BedDto(1, Bed.Type.TRANSFORMER)), LIVING_ROOM)),
                of()
        )));
    }

    public static Stream<Arguments> invalidApartmentDto() {
        Stream<Arguments> simpleArgs = Stream.of(
                // incorrect name
                Arguments.of(new CreateApartmentDto("", COMFORT, DEFAULT, of(), of(), of())),
                Arguments.of(new CreateApartmentDto(null, COMFORT, DEFAULT, of(), of(), of())),

                // incorrect type
                Arguments.of(new CreateApartmentDto("correct_name", null, DEFAULT, of(), of(), of())),

                // incorrect prices
                Arguments.of(new CreateApartmentDto("correct_name", COMFORT, DEFAULT, incorrectPrices(), of(), of())),


                // incorrect rooms
                Arguments.of(new CreateApartmentDto("correct_name", COMFORT, DEFAULT, of(), incorrectRooms(), of()))
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
                        "correct_name", COMFORT, DEFAULT, of(price), of(), of())
                ));
    }

    private static Stream<Arguments> invalidApartmentDtoWithInvalidPhotos() {
        return incorrectPhotos().stream()
                .map(photo -> Arguments.of(new CreateApartmentDto(
                        "correct_name", COMFORT, DEFAULT, of(), of(), of())
                ));
    }

    private static Stream<Arguments> invalidApartmentDtoWithInvalidRooms() {
        return incorrectRooms().stream()
                .map(room -> Arguments.of(new CreateApartmentDto(
                        "correct_name", COMFORT, DEFAULT, of(), of(room), of())
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
        List<RoomDto> rooms = new ArrayList<>(
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
                new BedDto(null, Bed.Type.CONNECTED),
                new BedDto(0, Bed.Type.CONNECTED),

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
                .set(field(Comment::getUser), null)
                .set(field(Comment::getApartment), null)
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

    public static RegistrationForm testRegistrationForm(String email) {
        return Instancio.of(RegistrationForm.class)
                .set(field(RegistrationForm::email), email)
                .generate(field(RegistrationForm::phone), gen -> gen.text().pattern("+#d#d#d#d#d#d#d#d#d#d#d#d#d#d"))
                .generate(field(RegistrationForm::password), gen -> gen.oneOf("password1", "password123", "qwerty1234Michael"))
                .create();
    }

    public static RegistrationForm invalidRegistrationForm() {
        return Instancio.of(RegistrationForm.class).create();
    }

    public static ApartmentInstance testApartmentInstanceWithoutReservation(String name) {
        return apartmentInstanceOf(name, of(), of());
    }

    public static ApartmentInstance testApartmentInstanceWithReservations(String name,
                                                                          List<Reservation> reservations) {
        return apartmentInstanceOf(name, of(), reservations);
    }

    private static ApartmentInstance apartmentInstanceOf(String name,
                                                         List<TurningOffTime> turningOffTimes,
                                                         List<Reservation> reservations) {
        return ApartmentInstance.of(null, name, null, null, turningOffTimes, reservations);
    }

    public static Reservation testReservationOf(LocalDateTime from, LocalDateTime to, User user) {
        ReservationDetails details = Instancio.of(ReservationDetails.class)
                .set(field(ReservationDetails::getFrom), from)
                .set(field(ReservationDetails::getTo), to)
                .create();
        return Instancio.of(Reservation.class)
                .set(field(Reservation::getId), null)
                .set(field(Reservation::getUser), user)
                .set(field(Reservation::getRejections), of())
                .set(field(Reservation::getDetails), details)
                .set(field(Reservation::getState), Reservation.State.PROCESSED)
                .create();
    }

    public static Apartment testApartmentWithPriceFor(int personCount) {
        Apartment apartment = testApartment();
        apartment.putPrice(new Price(personCount, BigDecimal.TEN));
        return apartment;
    }

    public static List<Reservation> testReservations(int count) {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var reservation = Instancio.of(Reservation.class).create();
            reservations.add(reservation);
        }
        return unmodifiableList(reservations);
    }

    public static Stream<Arguments> testUsers() {
        Settings settings = Settings.create()
                .set(Keys.STRING_NULLABLE, true);
        return Instancio.ofMap(User.class, ChangeUserForm.class)
                .size(50)
                .withSettings(settings)
                .create()
                .entrySet()
                .stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    public static Stream<Arguments> testNotificationSettings() {
        Settings settings = Settings.create()
                .set(BOOLEAN_NULLABLE, true);
        return Instancio.ofMap(NotificationSettings.class, ChangeNotificationSettingsForm.class)
                .size(50)
                .withSettings(settings)
                .create()
                .entrySet()
                .stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    public static Stream<Arguments> testApartments() {
        Settings settings = Settings.create()
                .set(STRING_NULLABLE, true)
                .set(BOOLEAN_NULLABLE, true);
        return Instancio.ofMap(Apartment.class, UpdateApartmentDto.class)
                .size(50)
                .withSettings(settings)
                .create()
                .entrySet()
                .stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    public static Stream<Arguments> testApartmentOptions() {
        Settings settings = Settings.create()
                .set(STRING_NULLABLE, true)
                .set(BOOLEAN_NULLABLE, true);
        return Instancio.ofMap(ApartmentOptions.class, UpdateApartmentOptionsDto.class)
                .size(50)
                .withSettings(settings)
                .create()
                .entrySet()
                .stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

}
