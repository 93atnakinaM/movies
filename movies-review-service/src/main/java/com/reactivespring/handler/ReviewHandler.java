package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewRepository;
import com.reactivespring.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    private ReviewRepository reviewRepository;

    Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public ReviewHandler(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Mono<ServerResponse> saveReview(ServerRequest serverRequest){

        return serverRequest.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.ok()::bodyValue);

        /*return serverRequest.bodyToMono(Review.class)
                .flatMap(
                review -> {
                    return ServerResponse.status(HttpStatus.CREATED)
                            .body(reviewService.saveReview(review),Review.class);
                }
        );*/
    }

    private void validate(Review review) {
        var constraintViolation = validator.validate(review);
        log.info("constraintViolation: "+constraintViolation);
        if(constraintViolation.size()>0){
            var errorMessage = constraintViolation.stream()
                    .map(constraintViolations -> constraintViolations.getMessage())
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");
        Flux<Review> result;
        if(movieInfoId.isPresent()){
            result = reviewRepository.findByMovieInfoId(Long.valueOf(movieInfoId.get()));
        } else {
            result = reviewRepository.findAll();
        }
        return getServerResponseMono(result);
    }

    private static Mono<ServerResponse> getServerResponseMono(Flux<Review> result) {
        return ServerResponse.ok().body(result, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        var id = request.pathVariable("id");
        var existReview = reviewRepository.findById(id);
                //.switchIfEmpty(Mono.error(new ReviewNotFoundException("Requested Review is not present for the Review id: "+ id)));

        return existReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(
                                updReview -> {
                                    review.setComment(updReview.getComment());
                                    review.setRating(updReview.getRating());
                                    return review;
                                }
                        ))
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.ok()::bodyValue)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {

        var id = request.pathVariable("id");
        var existReview = reviewRepository.findById(id);
        return existReview.flatMap(review -> reviewRepository.deleteById(id))
                .then(ServerResponse.noContent().build());
        /*return request.bodyToMono(Review.class).flatMap(
                review -> {
                    return reviewRepository.deleteById(id);
                }
        ).then(ServerResponse.noContent().build());*/
    }
}
