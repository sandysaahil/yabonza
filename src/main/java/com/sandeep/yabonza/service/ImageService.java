package com.sandeep.yabonza.service;

public interface ImageService {
    String storeDog(String dogBreedName, String imageUrl);

    void removeDogFromStore(String dogName);
}
