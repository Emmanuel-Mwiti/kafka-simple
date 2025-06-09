package manu.testkafka.email_service.kafka;

import manu.testkafka.base_domains.dto.Order;
import manu.testkafka.base_domains.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private final JavaMailSender mailSender;

    public OrderConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderEvent event) {
        LOGGER.info("Stock result event received in email service => {}", event.toString());

        String subject;
        String body;

        // Assume a customer email address is available, e.g., from the order details
        // For this example, we'll use a hardcoded address.
        String toEmail = "kiuguemmanu@gmail.com";

        if ("CONFIRMED".equals(event.getStatus())) {
            subject = "Your Order is Confirmed!";
            body = buildConfirmationEmailBody(event.getOrder());
            LOGGER.info("Sending confirmation email for Order ID: {}", event.getOrder().getOrderId());
        } else if ("REJECTED".equals(event.getStatus())) {
            subject = "There was a problem with your order";
            body = buildRejectionEmailBody(event);
            LOGGER.warn("Sending rejection email for Order ID: {}", event.getOrder().getOrderId());
        } else {
            LOGGER.warn("Received event with unknown status '{}'. No email will be sent.", event.getStatus());
            return;
        }

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@mykafkashop.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            LOGGER.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            LOGGER.error("Failed to send email", e);
        }
    }

    private String buildConfirmationEmailBody(Order order) {
        return String.format(
                "Dear Customer,\n\n" +
                        "Thank you for your purchase! Your order has been confirmed.\n\n" +
                        "Order Details:\n" +
                        "  Order ID: %s\n" +
                        "  Product ID: %s\n" +
                        "  Quantity: %d\n" +
                        "  Total Price: $%.2f\n\n" +
                        "We are preparing your order for shipment.\n\n" +
                        "Best regards,\n" +
                        "The Kafka Shop Team",
                order.getOrderId(),
                order.getProductId(),
                order.getQty(),
                order.getPrice()
        );
    }

    private String buildRejectionEmailBody(OrderEvent event) {
        return String.format(
                "Dear Customer,\n\n" +
                        "We are sorry, but we were unable to process your recent order (ID: %s).\n\n" +
                        "Reason: %s\n\n" +
                        "Please visit our store again later or contact customer support for assistance.\n\n" +
                        "Best regards,\n" +
                        "The Kafka Shop Team",
                event.getOrder().getOrderId(),
                event.getMessage()
        );
    }
}

