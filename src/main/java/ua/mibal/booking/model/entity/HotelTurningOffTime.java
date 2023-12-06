package ua.mibal.booking.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel_turning_off_times")
public class HotelTurningOffTime implements Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "\"from\"")
    private LocalDateTime from;

    @Column(nullable = false, name = "\"to\"")
    private LocalDateTime to;

    @Column
    private String name;

    @Override
    public LocalDateTime getStart() {
        return from;
    }

    @Override
    public LocalDateTime getEnd() {
        return to;
    }

    @Override
    public String getEventName() {
        return "Hotel turned off. Reason: " + Optional.ofNullable(name).orElse("");
    }
}
