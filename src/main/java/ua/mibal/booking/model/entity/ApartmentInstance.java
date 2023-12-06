package ua.mibal.booking.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
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
        name = "apartment_instances",
        indexes = @Index(name = "apartment_instances_apartment_id_idx", columnList = "apartment_id")
)
public class ApartmentInstance {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String bookingIcalId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "apartment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "apartment_instances_apartment_id_fk")
    )
    private Apartment apartment;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_instances_turning_off_times",
            joinColumns = @JoinColumn(
                    name = "apartment_instance_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_instances_turning_off_times_apartment_instance_id_fk")
            ),
            indexes = @Index(
                    name = "apartment_instances_turning_off_times_apartment_instance_id_idx",
                    columnList = "apartment_instance_id"
            ))
    @Setter(PRIVATE)
    private List<TurningOffTime> turningOffTimes = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentInstance")
    @Setter(PRIVATE)
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservation.setApartmentInstance(this);
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            reservation.setApartmentInstance(null);
        }
    }

    public void addTurningOffTime(TurningOffTime turningOffTime) {
        turningOffTime.setApartmentInstance(this);
        this.turningOffTimes.add(turningOffTime);
    }
}
