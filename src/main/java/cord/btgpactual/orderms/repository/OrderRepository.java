package cord.btgpactual.orderms.repository;

import cord.btgpactual.orderms.controller.dto.OrderResponse;
import cord.btgpactual.orderms.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, Long> {

    Page<Order> findAllByCustomerId(Long customerId, PageRequest pageRequest);
}
