package com.reactivespring.service;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Mono<Review> saveReview(Review review){
        return reviewRepository.save(review);
    }
}
