package main.java.com.alexa.poc;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import main.java.com.alexa.poc.handlers.CancelandStopIntentHandler;
import main.java.com.alexa.poc.handlers.FallBackIntentHandler;
import main.java.com.alexa.poc.handlers.HelpIntentHandler;
import main.java.com.alexa.poc.handlers.SessionEndedRequestHandler;
import main.java.com.alexa.poc.handlers.StartInputIntentHandler;

public class VotingSkillStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        //predefined Amazon Intents
                        new FallBackIntentHandler(),
                        new HelpIntentHandler(),
                        new CancelandStopIntentHandler(),
                        new SessionEndedRequestHandler(),
                        //Custom Intents
                        new StartInputIntentHandler()
                )
                //.withSkillId("")
                .build();
    }

    public VotingSkillStreamHandler(Skill skill) {
        super(getSkill());
    }
}
