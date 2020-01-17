package com.sandeep.yabonza.repository;

import com.sandeep.yabonza.repository.entity.Dog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends CrudRepository<Dog, Long> {
    List<Dog> findByDogName(String breed);

    @Query("select distinct d.dogName from Dog d")
    List<String> findAllDogBreeds();
}
