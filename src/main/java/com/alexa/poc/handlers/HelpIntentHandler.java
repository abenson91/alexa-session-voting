package main.java.com.alexa.poc.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HelpIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "I can start a review session. I need three echo " +
                "buttons and each will represent mad, sad, and glad and I'll " +
                "track the total";

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Alexa Session Voting", speechText)
                .withReprompt(speechText)
                .build();
    }

}
