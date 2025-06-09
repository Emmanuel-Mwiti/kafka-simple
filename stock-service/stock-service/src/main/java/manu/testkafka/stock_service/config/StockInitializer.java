package manu.testkafka.stock_service.config;

import manu.testkafka.stock_service.entity.Product;
import manu.testkafka.stock_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StockInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public StockInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        productRepository.deleteAll();

        // Add some sample products
        productRepository.save(new Product("PROD-001", "Laptop", 10));
        productRepository.save(new Product("PROD-002", "Mouse", 50));
        productRepository.save(new Product("PROD-003", "Keyboard", 25));
        productRepository.save(new Product("PROD-004", "Monitor", 0));
    }
}
