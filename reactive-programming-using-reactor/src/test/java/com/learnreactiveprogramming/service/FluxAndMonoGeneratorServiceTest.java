package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void getFluxStrTest() {
        StepVerifier.create(fluxAndMonoGeneratorService.getFluxStr())
                //.expectNext("Ram", "Rahim","John")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getFluxMap() {
        StepVerifier.create(fluxAndMonoGeneratorService.getFluxMap())
                .expectNext("R","A","H","I","M")
                .verifyComplete();
    }

    @Test
    void getFluxConcatMap() {
        StepVerifier.create(fluxAndMonoGeneratorService.getFluxConcatMap())
                .expectNext("R","A","H","I","M","J","O","H","N")
                .verifyComplete();
    }

    @Test
    void getFluxMapDelay() {
        StepVerifier.create(fluxAndMonoGeneratorService.getFluxMapDelay())
                //.expectNext("R","A","H","I","M","J","O","H","N")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void getMonoList() {
        String st = "RAM";

        StepVerifier.create(fluxAndMonoGeneratorService.getMonoList(st))
                .expectNext(List.of("R","A","M"))
                .verifyComplete();
    }

    @Test
    void getFluxFromMono() {
        StepVerifier.create(fluxAndMonoGeneratorService.getFluxFromMono())
                .expectNext("R","A","M")
                .verifyComplete();
    }

    @Test
    void getFluxMapTransform() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxTransform())
                .expectNext("R","A","H","I","M")
                .verifyComplete();
    }

    @Test
    void fluxTransformDefault() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxTransformDefault())
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void fluxConcat() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxConcat())
                .expectNext("M","A","N","I","K","A","N","T","A")
                .verifyComplete();
    }

    @Test
    void fluxConcatWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxConcatWith())
                .expectNext("M","A","N","I","K","A","N","T","A")
                .verifyComplete();
    }

    @Test
    void monoConcatWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.monoConcatWith())
                .expectNext("MANI","KANTA")
                .verifyComplete();
    }

    @Test
    void fluxMerge() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxMerge())
                .expectNext("M","A","N","I","K","A","N","T","A")
                .verifyComplete();
    }

    @Test
    void fluxMergeWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxMergeWith())
                .expectNext("M","A","N","I","K","A","N","T","A")
                .verifyComplete();
    }

    @Test
    void fluxMergeSeq() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxMergeSeq())
                .expectNext("M","A","N","I","K","A","N","T","A")
                .verifyComplete();
    }

    @Test
    void fluxzip() {
        StepVerifier.create(fluxAndMonoGeneratorService.fluxzip())
                .expectNext("MK","AA","NN","IT")
                .verifyComplete();
    }
}