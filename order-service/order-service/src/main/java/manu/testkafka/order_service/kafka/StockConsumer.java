package manu.testkafka.order_service.kafka;

import manu.testkafka.base_domains.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.stock_topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderEvent event) {
        LOGGER.info("Order status event received in order-service => {}", event.toString());

        // Here you would typically update the order status in the order-service's own database
        // For example: find order by event.getOrder().getOrderId() and set its status to event.getStatus()
        LOGGER.info("Updating status for order ID: {} to {}", event.getOrder().getOrderId(), event.getStatus());
    }
}
