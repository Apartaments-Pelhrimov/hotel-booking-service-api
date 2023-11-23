package ua.mibal.booking.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ua.mibal.booking.model.entity.embeddable.Bed;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "beds",
            joinColumns = @JoinColumn(
                    name = "room_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "beds_room_id_fk")
            ))
    @Setter(PRIVATE)
    private List<Bed> beds = new LinkedList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "apartment_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "rooms_apartment_type_id_fk")
    )
    private ApartmentType apartmentType;

    public enum RoomType {
        BEDROOM, LIVING_ROOM, MEETING_ROOM
    }
}
