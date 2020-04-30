package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.Test;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static scala.compat.java8.FutureConverters.*;

public class PongActorAdvanceTest {
    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(PongActor.class));

    @Test
    public void shouldPrintToConsole() throws Exception{
        askPong("Ping").thenAccept(x -> System.out.println("replied with: " + x));
        Thread.sleep(100);
    }

    @Test
    public void shouldTransform() throws Exception {
        char result = (char) get(askPong("Ping").thenApply(x -> x.charAt(0)));
        assertEquals('P', result);
    }

    @Test
    public void shouldTransformAsync() throws Exception {
        CompletionStage cs = askPong("Ping").thenCompose(x -> askPong("Ping"));
        assertEquals(get(cs), "Pong");
    }

    @Test
    public void shouldEffectOnError() throws Exception {
        CompletionStage<String> cs = askPong("cause error").handle((result,error) -> {
            if(error!=null){
                System.out.println("Error: "+ error);
            }
            return result;
        });
        System.out.println(get(cs));
    }

    @Test
    public void shouldRecoverOnError() throws Exception {
        CompletionStage<String> cs = askPong("cause error").exceptionally(t -> "default");
        assertEquals(get(cs), "default");
    }

    @Test
    public void shouldRetryOnError() throws Exception {
        CompletionStage<String> cs = askPong("cause error").
                handle((x,t) -> t == null
                    ? CompletableFuture.completedFuture(x)
                    : askPong("Ping")
                ).thenCompose(x -> x);
        assertEquals(get(cs), "Pong");
    }

    @Test
    public void shouldHandleErrorAtEnd() throws Exception {
        CompletionStage<String> cs = askPong("Ping").
                thenCompose(x -> askPong("Ping" + x)).
                handle((x,t) -> {
                    if(t != null){
                        return "default";
                    }else{
                        return x;
                    }
                });
        assertEquals(get(cs), "default");
    }

    @Test
    public void shouldAccessTwoFutures() throws Exception {
        CompletionStage<String> cs = askPong("Ping").thenCombine(askPong("Ping"), (a,b) -> {
            return a+b;
        });
        assertEquals(get(cs), "PongPong");
    }

    public CompletionStage<String> askPong(String message) {
        Future sFuture = ask(actorRef, message, 1000);
        CompletionStage<String> cs = toJava(sFuture);
        return cs;
    }

    public Object get(CompletionStage cs) throws Exception {
        return ((CompletableFuture<String>) cs).get(1000, TimeUnit.MILLISECONDS);
    }
}
