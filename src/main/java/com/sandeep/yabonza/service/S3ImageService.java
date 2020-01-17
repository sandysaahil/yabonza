package com.sandeep.yabonza.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.sandeep.yabonza.exception.YabonzaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.URL;

@Service
public class S3ImageService implements ImageService {

    @Autowired
    private AmazonS3 s3client;


    @Value("${s3.bucket.name:yabonza-dogs-store}")
    private String bucketName;

    @Autowired
    public S3ImageService(final AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String getImage(final String imageLocation) {
        return null;
    }

    public String storeDog(final String dogBreedName, String imageUrl) {

        String s3ImageUrl = null;

        if(!s3client.doesBucketExist(bucketName)) {
            throw new YabonzaException(String.format("S3 bucket with bucket name %1s does not exist.", bucketName));
        }

        try(InputStream inputStream = new URL(imageUrl).openStream()){

            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentType("image/jpg");

            PutObjectResult putObjectResult = s3client.putObject(new PutObjectRequest(bucketName,
                    dogBreedName,
                    inputStream,objMeta)
            );

            if(StringUtils.isEmpty(putObjectResult.getETag())){
               throw new YabonzaException("Image could not be stored in S3");
            }

            s3ImageUrl = getImageUrl(s3client, bucketName, dogBreedName);

        } catch (Exception e) {
            throw new YabonzaException("Image could not be stored in S3", e);
        }

        return s3ImageUrl;
    }

    @Override
    public void removeDogFromStore(String dogName) {

        try {

            if(!s3client.doesBucketExist(bucketName)) {
                throw new YabonzaException(String.format("S3 bucket with bucket name %1s does not exist.", bucketName));
            }

            S3Object object = s3client.getObject(bucketName, dogName);
            if (object != null) {

                s3client.deleteObject(bucketName, dogName);
            }

            //Nothing to be done as there is no file with the given name in the bucket.
        }catch (Exception e) {
            throw new YabonzaException("Image could not be deleted in S3", e);
        }
    }


    // There is no api for S3 to retrieve the URL of the file uploaded
    private String getImageUrl(AmazonS3 client, String bucketName, String key) {

        return new StringBuilder("https://")
                .append(bucketName)
                .append(".s3-")
                .append(client.getRegion())
                .append(".amazonaws.com/")
                .append(key)
                .toString();

    }

}



