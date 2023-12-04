package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.model.dto.response.Calendar;
import ua.mibal.booking.model.entity.Reservation;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ReservationMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ReservationCalendarService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ApartmentRepository apartmentRepository;

    public Calendar getCalendarForApartment(Long apartmentId, YearMonth yearMonth) {
        validate(apartmentId);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        List<Reservation> reservations = reservationRepository.findByIdBetween(apartmentId, start, end);
        return reservationMapper.toCalendar(reservations);
    }

    private void validate(Long apartmentId) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
    }
}
