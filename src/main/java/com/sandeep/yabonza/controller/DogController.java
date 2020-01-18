package com.sandeep.yabonza.controller;

import com.sandeep.yabonza.exception.YabonzaException;
import com.sandeep.yabonza.repository.entity.Dog;
import com.sandeep.yabonza.service.YabonzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Endpoint API for all the dog breed operations
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class DogController {

    @Autowired
    private YabonzaService yabonzaService;

    /**
     * Generates a new Dog Breed
     * <ul>
     *     <li>1. Retrieves a random dog picture from https://dog.ceo/api/breeds/image/random</li>
     *     <li>2. Stores the picture in AWS S3</li>
     *     <li>3. Stores the Dog breed, AWS S3 location and creation time in database</li>
     * </ul>
     *
     * @return Dog details
     * @throws Exception
     */
    @RequestMapping(value = "/dog", method = RequestMethod.POST)
    public ResponseEntity<Dog> storeDog()  throws Exception {

        try {

            return new ResponseEntity<Dog> (yabonzaService.storeDog(), HttpStatus.OK);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Retrieves the Dog details based on the id passed
     *
      * @param id id of the dog in database
     * @return
     */
    @RequestMapping(value = "/dog/{id}", method = RequestMethod.GET)
    public ResponseEntity<Dog> getDog(@PathVariable("id") String id) {

        try {

            if(StringUtils.isEmpty(id)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "dog id not found in the request");
            }

            Optional<Dog> dogWithId = yabonzaService.getDogWithId(Long.valueOf(id));
            if(dogWithId.isPresent()) {
                Dog dog = dogWithId.get();
                return new ResponseEntity<> (dog, HttpStatus.OK);
            } else {
                throw new YabonzaException("Dog could not be found for the id : "+id);
            }

        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "dog id could not be converted to correct internal id. Try using Integer value");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Removes the record from the database with the given id and also removes breed image
     * from datastore
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/dog/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeDog(@PathVariable("id") String id) {

        try {

            if(StringUtils.isEmpty(id)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "dog id not found in the request");
            }

            yabonzaService.removeDogById(Long.valueOf(id));

            return new ResponseEntity<> ("Dog successfully deleted. Dog id : "+id, HttpStatus.OK);
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "dog id could not be converted to correct internal id. Try using Integer value");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the dog breed information from the DB based on the given breed name
     *
     * @param breed
     * @return
     */
    @RequestMapping(value = "/dog/search/{breed}", method = RequestMethod.GET)
    public ResponseEntity<List<Dog>> searchDogByBreed(@PathVariable("breed") String breed) {

        try {

            if(StringUtils.isEmpty(breed)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "dog breed not found in the request");
            }

            List<Dog> dogList = yabonzaService.getDogWithBreed(breed);
            return new ResponseEntity<> (dogList, HttpStatus.OK);


        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Retrieves all the distinct dog breed names in the system
     *
     * @return
     */
    @RequestMapping(value = "/dogs", method = RequestMethod.GET)
    public ResponseEntity<List<String>> searchBreeds() {

        try {


            List<String> dogList = yabonzaService.searchBreeds();
            return new ResponseEntity<> (dogList, HttpStatus.OK);


        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

}
