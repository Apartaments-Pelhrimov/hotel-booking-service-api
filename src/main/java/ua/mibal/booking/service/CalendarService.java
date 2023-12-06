package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

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
                dateTimeUtils.monthStart(yearMonth),
                dateTimeUtils.monthEnd(yearMonth)
        );
    }

    private void validateApartmentExists(Long apartmentId) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
    }
}
