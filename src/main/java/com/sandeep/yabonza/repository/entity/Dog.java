package com.sandeep.yabonza.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dog {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long dogId;

    private String dogName;
    private String dogImageUrl;
    private Date creationTime;
}
