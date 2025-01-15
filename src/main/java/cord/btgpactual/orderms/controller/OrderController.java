package cord.btgpactual.orderms.controller;

import cord.btgpactual.orderms.controller.dto.ApiResponse;
import cord.btgpactual.orderms.controller.dto.OrderResponse;
import cord.btgpactual.orderms.controller.dto.PaginationResponse;
import cord.btgpactual.orderms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {


    @Autowired
    private OrderService service;

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerId") Long customerId,
                                                                 @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){

        var body = service.findAllByCustomerId(customerId, PageRequest.of(page,pageSize));
        var totalOnOrders = service.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok().body(new ApiResponse<>(
                Map.of("totalOnOrders", totalOnOrders),
                body.getContent(),
                PaginationResponse.fromPage(body)
        ));

    }
}
