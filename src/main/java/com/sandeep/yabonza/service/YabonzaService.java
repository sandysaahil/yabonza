package com.sandeep.yabonza.service;

import com.sandeep.yabonza.domain.RandomDogImage;
import com.sandeep.yabonza.exception.YabonzaException;
import com.sandeep.yabonza.repository.DogRepository;
import com.sandeep.yabonza.repository.entity.Dog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Manages the Dog breed service - Stores dog breed images in Store (AWS S3) and other details in
 * Database
 */
@Service
@Slf4j
public class YabonzaService {

    private final static String SUCEESS = "success";

    private DogRepository dogRepository;
    private ImageService imageService;
    private RestTemplate restTemplate;

    /**
     * API is generate random dog breed images
     */
    @Value("${yabonza.dog.image.url}")
    private String dogImageUrl;

    @Autowired
    public YabonzaService(final DogRepository dogRepository, final ImageService s3ImageService, RestTemplate restTemplate) {

        this.dogRepository = dogRepository;
        this.imageService = s3ImageService;
        this.restTemplate = restTemplate;
    }

    /**
     * Stores dog images in AWS S3 and other details in Database
     * @return
     */
    @Transactional
    public Dog storeDog() {

        log.info("YabonzaService.storeDog() - Started");
        ResponseEntity<RandomDogImage> randomDogImage = restTemplate.getForEntity(dogImageUrl, RandomDogImage.class);

        if(randomDogImage.getStatusCode() != HttpStatus.OK
                || !SUCEESS.equals(randomDogImage.getBody().getStatus())) {

            throw new YabonzaException("Random dog image generator API returned Error Message");
        }

        String imageUrl = randomDogImage.getBody().getMessage();

        final String dogBreedName = getDogBreedName(imageUrl);
        String s3ImageUrl = imageService.storeDog(dogBreedName, imageUrl);

        log.info("YabonzaService.storeDog() - Dog image is successfully stored in S3");

        final Dog dog = new Dog(null, dogBreedName, s3ImageUrl, new Date());

        Dog savedDog = dogRepository.save(dog);

        log.info("YabonzaService.storeDog() - Completed. Dog data is stored successfully - "+savedDog.toString());
        return savedDog;
    }

    /**
     * The name of the dog breed is in the Dog image random API URL
     *
     * @param dogImageUrl
     * @return
     */
    private String getDogBreedName(String dogImageUrl) {

        String[] split = dogImageUrl.split("/");
        String dogBreedName = split[split.length - 2];
        log.info("YabonzaService.getDogBreedName() - The name of the breed is : "+dogBreedName);

        return dogBreedName;
    }

    public Optional<Dog> getDogWithId(Long id) {
       return dogRepository.findById(id);
    }

    /**
     * Removes te dog image form AWS S3 and details from Database
     *
     * @param id
     */
    @Transactional
    public void removeDogById(Long id) {

        log.info("YabonzaService.removeDogById() - Started");
        Optional<Dog> dogWithId = this.getDogWithId(id);

        if(dogWithId.isPresent()) {

            Dog dog = dogWithId.get();
            imageService.removeDogFromStore(dog.getDogName());
            dogRepository.deleteById(id);

        }
        // Nothing to be done if dog is not found in repository
        log.info("YabonzaService.removeDogById() - Finished. Dog with id : "+ id + " deleted Succefully");
    }

    public List<Dog> getDogWithBreed(String breed) {

        return dogRepository.findByDogName(breed);
    }

    public List<String> searchBreeds() {
        return dogRepository.findAllDogBreeds();
    }
}
