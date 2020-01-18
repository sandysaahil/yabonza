package com.sandeep.yabonza.service;

/**
 * Gereric interface to store and remove images from Data Store. Current implementation is AWS S3 but this can
 * be implemented by other Storage APIs.
 */
public interface ImageService {

    String storeDog(String dogBreedName, String imageUrl);
    void removeDogFromStore(String dogName);
}
