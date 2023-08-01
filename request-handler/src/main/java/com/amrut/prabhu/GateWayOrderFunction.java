package com.amrut.prabhu;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import java.util.function.Function;
import org.springframework.http.HttpStatus;

public class GateWayOrderFunction implements Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    @Override
    public APIGatewayV2HTTPResponse apply(APIGatewayV2HTTPEvent apiGatewayV2HTTPEvent) {
        System.out.println("received payload from gateway: " + apiGatewayV2HTTPEvent.getBody());
        return APIGatewayV2HTTPResponse.builder()
                .withBody(apiGatewayV2HTTPEvent.getBody())
                .withStatusCode(HttpStatus.OK.value())
                .build();
    }
}
