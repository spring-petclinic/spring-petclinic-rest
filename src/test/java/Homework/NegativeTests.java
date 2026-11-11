package Homework;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class NegativeTests {

    private static RequestSpecification spec;
    private static int ownerId;
    private static int petId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:9966/petclinic";
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

        ownerId = given().spec(spec).body("""
        {"firstName": "Anna", "lastName": "Sidorova", "address": "Lenina", "city": "SPb", "telephone": "9876543210"}
        """)
                .when().post("/api/owners")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .extract().path("id");

        int catTypeId = given().spec(spec).body("{\"name\": \"cat\"}").when().post("/api/pettypes")
                .then().extract().path("id");

        petId = given().spec(spec).body("""
                {"name": "Barsik", "birthDate": "2023-05-05", "type": {"id": %d, "name": "cat"}}
                """.formatted(catTypeId))
                .when().post("/api/owners/" + ownerId + "/pets")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .extract().path("id");

        given().spec(spec).body("""
                {"date": "2026-01-01", "description": "Test visit"}
                """)
                .when().post("/api/owners/" + ownerId + "/pets/" + petId + "/visits")
                .then().log().ifValidationFails()
                .statusCode(anyOf(equalTo(200), equalTo(201)));
    }

    @Test
    public void negativeScenario_VisitForNonExistingPet() {
        int nonExistingPetId = petId + 1000;
        String visitDate = "2026-12-25";
        String visitDescription = "Visit for ghost pet";

        given().spec(spec).body("""
                {"date": "%s", "description": "%s"}
                """.formatted(visitDate, visitDescription))
                .when().post("/api/owners/" + ownerId + "/pets/" + nonExistingPetId + "/visits")
                .then().log().ifValidationFails()
                .statusCode(404);
    }
}
