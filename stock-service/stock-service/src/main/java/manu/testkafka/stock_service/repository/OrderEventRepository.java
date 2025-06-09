package manu.testkafka.stock_service.repository;

import manu.testkafka.stock_service.entity.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEventEntity, Long> {

}
