package com.learnreactiveprogramming.service;

import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> getFluxStr(){
        return Flux.fromIterable(List.of("Ram", "Rahim","John")).log();
    }

    public Flux<String> getFluxMap(){
        return Flux.fromIterable(List.of("Ram", "Rahim"))
                .filter(s -> s.length()>3)
                .map(s->s.toUpperCase())
                .flatMap(s->substring(s)).log();
    }

    public Flux<String> getFluxMapDelay(){
        return Flux.fromIterable(List.of("Ram", "Rahim","John"))
                .filter(s -> s.length()>3)
                .map(s->s.toUpperCase())
                //flatMap wont store order while doing asynchronous
                .flatMap(s->substringDelay(s)).log();
    }

    public Flux<String> getFluxConcatMap(){
        return Flux.fromIterable(List.of("Ram", "Rahim","John"))
                .filter(s -> s.length()>3)
                .map(s->s.toUpperCase())
                //concatMap uses for storing order of objects, but takes more time than flatMap
                .concatMap(s->substringDelay(s)).log();
    }

    public Mono<List<String>> getMonoList(String st){
        //var charArray = st.split(""); converting string to char Array
        return Mono.just(List.of(st.split(""))).log();
    }

    public Flux<String> getFluxFromMono(){
        //by using flatmapmany we can get flux from mono
        return Mono.just("Ram")
                .map(s->s.toUpperCase()).flatMapMany(this::substring);
    }

    public Flux<String> substring(String s){
        return Flux.fromArray(s.split(""));
    }

    public Flux<String> fluxTransform(){

        Function<Flux<String>, Flux<String>> function = st -> st.filter(s -> s.length()>3)
                .map(s->s.toUpperCase());

        return Flux.fromIterable(List.of("Ram", "Rahim"))
                .transform(function)
                .flatMap(s->substring(s)).log();
    }

    public Flux<String> fluxTransformDefault(){

        Function<Flux<String>, Flux<String>> function = st -> st.filter(s -> s.length()>3)
                .map(s->s.toUpperCase()).flatMap(s->substring(s));

        return Flux.fromIterable(List.of(""))
                .transform(function)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> fluxConcat(){
        return Flux.concat(Flux.just("M","A","N","I"),Flux.just("K","A","N","T","A"));
    }

    public Flux<String> fluxConcatWith(){
        return Flux.just("M","A","N","I").concatWith(Flux.just("K","A","N","T","A"));
    }

    public Flux<String> monoConcatWith(){
        return Mono.just("MANI").concatWith(Mono.just("KANTA")).log();
    }

    public Flux<String> fluxMerge(){
        return Flux.merge(Flux.just("M","A","N","I").delayElements(Duration.ofMillis(100)),
                Flux.just("K","A","N","T","A").delayElements(Duration.ofMillis(110))).log();
    }

    public Flux<String> fluxMergeWith(){
        return Flux.just("M","A","N","I").delayElements(Duration.ofMillis(100)).mergeWith(
                Flux.just("K","A","N","T","A").delayElements(Duration.ofMillis(110))).log();
    }

    public Flux<String> monoMergeWith(){
        return Mono.just("MANI").mergeWith(Mono.just("KANTA")).log();
    }

    public Flux<String> fluxMergeSeq(){
        return Flux.mergeSequential(Flux.just("M","A","N","I").delayElements(Duration.ofMillis(100)),
                Flux.just("K","A","N","T","A").delayElements(Duration.ofMillis(110))).log();
    }

    public Flux<String> fluxzip(){
        return Flux.zip(Flux.just("M","A","N","I"),Flux.just("K","A","N","T","A"),(a,b)->a+b).log();
    }

    public Flux<String> substringDelay(String s){
        return Flux.fromArray(s.split("")).delayElements(Duration.ofMillis(1000));
    }

    public Mono<String> getMonoStr(){
        return Mono.just("Ram");
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.getFluxStr().subscribe(
                name -> System.out.println("Flux Name is: "+name)
        );
        fluxAndMonoGeneratorService.getMonoStr().subscribe(
                name -> System.out.println("Mono Name is: "+name)
        );
    }
}
