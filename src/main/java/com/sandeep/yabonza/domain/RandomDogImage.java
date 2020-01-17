package com.sandeep.yabonza.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RandomDogImage {

    private String message;
    private String status;
}
