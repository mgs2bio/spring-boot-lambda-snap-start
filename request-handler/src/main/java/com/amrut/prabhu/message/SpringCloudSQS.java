package com.amrut.prabhu.message;


import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amrut.prabhu.Order;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringCloudSQS {

    private final QueueMessagingTemplate queueMessagingTemplate;


    @Value("${SQSqueueName}")
    private String QUEUE_NAME;


    public SpringCloudSQS(AmazonSQSAsync amazonSQSAsync) {
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }

    public void send(Order order) {
        log.info("sending order {} to sqs - {}", order, QUEUE_NAME);
        this.queueMessagingTemplate.convertAndSend(QUEUE_NAME, MessageBuilder.withPayload(order).build());
    }


}
