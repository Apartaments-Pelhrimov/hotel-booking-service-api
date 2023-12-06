package ua.mibal.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.mibal.booking.model.entity.HotelTurningOffTime;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface HotelTurningOffRepository extends JpaRepository<HotelTurningOffTime, Long> {

    @Query("""
            select htot
                from HotelTurningOffTime htot
            where
                htot.from < ?2 and htot.to > ?1
            """)
    List<HotelTurningOffTime> findBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select htot
                from HotelTurningOffTime htot
            where htot.to > now()
            """)
    List<HotelTurningOffTime> findFromNow();
}
