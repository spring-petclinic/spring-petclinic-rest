package Homework;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PositiveTests {

    private static RequestSpecification spec;
    private static int ownerId;
    private static int petId;
    private static int vetId;
    private static String vetSpecialtyName;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:9966/petclinic";
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

        ownerId = given().spec(spec).body("""
        {"firstName": "Ivan", "lastName": "Petrov", "address": "Tverskaya", "city": "Moscow", "telephone": "1234567890"}
        """)
                .when().post("/api/owners")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .extract().path("id");


        int catTypeId = findOrCreatePetType("cat");
        petId = given().spec(spec).body("""
                {"name": "Murzik", "birthDate": "2023-01-15", "type": {"id": %d, "name": "cat"}}
                """.formatted(catTypeId))
                .when().post("/api/owners/" + ownerId + "/pets")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .extract().path("id");

        List<Map<String, Object>> vets = given().spec(spec).when().get("/api/vets")
                .then().log().ifValidationFails()
                .statusCode(200)
                .extract().jsonPath().getList("");

        if (vets.isEmpty()) {
            throw new IllegalStateException("Не удалось получить список ветеринаров!");
        }

        Map<String, Object> firstVet = vets.get(0);
        vetId = (Integer) firstVet.get("id");

        List<Map<String, Object>> specialties = (List<Map<String, Object>>) firstVet.get("specialties");
        if (specialties.isEmpty()) {
            vetSpecialtyName = "General";
        } else {
            vetSpecialtyName = (String) specialties.get(0).get("name");
        }
    }

    @Test
    public void positiveScenario_CreateAndVerifyVisit() {
        String visitDate = "2026-12-20";
        String visitDescription = "Checkup for " + vetSpecialtyName;

        given().spec(spec).body("""
                {"date": "%s", "description": "%s"}
                """.formatted(visitDate, visitDescription))
                .when().post("/api/owners/" + ownerId + "/pets/" + petId + "/visits")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)));

        given().spec(spec)
                .when().get("/api/owners/" + ownerId + "/pets/" + petId)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("visits.find { it.description == '" + visitDescription + "' }.date", equalTo(visitDate));
    }

    private static int findOrCreatePetType(String name) {
        List<Map<String, Object>> types = given().spec(spec).when().get("/api/pettypes")
                .then().log().ifValidationFails()
                .extract().jsonPath().getList("");

        for (Map<String, Object> type : types) {
            if (name.equals(type.get("name"))) {
                return (Integer) type.get("id");
            }
        }

        return given().spec(spec).body("{\"name\": \"" + name + "\"}")
                .when().post("/api/pettypes")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .extract().path("id");
    }
}
