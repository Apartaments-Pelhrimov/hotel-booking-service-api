package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.time.LocalDateTime.now;
import static org.apache.commons.collections4.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ReservationMapper reservationMapper;
    private final ApartmentRepository apartmentRepository;
    private final HotelTurningOffRepository hotelTurningOffRepository;
    private final ICalService iCalService;
    private final DateTimeUtils dateTimeUtils;

    public List<Calendar> getCalendarsForApartment(Long apartmentId, YearMonth yearMonth) {
        List<ApartmentInstance> apartmentInstances = apartmentInstancesByIdAndMonth(apartmentId, yearMonth);
        List<HotelTurningOffTime> hotelTurningOffTimes = hotelTurningOffTimes(yearMonth);
        return reservationMapper.toCalendarList(apartmentInstances, hotelTurningOffTimes);
    }

    public Calendar getCalendarForApartmentInstance(Long apartmentInstanceId, YearMonth yearMonth) {
        ApartmentInstance apartmentInstance = apartmentInstanceByIdAndMonth(apartmentInstanceId, yearMonth);
        List<HotelTurningOffTime> hotelTurningOffTimes = hotelTurningOffTimes(yearMonth);
        return reservationMapper.toCalendar(apartmentInstance, hotelTurningOffTimes);
    }

    @Transactional(readOnly = true)
    public String getICalForApartmentInstance(Long apartmentInstanceId) {
        Collection<Event> events = eventsForNowByInstanceId(apartmentInstanceId);
        return iCalService.calendarFromEvents(events).toString();
    }

    private ApartmentInstance apartmentInstanceByIdAndMonth(Long apartmentInstanceId, YearMonth yearMonth) {
        return apartmentRepository.findByApartmentInstanceIdBetweenFetchReservations(
                        apartmentInstanceId,
                        dateTimeUtils.monthStartWithTime(yearMonth),
                        dateTimeUtils.monthEndWithTime(yearMonth)
                )
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(apartmentInstanceId));
    }

    private List<ApartmentInstance> apartmentInstancesByIdAndMonth(Long apartmentId, YearMonth yearMonth) {
        return apartmentRepository.findByIdBetweenFetchInstancesAndReservations(
                        apartmentId,
                        dateTimeUtils.monthStartWithTime(yearMonth),
                        dateTimeUtils.monthEndWithTime(yearMonth)
                )
                .map(Apartment::getApartmentInstances)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
    }

    private Collection<Event> eventsForNowByInstanceId(Long instanceId) {
        Collection<Event> apartmentEvents = apartmentEventsForNow(instanceId);
        List<HotelTurningOffTime> hotelTurningOffTimes = hotelTurningOffRepository.findFromNow();
        return union(apartmentEvents, hotelTurningOffTimes);
    }

    private Collection<Event> apartmentEventsForNow(Long instanceId) {
        ApartmentInstance apartmentInstance = apartmentRepository
                .findInstanceByIdFetchReservations(instanceId)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(instanceId));
        List<Reservation> reservations = apartmentInstance.getReservations();
        List<TurningOffTime> turningOffTimes = apartmentInstance.getTurningOffTimes();
        return filterForActuality(union(reservations, turningOffTimes));
    }

    private Collection<Event> filterForActuality(Collection<Event> collection) {
        Predicate<Event> isActual = e -> e.getEnd().isAfter(now());
        return collection.stream()
                .filter(isActual)
                .toList();
    }

    private List<HotelTurningOffTime> hotelTurningOffTimes(YearMonth yearMonth) {
        return hotelTurningOffRepository.findBetween(
                dateTimeUtils.monthStart(yearMonth),
                dateTimeUtils.monthEnd(yearMonth)
        );
    }
}
