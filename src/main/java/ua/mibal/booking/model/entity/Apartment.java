package ua.mibal.booking.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "apartments",
        indexes = @Index(name = "apartments_apartment_type_id_idx", columnList = "apartment_type_id")
)
public class Apartment {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "apartment_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "apartments_apartment_type_id_fk")
    )
    private ApartmentType apartmentType;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartments_turning_off_times",
            joinColumns = @JoinColumn(
                    name = "apartment_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartments_turning_off_times_apartment_id_fk")
            ),
            indexes = @Index(
                    name = "apartments_turning_off_times_apartment_id_idx",
                    columnList = "apartment_id"
            ))
    @Setter(PRIVATE)
    private List<TurningOffTime> turningOffTimes = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Setter(PRIVATE)
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservation.setApartment(this);
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            reservation.setApartment(null);
        }
    }
}
