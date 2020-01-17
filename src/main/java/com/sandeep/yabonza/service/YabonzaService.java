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
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class YabonzaService {

    private final static String SUCEESS = "success";

    private DogRepository dogRepository;
    private ImageService imageService;
    private RestTemplate restTemplate;

    @Value("${yabonza.dog.image.url}")
    private String dogImageUrl;

    @Autowired
    public YabonzaService(final DogRepository dogRepository, final ImageService s3ImageService, RestTemplate restTemplate) {

        this.dogRepository = dogRepository;
        this.imageService = s3ImageService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Dog storeDog() {

        ResponseEntity<RandomDogImage> randomDogImage = restTemplate.getForEntity(dogImageUrl, RandomDogImage.class);

        if(randomDogImage.getStatusCode() != HttpStatus.OK
                || !SUCEESS.equals(randomDogImage.getBody().getStatus())) {

            throw new YabonzaException("Random dog image generator API returned Error Message");
        }

        String imageUrl = randomDogImage.getBody().getMessage();

        final String dogBreedName = getDogBreedName(imageUrl);
        String s3ImageUrl = imageService.storeDog(dogBreedName, imageUrl);

        final Dog dog = new Dog(null, dogBreedName, s3ImageUrl, new Date());

        return dogRepository.save(dog);
    }

    private String getDogBreedName(String dogImageUrl) {

        String[] split = dogImageUrl.split("/");
        return split[split.length - 2];
    }

    public Optional<Dog> getDogWithId(Long id) {

       return dogRepository.findById(id);

    }

    @Transactional
    public void removeDogById(Long id) {

        Optional<Dog> dogWithId = this.getDogWithId(id);

        if(dogWithId.isPresent()) {

            Dog dog = dogWithId.get();
            imageService.removeDogFromStore(dog.getDogName());
            dogRepository.deleteById(id);

        }
        // Nothing to be done if dog is not found in repository
    }

    public List<Dog> getDogWithBreed(String breed) {

        return dogRepository.findByDogName(breed);
    }

    public List<String> searchBreeds() {
        return dogRepository.findAllDogBreeds();
    }
}
