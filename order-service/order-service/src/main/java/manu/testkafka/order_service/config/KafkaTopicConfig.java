package manu.testkafka.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic.order_topic}")
    private String orderTopicName;

    @Value("${spring.kafka.topic.stock_topic}")
    private String stockTopicName;

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder
                .name(orderTopicName)
                .build();
    }

    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder
                .name(stockTopicName)
                .build();
    }
}