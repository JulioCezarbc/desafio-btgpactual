package cord.btgpactual.orderms.service;

import cord.btgpactual.orderms.controller.dto.OrderResponse;
import cord.btgpactual.orderms.entity.Order;
import cord.btgpactual.orderms.entity.OrderItem;
import cord.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import cord.btgpactual.orderms.repository.OrderRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest){
        var orders = repository.findAllByCustomerId(customerId, pageRequest);
        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId){
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );
        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }

    public void save(OrderCreatedEvent event){
        var entity = new Order();
        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItens(event));
        entity.setTotal(getTotal(event));

        repository.save(entity);

    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().
                        multiply(BigDecimal.valueOf(i.quantidade()))).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO );
    }

    private static List<OrderItem> getOrderItens(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> new OrderItem(i.produto(),
                        i.quantidade(),
                        i.preco()))
                .toList();
    }
}
