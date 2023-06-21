package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

    private WebClient webClient;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${restClient.reviewUrl}")
    private String reviewUrl;

    public Flux<Review> getReview(String movieId){
        String URL = UriComponentsBuilder
                .fromHttpUrl(reviewUrl)
                .queryParam("movieInfoId",movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(URL)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("Status code is:"+clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new ReviewsClientException(errorMessage)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Status code is:"+clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new ReviewsServerException(
                                    "Server Exception in Reviews service"+errorMessage
                            )));
                })
                .bodyToFlux(Review.class);
    }
}
