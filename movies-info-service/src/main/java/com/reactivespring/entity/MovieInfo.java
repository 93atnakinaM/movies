package com.reactivespring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "Movie name must not be null")
    private String name;
    @NotNull
    @Positive(message = "Movie year must be a positive value")
    private int year;
    private List<@NotBlank(message = "Movie cast must not be null") String> cast;
    private LocalDate releaseDate;
}
