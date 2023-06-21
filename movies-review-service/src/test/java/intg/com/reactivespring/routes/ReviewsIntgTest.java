package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ReviewsIntgTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReviewRepository reviewRepository;

    final static String URI = "/v2/review";

    @BeforeEach
    void setUp() {
        Review review = new Review("123",1L,"Good",4d);
        var list = Arrays.asList(
                new Review("123",1L,"Good",4d),
                new Review("456",1L,"Good",4d),
                new Review(null,1L,"Good",4d)
        );
        reviewRepository.saveAll(list).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().block();
    }

    @Test
    void saveReview() {
        Review review = new Review(null,1L,"Good",4d);

        webTestClient.post()
                .uri(URI)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(out -> {
                    var result = out.getResponseBody();
                    assert result.getReviewId() != null;
                });
    }

    @Test
    void testGetAllReviews(){
        webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith(listEntityExchangeResult -> {
                    var result = listEntityExchangeResult.getResponseBody();
                    System.out.println(result);
                })
                .hasSize(3);
    }

    @Test
    void testUpdateReview(){
        Review review = new Review("123",1L,"Bad",3d);

        webTestClient.put()
                .uri(URI+"/123")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(list -> {
                    var result = list.getResponseBody();
                    System.out.println(result);
                    assert result!= null;
                });
    }

    @Test
    void testDeleteReview(){

        webTestClient.delete()
                .uri(URI+"/123")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void testGetReviewByMovieInfoId(){
        webTestClient.get()
                .uri(URI+"?movieInfoId=1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith(list -> {
                    var result = list.getResponseBody();
                    assert result!=null;
                    System.out.println(result);
                    assertEquals(3,result.size());
                });
    }

    @Test
    void testGetReviewByMovieInfoId2(){
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(URI)
                                .queryParam("movieInfoId",1)
                                .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith(list -> {
                    var result = list.getResponseBody();
                    assert result!=null;
                    System.out.println(result);
                    assertEquals(3,result.size());
                });
    }
}
