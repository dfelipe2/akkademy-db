package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.example.pong.PongActor;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import scala.concurrent.Future;
import static scala.compat.java8.FutureConverters.*;

@Ignore
public class PongActorTest {
    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(PongActor.class));

    @Test
    public void sholudReplyToPingWithPong() throws Exception{
        Future sFuture = ask(actorRef, "Ping", 1000);
        final CompletionStage<String> cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;

        assert(jFuture.get(1000, TimeUnit.MILLISECONDS).equals("Pong"));
    }

    @Test(expected = ExecutionException.class)
    public void shouldReplyToUnknownMessageWithFailure() throws Exception{
        Future sFuture = ask(actorRef, "unknown", 1000);
        final CompletionStage<String>  cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;

        jFuture.get(1000, TimeUnit.MILLISECONDS);
    }
}
