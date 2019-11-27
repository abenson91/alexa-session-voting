package main.java.com.alexa.poc.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.gameEngine.StartInputHandlerDirective;
import com.amazon.ask.model.services.gameEngine.Event;
import com.amazon.ask.model.services.gameEngine.EventReportingType;
import com.amazon.ask.model.services.gameEngine.InputEventActionType;
import com.amazon.ask.model.services.gameEngine.Pattern;
import com.amazon.ask.model.services.gameEngine.PatternRecognizer;
import com.amazon.ask.model.services.gameEngine.PatternRecognizerAnchorType;
import com.amazon.ask.model.services.gameEngine.Recognizer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class StartInputIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(intentName("StartInputHandlerIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {

        // We won't know the gadget IDs of the buttons in advance,
        // so define proxies that we can use in the recognizer definition.
        List<String> proxies = (List<String>) Arrays.asList("first", "second"
                , "third");

        // Build a pattern that the recognizer will depend on.
        // This means that for the recognizer to be true, this
        // pattern (and any other patterns that the recognizer
        // depends on) must occur. We use a proxy ("first")
        // instead of a gadget ID because we won't know the
        // gadget ID in advance.
        Pattern firstButtonPressed = Pattern.builder()
                .withAction(InputEventActionType.DOWN)
                .withGadgetIds((List<String>) Arrays.asList("first"))
                .addColorsItem("FF0000") //Red
                .build();

        // Build a pattern for the second button pressed.
        Pattern secondButtonPressed = Pattern.builder()
                .withAction(InputEventActionType.DOWN)
                .withGadgetIds((List<String>) Arrays.asList("second"))
                .addColorsItem("FFFF00") //Yellow
                .build();

        // Build a pattern for the third button pressed.
        Pattern thirdButtonPressed = Pattern.builder()
                .withAction(InputEventActionType.DOWN)
                .withGadgetIds((List<String>) Arrays.asList("third"))
                .addColorsItem("00FF00") //Green
                .build();

        // Build the recognizer that will be true when
        // three buttons are pressed.
        PatternRecognizer allPressedRecognizer = PatternRecognizer.builder()
                .withFuzzy(true)
                .withAnchor(PatternRecognizerAnchorType.END)
                .withPattern((List<Pattern>) Arrays.asList(
                        firstButtonPressed,
                        secondButtonPressed,
                        thirdButtonPressed))
                .build();

        // Put the recognizer into a map so that we can give
        // it a name.
        HashMap<String, Recognizer> recognizerMap =
                new HashMap<String, Recognizer>();
        recognizerMap.put("allPressedRecognizer", allPressedRecognizer);

        // Define the event that will be triggered when three buttons
        // have been pressed.
        Event rollCallCompleteEvent = Event.builder()
                .withShouldEndInputHandler(true)
                .withReports(EventReportingType.MATCHES)
                .withMeets((List<String>) Arrays.asList("allPressedRecognizer"))
                .build();

        // Define the event that will be triggered by the Input Handler
        // timing out.
        Event rollCallFailedEvent = Event.builder()
                .withShouldEndInputHandler(true)
                .withReports(EventReportingType.HISTORY)
                .withMeets((List<String>) Arrays.asList("timed out"))
                .build();

        // Put the events into a map.
        HashMap<String, Event> eventMap = new HashMap<String, Event>();
        eventMap.put("rollCallComplete", rollCallCompleteEvent);
        eventMap.put("rollCallFailed", rollCallFailedEvent);

        // Set up an Input Handler to notify this skill when three buttons
        // have been pressed, or if 30 seconds have passed without three
        // buttons being pressed.
        StartInputHandlerDirective directive =
                StartInputHandlerDirective.builder()
                .withTimeout((long) 30000)
                .withProxies(proxies)
                .withRecognizers(recognizerMap)
                .withEvents(eventMap)
                .build();

        // Assemble the response.
        Optional<Response> response = handlerInput.getResponseBuilder()
                .withSpeech("Please press the buttons one at a time. When you" +
                        " press the "
                        + "third button, there will be an Input Handler event" +
                        ". If you "
                        + "do not press three buttons within 30 seconds, " +
                        "there will be "
                        + "an Input Handler event to signal that roll call " +
                        "failed. "
                        + "<audio src=\"https://s3.amazonaws" +
                        ".com/ask-soundlibrary/foley" +
                        "/amzn_sfx_rhythmic_ticking_30s_01.mp3\"/>")
                .withShouldEndSession(false)
                .addDirective(directive)
                .build();

        // Write the response to CloudWatch.
        String responseLog = response.toString();
        responseLog = responseLog.replace("\n", " ").replace("\r", " ");
        System.out.println("===RESPONSE=== " + responseLog);

        // Return the response.
        return response;

    }
}

