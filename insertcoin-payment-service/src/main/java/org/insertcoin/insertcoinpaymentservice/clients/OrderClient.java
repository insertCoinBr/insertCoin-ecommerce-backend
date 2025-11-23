package org.insertcoin.insertcoinpaymentservice.clients;

import org.insertcoin.insertcoinpaymentservice.dtos.OrderNotificationDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(
        name = "insertcoin-order-service",
        path = "/internal/orders"
)
public interface OrderClient {

    @GetMapping("/{id}/notification-data")
    OrderNotificationDataDTO getNotificationData(
            @PathVariable("id") UUID orderId
    );
}
