package com.reactivespring.repository;

import com.reactivespring.entity.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    Flux<MovieInfo> findMovieByYear(Integer year);

    Mono<MovieInfo> findMovieByName(String name);
}
