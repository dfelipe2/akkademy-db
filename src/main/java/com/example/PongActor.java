package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;

public class PongActor extends AbstractActor {
    public PartialFunction receive(){
        return ReceiveBuilder.
                matchEquals("Ping", s -> sender().tell("Pong", ActorRef.noSender())).
                matchAny(x -> sender().tell(new Status.Failure(new Exception("unknow message")), self())).build();
    }
}
