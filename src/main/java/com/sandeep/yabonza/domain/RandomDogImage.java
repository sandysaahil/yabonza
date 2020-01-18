package com.sandeep.yabonza.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Maps random dog API response to class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RandomDogImage {

    private String message;
    private String status;
}
