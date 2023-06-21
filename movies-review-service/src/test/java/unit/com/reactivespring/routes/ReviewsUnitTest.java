package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.reactivespring.routes.ReviewsIntgTest.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isA;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSaveReview() {
        Review review = new Review(null,1L,"Good",4d);

        when(reviewRepository.save(isA(Review.class))).thenReturn(
                Mono.just(new Review("1234",1L,"Good",4d))
        );

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
    void testSaveReviewValidations() {
        Review review = new Review(null,null,"Good",4d);

        when(reviewRepository.save(isA(Review.class))).thenReturn(
                Mono.just(new Review("1234",1L,"Good",4d))
        );

        webTestClient.post()
                .uri(URI)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(out -> {
                    var result = out.getResponseBody();
                    assertEquals("review.movieInfoId : must be a not null value",result);
                });
    }

    @Test
    void testGetAllReviews() {

        Flux<Review> reviewFlux = Flux.fromIterable(List.of(new Review("1234", 1L, "Good", 4d),
                new Review("1234", 1L, "Good", 4d),
                new Review("1234", 1L, "Good", 4d)));

        when(reviewRepository.findAll()).thenReturn(reviewFlux);

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

        when(reviewRepository.findById(isA(String.class))).thenReturn(Mono.just(
                new Review("123",1L,"Bad",3d)
        ));

        when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(
                new Review("123",1L,"Bad1",3d)
        ));

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
                    assertEquals("Bad1",result.getComment());
                });
    }

    @Test
    void testDeleteReview(){

        when(reviewRepository.findById(isA(String.class))).thenReturn(Mono.just(
                new Review("123",1L,"Bad",3d)
        ));

        when(reviewRepository.deleteById(isA(String.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(URI+"/123")
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
