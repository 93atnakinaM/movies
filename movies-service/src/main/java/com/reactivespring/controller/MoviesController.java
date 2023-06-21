package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.Review;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;

    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable("id") String movieId){

        return moviesInfoRestClient.getMovieInfo(movieId)
                .flatMap(movieInfo -> {
                        Mono<List<Review>> reviewList = reviewsRestClient.getReview(movieId)
                                .collectList();
                        return reviewList.map(reviews -> new Movie(movieInfo, reviews));
                });
    }
}
