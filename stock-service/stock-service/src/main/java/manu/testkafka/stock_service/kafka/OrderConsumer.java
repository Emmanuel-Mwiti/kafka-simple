package manu.testkafka.stock_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import manu.testkafka.base_domains.dto.Order;
import manu.testkafka.base_domains.dto.OrderEvent;
import manu.testkafka.stock_service.entity.OrderEventEntity;
import manu.testkafka.stock_service.entity.Product;
import manu.testkafka.stock_service.repository.OrderEventRepository;
import manu.testkafka.stock_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private final ProductRepository productRepository;
    private final StockProducer stockProducer;
    // The OrderEventRepository can be used for auditing/logging events if desired
    // private final OrderEventRepository orderEventRepository;

    public OrderConsumer(ProductRepository productRepository, StockProducer stockProducer) {
        this.productRepository = productRepository;
        this.stockProducer = stockProducer;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.order_topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(OrderEvent event) {
        LOGGER.info("Order event received in stock service => {}", event.toString());

        if (!"CREATED".equals(event.getStatus())) {
            LOGGER.warn("Received event with unexpected status: {}. Ignoring.", event.getStatus());
            return;
        }

        Order order = event.getOrder();
        Optional<Product> productOptional = productRepository.findById(order.getProductId());

        OrderEvent stockResultEvent = new OrderEvent();
        stockResultEvent.setOrder(order);

        if (productOptional.isPresent() && productOptional.get().getQuantity() >= order.getQty()) {
            // Stock is available
            Product product = productOptional.get();
            product.setQuantity(product.getQuantity() - order.getQty());
            productRepository.save(product);

            stockResultEvent.setStatus("CONFIRMED");
            stockResultEvent.setMessage("Stock confirmed and reserved for order.");
            LOGGER.info("Stock sufficient for product {}. Updated quantity: {}", product.getProductId(), product.getQuantity());

        } else {
            // Stock is not available
            stockResultEvent.setStatus("REJECTED");
            if (productOptional.isPresent()) {
                stockResultEvent.setMessage(String.format("Stock insufficient for product %s. Required: %d, Available: %d.",
                        order.getProductId(), order.getQty(), productOptional.get().getQuantity()));
            } else {
                stockResultEvent.setMessage(String.format("Product with ID %s not found in stock.", order.getProductId()));
            }
            LOGGER.warn("Stock check failed: {}", stockResultEvent.getMessage());
        }

        // Publish the result to the stock topic
        stockProducer.sendMessage(stockResultEvent);
    }
}
