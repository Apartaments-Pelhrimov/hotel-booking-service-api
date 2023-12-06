package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.TurnOffDto;
import ua.mibal.booking.model.dto.response.calendar.Calendar;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;
import ua.mibal.booking.model.exception.IllegalTurningOffTimeException;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.now;
import static org.apache.commons.collections4.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ApartmentRepository apartmentRepository;
    private final HotelTurningOffRepository hotelTurningOffRepository;
    private final ReservationRepository reservationRepository;
    private final ICalService iCalService;
    private final DateTimeUtils dateTimeUtils;

    @Transactional(readOnly = true)
    public List<Calendar> getCalendarsForApartment(Long apartmentId, YearMonth yearMonth) {
        validateApartmentExists(apartmentId);
        List<ApartmentInstance> instances = apartmentRepository
                .findInstancesByApartmentIdFetchReservations(apartmentId);
        List<? extends Event> hotelEvents = hotelTurningOffTimes(yearMonth);
        return instancesToCalendars(instances, hotelEvents, yearMonth);
    }

    @Transactional(readOnly = true)
    public Calendar getCalendarForApartmentInstance(Long instanceId, YearMonth yearMonth) {
        ApartmentInstance instance = apartmentRepository.findInstanceByIdFetchReservations(instanceId)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(instanceId));
        List<? extends Event> hotelEvents = hotelTurningOffTimes(yearMonth);
        return instanceToCalendar(instance, hotelEvents, yearMonth);
    }

    @Transactional(readOnly = true)
    public String getICalForApartmentInstance(Long instanceId) {
        ApartmentInstance apartmentInstance = apartmentRepository.findInstanceByIdFetchReservations(instanceId)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(instanceId));
        List<? extends Event> hotelEvents = hotelTurningOffRepository.findFromNow();
        return instanceToICal(apartmentInstance, hotelEvents);
    }

    public void turnOffHotel(TurnOffDto turnOffDto) {
        validateRangeToTurnOffHotel(turnOffDto.from(), turnOffDto.to());
        HotelTurningOffTime turningOffTime = hotelTurningOffFromDto(turnOffDto);
        hotelTurningOffRepository.save(turningOffTime);
    }

    @Transactional
    public void turnOffApartmentInstance(Long id, TurnOffDto turnOffDto) {
        ApartmentInstance instance = apartmentRepository.findInstanceByIdFetchReservations(id)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(id));
        validateRangeToTurnOffApartmentInstance(instance, turnOffDto.from(), turnOffDto.to());
        TurningOffTime turningOffTime = turningOffTimeFromDto(turnOffDto);
        instance.addTurningOffTime(turningOffTime);
    }

    private List<Calendar> instancesToCalendars(List<ApartmentInstance> instances,
                                                List<? extends Event> hotelEvents,
                                                YearMonth yearMonth) {
        return instances.stream()
                .map(instance -> instanceToCalendar(instance, hotelEvents, yearMonth))
                .toList();
    }

    private Calendar instanceToCalendar(ApartmentInstance apartmentInstance,
                                        List<? extends Event> hotelEvents,
                                        YearMonth yearMonth) {
        Collection<Event> apartmentEvents = apartmentEventsForDateRange(
                apartmentInstance,
                dateTimeUtils.monthStartWithTime(yearMonth),
                dateTimeUtils.monthEndWithTime(yearMonth)
        );
        return Calendar.of(union(apartmentEvents, hotelEvents));
    }

    private String instanceToICal(ApartmentInstance apartmentInstance,
                                  List<? extends Event> hotelEvents) {
        Collection<Event> apartmentEvents = apartmentEventsForDateRange(
                apartmentInstance, now(), MAX
        );
        return iCalService.calendarFromEvents(
                union(apartmentEvents, hotelEvents)
        ).toString();
    }

    private Collection<Event> apartmentEventsForDateRange(ApartmentInstance apartmentInstance,
                                                          LocalDateTime start,
                                                          LocalDateTime end) {
        return apartmentEvents(apartmentInstance).stream()
                .filter(ev -> ev.getEnd().isAfter(start) &&
                              ev.getStart().isBefore(end))
                .toList();
    }

    private Collection<Event> apartmentEvents(ApartmentInstance apartmentInstance) {
        List<Reservation> reservations = apartmentInstance.getReservations();
        List<TurningOffTime> turningOffTimes = apartmentInstance.getTurningOffTimes();
        return union(reservations, turningOffTimes);
    }

    private List<HotelTurningOffTime> hotelTurningOffTimes(YearMonth yearMonth) {
        return hotelTurningOffRepository.findBetween(
                dateTimeUtils.monthStartWithTime(yearMonth),
                dateTimeUtils.monthEndWithTime(yearMonth)
        );
    }

    private void validateApartmentExists(Long apartmentId) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
    }

    private HotelTurningOffTime hotelTurningOffFromDto(TurnOffDto turnOffDto) {
        return HotelTurningOffTime.builder()
                .from(turnOffDto.from())
                .to(turnOffDto.to())
                .event(turnOffDto.event())
                .build();
    }

    private TurningOffTime turningOffTimeFromDto(TurnOffDto turnOffDto) {
        return TurningOffTime.builder()
                .from(turnOffDto.from())
                .to(turnOffDto.to())
                .build();
    }

    private void validateRangeToTurnOffApartmentInstance(ApartmentInstance instance,
                                                         LocalDateTime start,
                                                         LocalDateTime end) {
        Predicate<Reservation> intersectsWithRangeCondition =
                r -> r.getDetails().getReservedTo().isAfter(start) &&
                     r.getDetails().getReservedFrom().isBefore(end);
        if (instance.getReservations().stream()
                .anyMatch(intersectsWithRangeCondition))
            throw new IllegalTurningOffTimeException(start, end);
    }

    private void validateRangeToTurnOffHotel(LocalDateTime start, LocalDateTime end) {
        if (reservationRepository.existsReservationThatIntersectRange(start, end))
            throw new IllegalTurningOffTimeException(start, end);
    }
}
