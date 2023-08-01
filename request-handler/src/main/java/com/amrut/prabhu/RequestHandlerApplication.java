package com.amrut.prabhu;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import java.time.Instant;
import java.util.function.Function;

import com.amrut.prabhu.message.SpringCloudSQS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
@Slf4j
public class RequestHandlerApplication /*implements ApplicationContextInitializer<GenericApplicationContext>*/ {


    /*
     * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
     * in the POM to ensure boot plug-in makes the correct entry
     */
    public static void main(String[] args) {
        // empty unless using Custom runtime at which point it should include
        // FunctionalSpringApplication.run(RequestHandlerApplication.class, args);
         SpringApplication.run(RequestHandlerApplication.class, args);
    }

    public Function<String, String> toUpperCase() {
        return value -> {
            System.out.printf("invoked UpperCase: " + value);
            return value.toUpperCase() + " " + Instant.now();
        };
    }

    @Bean
    public Function<Message<Order>, Message<Confirmation>> placeOrder(SpringCloudSQS springCloudSQS) {
        return orderMsg -> {
            log.info("placing an order message: {}", orderMsg);
            springCloudSQS.send(orderMsg.getPayload());
            return MessageBuilder.withPayload(Confirmation.builder()
                            .orderId(orderMsg.getPayload().getOrderId())
                            .isShipped(true)
                            .balance(1000)
                            .build())
                    .build();
        };
    }

    public Function<String, String> toLowerCase() {
        return value -> value.toLowerCase();
    }

    //    @Bean
    //    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getResponse(){
    //
    //        return value-> {
    //            System.out.println("received value: " + value.getBody());
    //            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(value.getBody());
    //        };
    //    }

    public Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> placeOrderFromGW() {
        return gwEvent -> {
            System.out.println("received payload from gateway: " + gwEvent.getBody());
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(gwEvent.getBody())
                    .build();
        };
    }

    // functions will start much quicker if you can use functional bean definitions instead of @Bean

    public void initialize(GenericApplicationContext context) {
        // set spring_cloud_function_definition to "placeOrder" in terraform script before invoking 'placeOrder'
        context.registerBean("placeOrder", FunctionRegistration.class, () -> new FunctionRegistration<
                        Function<Message<Order>, Message<Confirmation>>>(new OrderFunction())
                .type(OrderFunction.class));
        // use APIGatewayProxyRequestEvent for rest api gateway type.
        context.registerBean("placeOrderFromGateway", FunctionRegistration.class, () -> new FunctionRegistration<
                        Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>>(new GateWayOrderFunction())
                .type(FunctionType.from(APIGatewayV2HTTPEvent.class).to(APIGatewayV2HTTPResponse.class)));
        context.registerBean("toUpperCase", FunctionRegistration.class, () -> new FunctionRegistration<
                        Function<String, String>>(toUpperCase())
                .type(FunctionType.from(String.class).to(String.class)));
        context.registerBean("toLowerCase", FunctionRegistration.class, () -> new FunctionRegistration<
                        Function<String, String>>(toLowerCase())
                .type(FunctionType.from(String.class).to(String.class)));
    }
}
