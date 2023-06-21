package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler reviewHandler){
        return route()
                .nest(path("v2/review"),builder -> {
                    builder.POST("",request -> reviewHandler.saveReview(request))
                            .GET("",request -> reviewHandler.getAllReviews(request))
                            .PUT("/{id}",request -> reviewHandler.updateReview(request))
                            .DELETE("/{id}",request -> reviewHandler.deleteReview(request));
                })
                .GET("v2/hello",request -> ServerResponse.ok().bodyValue("Hello World"))
                .build();

    }
}
