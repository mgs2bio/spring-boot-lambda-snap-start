package com.amrut.prabhu;

import java.util.function.Function;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amrut.prabhu.message.SpringCloudSQS;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


@AllArgsConstructor
@NoArgsConstructor
public class OrderFunction implements Function<Message<Order>, Message<Confirmation>> {

    private SpringCloudSQS springCloudSQS;

    @Override
    public Message<Confirmation> apply(Message<Order> orderMsg) {
        System.out.printf("placing order message without gw: " + orderMsg);
        Order order = orderMsg.getPayload();
        if (springCloudSQS == null) {
            System.out.println("springCloudSQS is null.");
        }
        springCloudSQS.send(order);
        return MessageBuilder.withPayload(Confirmation.builder()
                        .orderId(order.getOrderId())
                        .isShipped(true)
                        .balance(order.getAmount())
                        .build())
                .build();
    }
}
