package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "restClient.movieInfoUrl= http://localhost:8084/v1/movieInfo",
                "restClient.reviewUrl= http://localhost:8084/v2/review"
        }
)
public class MoviesControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testGetMovie() {

        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieInfo"+"/"+movieId))
                .willReturn(aResponse()
                .withHeader("Content-Type","application/json")
                .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v2/review"))
                .willReturn(aResponse()
                        .withHeader("Content-Type","application/json")
                        .withBodyFile("reviews.json")));

        webTestClient.get()
                .uri("/v1/movies"+"/"+"{movieId}",movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movie ->{
                    var result = movie.getResponseBody();
                    assert result.getReviewList().size() ==2;
                    assertEquals("Batman Begins",result.getMovieInfo().getName());
                });

        /*var movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v2/review"))
                .withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));


        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", "abc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                            var movie = movieEntityExchangeResult.getResponseBody();
                            assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                            assertEquals("Batman Begins", movie.getMovieInfo().getName());
                        }
                );*/
    }
}
