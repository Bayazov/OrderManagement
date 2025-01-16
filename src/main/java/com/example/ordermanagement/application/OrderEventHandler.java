package com.example.ordermanagement.application;

import com.example.ordermanagement.domain.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);

    @EventListener
    public void handleOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        logger.info("Order status changed: orderId={}, oldStatus={}, newStatus={}",
                event.getOrderId(), event.getOldStatus(), event.getNewStatus());
        // Здесь можно добавить дополнительную логику обработки события
    }
}

