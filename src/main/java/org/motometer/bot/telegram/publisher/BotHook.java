package org.motometer.bot.telegram.publisher;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.motometer.telegram.bot.Bot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class BotHook implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final MessageSender messageSender = new MessageSender();
    private final Bot bot = BotConfig.bot();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Received request with url =" + input.getPath());

        final String body = input.getBody();

        bot.createWebHookListener(messageSender)
            .onEvent(body);

        logger.log("Successfully handled request");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(201);
        return response;
    }
}