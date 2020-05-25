package Domain;

import ch.qos.logback.core.net.server.Client;
import com.example.demo.model.Person;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpringRestClient {
    
    private static final String GET_PERSON_ENDPOINT_URL = "http://localhost:8082/api/v1/person";
    private static final String POST_PERSON_ENDPOINT_URL = "http://localhost:8082/api/v1/person";


    private static final String POST_USER_ENDPOINT_URL = "http://localhost:8082/api/v1/person";
    private static final String GET_USER_ENDPOINT_URL = "http://localhost:8082/api/v1/person";



    
    private static RestTemplate restTemplate = new RestTemplate();



    public static void main(String [] args) {
        SpringRestClient springRestClient = new SpringRestClient();
        //Client c = Client.create();
        
        //step1: create Person
        springRestClient.createPerson();
        System.out.println("End Post Method");

        //step2: get List Persons
        springRestClient.getPersonList();
        System.out.println("End Get Method");

        
        
    }










    private void getPersonList() {
        //-------------work with return json-----------------
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept((Arrays.asList(MediaType.APPLICATION_JSON)));
//        HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
//        ResponseEntity<String> result= restTemplate.exchange(GET_PERSON_ENDPOINT_URL,HttpMethod.GET,entity,String.class);
//        System.out.println(result);

        //-------------work with return object-----------------
        ResponseEntity<List<Person>> result= restTemplate.exchange(GET_PERSON_ENDPOINT_URL,HttpMethod.GET,null,
                new ParameterizedTypeReference<List<Person>>() {});
        List <Person> personsList = result.getBody();
        for(int i = 0; i<personsList.size();i++) {
            Person p = personsList.get(i);
            System.out.println("The Person name is: " + p.getName());
        }
    }


    private void createPerson() {
        UUID id = UUID.randomUUID();
        Person person = new Person (id,"Donald Tramp");
        RestTemplate restTemplate = new RestTemplate();
        boolean result = restTemplate.postForObject(POST_PERSON_ENDPOINT_URL,person,Boolean.class);
        if(result) {
            System.out.println(result + " - The Person inserted!");
        }
    }
}
