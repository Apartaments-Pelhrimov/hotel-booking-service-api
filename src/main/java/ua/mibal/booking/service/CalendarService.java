package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ApartmentRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
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
    private final ICalService iCalService;

    public List<Calendar> getCalendarsForApartment(Long apartmentId, YearMonth yearMonth) {
        validateApartmentId(apartmentId);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        List<ApartmentInstance> apartmentInstances =
                apartmentRepository.findByApartmentIdBetweenFetchReservations(apartmentId, start, end);
        return reservationMapper.toCalendarList(apartmentInstances);
    }

    public Calendar getCalendarForApartmentInstance(Long apartmentInstanceId, YearMonth yearMonth) {
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        ApartmentInstance apartmentInstance =
                apartmentRepository.findByApartmentInstanceIdBetweenFetchReservations(apartmentInstanceId, start, end)
                        .orElseThrow(() -> new ApartmentInstanceNotFoundException(apartmentInstanceId));
        return reservationMapper.toCalendar(apartmentInstance);
    }

    private void validateApartmentId(Long apartmentId) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
    }

    public String getICalForApartmentInstance(Long apartmentInstanceId) {
        List<Reservation> reservations = apartmentRepository
                .findByApartmentInstanceIdFetchReservations(apartmentInstanceId)
                .map(ApartmentInstance::getReservations)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(apartmentInstanceId));
        return iCalService.calendarFromReservations(reservations)
                .toString();
    }
}
