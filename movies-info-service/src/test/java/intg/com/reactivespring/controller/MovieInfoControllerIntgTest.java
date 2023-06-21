package com.reactivespring.controller;

import com.reactivespring.entity.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MovieInfoControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieInfoRepository movieInfoRepository;

    final static String URI = "/v1/movieInfo";

    @BeforeEach
    void setUp() {
        MovieInfo movieInfo = new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));
        movieInfoRepository.save(movieInfo).block();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void saveMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null,"Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));

        var response = webTestClient.post()
                .uri(URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var result = movieInfoEntityExchangeResult.getResponseBody();
                    assert result != null;
                    assert result.getMovieInfoId() != null;
                });
    }

    @Test
    void getAllInfoTest(){
        webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getInfoByIdTest(){
        MovieInfo movieInfo = new MovieInfo("12345","Spider man2",1999, List.of("Edward","John"), LocalDate.of(1999,01,23));
        webTestClient.get()
                .uri(URI+"/12345")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .isEqualTo(movieInfo);
    }

    @Test
    void getInfoByIdTest2(){
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
    void updateMovieInfoById(){
        MovieInfo movieInfo = new MovieInfo("12345","Spider man",2000, List.of("Edward","John"), LocalDate.of(2000,01,23));
        webTestClient.put()
                .uri(URI+"/12345")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var result = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals("Spider man",result.getName());
                });
    }

    @Test
    void deleteMovieInfoById(){
        webTestClient.delete()
                .uri(URI+"/12345")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void testGetById(){
        webTestClient.get()
                .uri(URI+"/123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}