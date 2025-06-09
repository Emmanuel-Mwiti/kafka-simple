package manu.testkafka.stock_service.kafka;

import manu.testkafka.base_domains.dto.OrderEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class StockProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockProducer.class);

    @Value("${spring.kafka.topic.stock_topic}")
    private String stockTopicName;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public StockProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(OrderEvent orderEvent) {
        LOGGER.info("Publishing stock check result event => {}", orderEvent.toString());

        Message<OrderEvent> message = MessageBuilder
                .withPayload(orderEvent)
                .setHeader(KafkaHeaders.TOPIC, stockTopicName)
                .build();
        kafkaTemplate.send(message);
    }
}
