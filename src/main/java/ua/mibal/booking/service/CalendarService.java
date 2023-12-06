package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Event;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.repository.ReservationRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

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
    private final ReservationRepository reservationRepository;

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

    private Collection<Event> eventsForNowByInstanceId(Long apartmentInstanceId) {
        List<Reservation> reservations = reservationsByInstanceId(apartmentInstanceId);
        List<HotelTurningOffTime> hotelTurningOffTimes = hotelTurningOffRepository.findFromNow();
        return CollectionUtils.union(reservations, hotelTurningOffTimes);
    }

    private List<Reservation> reservationsByInstanceId(Long apartmentInstanceId) {
        validateApartmentInstanceExists(apartmentInstanceId);
        return reservationRepository.findAllByApartmentInstanceIdForNowFetchApartmentInstance(apartmentInstanceId);
    }

    private void validateApartmentInstanceExists(Long instanceId) {
        if (!apartmentRepository.instanceExistsById(instanceId))
            throw new ApartmentInstanceNotFoundException(instanceId);
    }

    private List<HotelTurningOffTime> hotelTurningOffTimes(YearMonth yearMonth) {
        return hotelTurningOffRepository.findBetween(
                dateTimeUtils.monthStart(yearMonth),
                dateTimeUtils.monthEnd(yearMonth)
        );
    }
}
