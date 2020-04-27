package com.example;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.example.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected final Map<String, Object> map = new HashMap<>();

    private AkkademyDb(){
        receive(ReceiveBuilder.
                match(SetRequest.class, message ->{
                    log.info("Received set request - key: {} value: {}", message.getKey(), message.getValue());
                    map.put(message.getKey(), message.getValue());
                }).matchAny(o -> log.info("received unknown message {}", o)).build());
    }
}
