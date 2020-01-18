package com.sandeep.yabonza.service;

import com.amazonaws.SdkClientException;
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

/**
 * AWS S3 specific class to manage uploading images, Removing Images and Creating AWS S3 buckets
 */
@Service
public class S3ImageService implements ImageService {

    @Autowired
    private AmazonS3 s3client;

    // read from environment variables to maintain security and configurability
    @Value("${s3.bucket.name:yabonza-dogs-store}")
    private String bucketName;

    private static final String s3BucketNameRegex = "^([a-z]|(d(?!d{0,2}.d{1,3}.d{1,3}.d{1,3})))([a-zd]|(.(?!(.|-)))|(-(?!.))){1,61}[a-zd.]$";

    @Autowired
    public S3ImageService(final AmazonS3 s3client) {
        this.s3client = s3client;
    }


    /**
     * Stores the image in S3 in the bucket name specified. If bucket does not exist, it will create a new bucket and store image
     *
     * @param dogBreedName
     * @param imageUrl
     * @return
     */
    @Override
    public String storeDog(final String dogBreedName, String imageUrl) {

        String s3ImageUrl = null;

        if(!s3client.doesBucketExist(bucketName)) {
            createS3Bucket(bucketName);
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

    /**
     * Removes the dog breed image from the S3. If the bucket does not exist, does nothing
     *
     * @param dogBreedName
     */
    @Override
    public void removeDogFromStore(String dogBreedName) {

        try {

            if (!s3client.doesBucketExist(bucketName)) {
                return;
            }

            S3Object object = s3client.getObject(bucketName, dogBreedName);
            if (object != null) {

                s3client.deleteObject(bucketName, dogBreedName);
            }

            //Nothing to be done as there is no file with the given name in the bucket.
        } catch (Exception e) {
            throw new YabonzaException("Image could not be deleted in S3", e);
        }
    }

    /**
     * Creates S3 bucket if name matches the S3 guidelines
     *
     * @param bucketName
     */
    private void createS3Bucket(final String bucketName) {

        if(StringUtils.isEmpty(bucketName) || !bucketName.matches(s3BucketNameRegex)) {
            throw new YabonzaException("The name of the bucket from environment variables is not matching S3 bucket naming guidelines. The name of the bucket is : "+bucketName);
        }

        try {
            s3client.createBucket(bucketName);
        }catch (SdkClientException ex) {
            throw new YabonzaException("Bucket with name "+ bucketName + " could not be created due to exception", ex);
        }
    }


    /**
     * Creates the S3 URL of the image.
     *
     * @param client
     * @param bucketName
     * @param key
     * @return
     */
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