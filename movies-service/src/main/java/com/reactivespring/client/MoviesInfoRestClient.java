package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    public WebClient webClient;

    @Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> getMovieInfo(String movieId){

        //var url = movieInfoUrl.concat("/{id}");
        return webClient.get()
                //.uri(url,movieId)
                .uri(movieInfoUrl+"/"+movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("Status code is:"+clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.error(new MoviesInfoClientException(
                                "There is no Movie info available for the given id: "+movieId,
                                clientResponse.statusCode().value()
                        ));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new MoviesInfoClientException(
                                    errorMessage, clientResponse.statusCode().value()
                            )));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Status code is:"+clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new MoviesInfoServerException(
                                    "Server Exception in Movie info service"+errorMessage
                            )));
                })
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
