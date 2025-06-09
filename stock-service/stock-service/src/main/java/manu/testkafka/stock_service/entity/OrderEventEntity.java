package manu.testkafka.stock_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_events")
@Getter
@Setter
public class OrderEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventMessage;
    private String eventStatus;

    // storing the order data directly or as a JSON string
    @Lob // Use @Lob for potentially large JSON strings
    private String orderJson;
}
