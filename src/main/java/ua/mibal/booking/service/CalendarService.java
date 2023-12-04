package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.ApartmentInstance;
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

    public List<Calendar> getCalendarsForApartment(Long apartmentId, YearMonth yearMonth) {
        validateApartmentId(apartmentId);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        List<ApartmentInstance> apartmentInstances =
                apartmentRepository.findByApartmentIdBetween(apartmentId, start, end);
        return reservationMapper.toCalendarList(apartmentInstances);
    }

    public Calendar getCalendarForApartmentInstance(Long apartmentInstanceId, YearMonth yearMonth) {
        validateApartmentInstanceId(apartmentInstanceId);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        ApartmentInstance apartmentInstance =
                apartmentRepository.findByApartmentInstanceIdBetween(apartmentInstanceId, start, end);
        return reservationMapper.toCalendar(apartmentInstance);
    }

    private void validateApartmentId(Long apartmentId) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
    }

    private void validateApartmentInstanceId(Long apartmentInstanceId) {
        if (!apartmentRepository.instanceExistsById(apartmentInstanceId))
            throw new ApartmentInstanceNotFoundException(apartmentInstanceId);
    }
}
