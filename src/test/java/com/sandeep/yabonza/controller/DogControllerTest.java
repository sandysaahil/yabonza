package com.sandeep.yabonza.controller;

import com.sandeep.yabonza.domain.RandomDogImage;
import com.sandeep.yabonza.repository.DogRepository;
import com.sandeep.yabonza.repository.entity.Dog;
import com.sandeep.yabonza.service.ImageService;
import com.sandeep.yabonza.service.YabonzaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DogController.class)
public class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private YabonzaService yabonzaService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ImageService imageService;

    @MockBean
    private DogRepository dogRepository;

    private Dog dog;
    private String dateFormat;

    @Before
    public void setup() throws ParseException {
        Date dogCreationDate = Calendar.getInstance().getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat = formatter.format(dogCreationDate);
        dog = new Dog(1l,
                "retriever-golden",
                "https://images.dog.ceo/breeds/retriever-golden/n02099601_3508.jpg",
                dogCreationDate);
    }

    @Test
    public void getDogSuccess() throws Exception {
        Mockito.when(yabonzaService.getDogWithId(Mockito.anyLong())).thenReturn(Optional.of(dog));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/dog/1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"dogId\":1,\"dogName\":\"retriever-golden\",\"dogImageUrl\":\"https://images.dog.ceo/breeds/retriever-golden/n02099601_3508.jpg\",\"creationTime\":\"" +dateFormat+ "\"}";
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void getDogWithIncorrectId() throws Exception {
        Mockito.when(yabonzaService.getDogWithId(Mockito.anyLong())).thenReturn(Optional.of(dog));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/dog/aadfasdfadfs");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(result.getResolvedException().getClass(), ResponseStatusException.class);
    }

    @Test
    public void getDogWithNoResult() throws Exception {
        Mockito.when(yabonzaService.getDogWithId(Mockito.anyLong())).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/dog/1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(result.getResolvedException().getClass(), ResponseStatusException.class);
    }

    @Test
    public void storeDog() throws Exception {

        URI location = new URI("https://dog.ceo/api/breeds/image/random");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        responseHeaders.set("MyResponseHeader", "MyValue");
        RandomDogImage randomDogImage = new RandomDogImage();
        randomDogImage.setMessage("https://images.dog.ceo/breeds/bouvier/n02106382_7656.jpg");
        randomDogImage.setStatus("success");

        Mockito.when(yabonzaService.storeDog()).thenReturn(dog);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<>(randomDogImage, responseHeaders, HttpStatus.CREATED));
        Mockito.when(imageService.storeDog(Mockito.anyString(), Mockito.anyString())).thenReturn("https://images.dog.ceo/breeds/retriever-golden/n02099601_3508.jpg");
        Mockito.when(dogRepository.save(Mockito.any(Dog.class))).thenReturn(dog);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/dog");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String expected = "{\"dogId\":1,\"dogName\":\"retriever-golden\",\"dogImageUrl\":\"https://images.dog.ceo/breeds/retriever-golden/n02099601_3508.jpg\",\"creationTime\":\"" +dateFormat+ "\"}";
        JSONAssert.assertEquals(expected, response.getContentAsString(), false);
    }
}
