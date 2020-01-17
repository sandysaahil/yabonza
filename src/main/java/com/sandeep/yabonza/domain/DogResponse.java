package com.sandeep.yabonza.domain;

import com.sandeep.yabonza.repository.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class DogResponse {
    private HttpStatus status;
    private String error;
    private List<Dog> dogList;

    public DogResponse(HttpStatus status, String error, List<Dog> dogList) {
        this.status = status;
        this.error = error;
        this.dogList = dogList;
    }
}
