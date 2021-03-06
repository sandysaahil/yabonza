# Yabonza Test

Once hosted, application will be availble at

`http://localhost:8080/v1/dog - Generates a new Dog Breed (POST method)`

`http://localhost:8080/v1/dog/{id} - Retrieves the Dog details based on the id passed (GET method)`

`http://localhost:8080/v1/dog/{id} - Removes the record with the given id (DELETE method)`

`http://localhost:8080/v1/dog/search/{breed} - Retrieves all the dog breed information based on the given breed name (GET method)`

`http://localhost:8080/v1/dogs - Retrieves all the distinct dog breed names in the system (GET method)`

### Prerequisites
Application is built using Spring Boot, Java 1.8 and Maven. Java and Maven need to be installed before running the application.

### Running the application
Application is built using Maven. Following commands can be used to build and run the application

`<cmd> mvn clean - to clean the application`
 
`<cmd> mvn package - to create the package`

`<cmd> mvn spring-boot:run - to run the project`

A jar - **yabonza-0.0.1-SNAPSHOT.jar** - will be generated by using mvn package which can be run as a Java command

`<cmd>java -jar yabonza-0.0.1-SNAPSHOT.jar`

### AWS S3
I am using my credentials by default. If you want to change the S3 location you can override following  environment variable

jsa.aws.access_key_id
jsa.aws.secret_access_key

I have hardcoded the AWS S3 bucket name but it can be override easily by adding following environment variable

s3.bucket.name

S3 permission is only for the program user and not accessible publically. I will switch off the S3 once the test is reviewed.

![Environment Variables setting in IntelliJ](https://github.com/sandysaahil/yabonza/blob/master/src/main/resources/static/Environment%20Variables.png)

### Database

For simplicity, I am using in memory H2 Database which will be wiped everytime the application is stopped. So the ideal way to run is to first create some dogs in the system using APIs

To browse database, please use http://locahost:8080/h2-console and use following properties

![Database configurations](https://github.com/sandysaahil/yabonza/blob/master/src/main/resources/static/database%20config.png)

Password = password

### API Documentation (Swagger)
API documentation is available at (once application is deployed) - http://locahost:8080/swagger-ui.html
