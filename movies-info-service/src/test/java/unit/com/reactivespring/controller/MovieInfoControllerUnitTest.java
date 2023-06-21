package com.reactivespring.controller;

import com.reactivespring.entity.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.reactivespring.controller.MovieInfoControllerIntgTest.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testGetAllMovieInfo(){

        var movieInfoFlux = List.of(new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)),
                new MovieInfo("123456","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)),
                new MovieInfo("1234567","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)));

        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfoFlux));

        webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void testGetMovieInfoById(){

        when(movieInfoServiceMock.getMovieInfoById("12345")).thenReturn(
                Mono.just(new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)))
        );

        webTestClient.get()
                .uri(URI+"/12345")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Spider man2");
    }

    @Test
    void testSaveMovieInfo(){
        MovieInfo movieInfo = new MovieInfo(null,"Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));

        when(movieInfoServiceMock.saveMovieInfo(ArgumentMatchers.isA(MovieInfo.class))).thenReturn(
                Mono.just(new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)))
        );

        webTestClient.post()
                .uri(URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var result = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals("12345",result.getMovieInfoId());
                });
    }

    @Test
    void testUpdateMovieInfo(){
        MovieInfo movieInfo = new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));

        when(movieInfoServiceMock.updateMovieInfo(ArgumentMatchers.isA(MovieInfo.class),ArgumentMatchers.isA(String.class)))
                .thenReturn(Mono.just(new MovieInfo("12345","Spider man",1999, List.of("Edward","John"), LocalDate.of(1999,01,23))));

        webTestClient.put()
                .uri(URI+"/12345")
                .bodyValue(movieInfo)
                .exchange()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var result = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals("Spider man2",result.getName());
                });
    }

    @Test
    void testDeleteMovieInfoById(){

        when(movieInfoServiceMock.deleteMovieInfoById(isA(String.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(URI+"/12345")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void testSaveMovieInfoValidations(){
        MovieInfo movieInfo = new MovieInfo(null,null,-100, List.of(""), LocalDate.of(1999,01,23));

        when(movieInfoServiceMock.saveMovieInfo(ArgumentMatchers.isA(MovieInfo.class))).thenReturn(
                Mono.just(new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23)))
        );

        webTestClient.post()
                .uri(URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(string -> {
                    var error = string.getResponseBody();
                    assert error != null;
                    var errorMessage = "Movie cast must not be null,Movie name must not be null,Movie year must be a positive value";
                    assertEquals(errorMessage,error);
                });
    }

    @Test
    void testUpdateMovieInfoNotFound(){

        MovieInfo movieInfo = new MovieInfo("1234","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));

        Mockito.when(movieInfoServiceMock.updateMovieInfo(isA(MovieInfo.class),isA(String.class)))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri(URI+"/123")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testGetMovieInfoById2(){

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class))).thenReturn(
                Mono.empty());

        webTestClient.get()
                .uri(URI+"/12345")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testGetMovieInfoByYear(){

        var url = UriComponentsBuilder.fromUriString(URI)
                .queryParam("year",2000)
                .buildAndExpand().toUri();

        when(movieInfoServiceMock.getMovieInfoByYear(isA(Integer.class))).thenReturn(
                Flux.just(new MovieInfo("1234","Spider man2",2000, List.of("Edward","John"), LocalDate.of(1999,01,23)))
        );

        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .consumeWith(listEntityExchangeResult -> {
                    var result = listEntityExchangeResult.getResponseBody();
                    assert result != null;
                });

    }
}
