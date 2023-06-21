package com.reactivespring.controller;

import com.reactivespring.entity.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    private MovieInfoService movieInfoService;

    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping(value = "/movieInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovieInfo> saveMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.saveMovieInfo(movieInfo).log();
    }

    @GetMapping("/movieInfo")
    public Flux<MovieInfo> getAllMovieInfo(@RequestParam(value = "year", required = false) Integer year){
        if(year!=null)
            return movieInfoService.getMovieInfoByYear(year);
        return movieInfoService.getAllMovieInfo();
    }

    @GetMapping("/movieInfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id){
        return movieInfoService.getMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
    }

    @PutMapping("/movieInfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo movieInfo, @PathVariable String id){
        return movieInfoService.updateMovieInfo(movieInfo,id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/movieInfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfoById(@PathVariable String id){
        return movieInfoService.deleteMovieInfoById(id);
    }
}
