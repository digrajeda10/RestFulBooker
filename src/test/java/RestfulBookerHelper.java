import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

public class RestfulBookerHelper {

    // Constructor que configura la URL base y registra un parser para manejar respuestas en texto plano
    public RestfulBookerHelper() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.registerParser("text/plain", Parser.TEXT);
    }

    // Método para crear una nueva reserva
    public Response createBooking(String firstName, String lastName, int totalPrice, boolean depositPaid, String checkin, String checkout) {
        String payload = "{\n" +
                "    \"firstname\": \"" + firstName + "\",\n" +
                "    \"lastname\": \"" + lastName + "\",\n" +
                "    \"totalprice\": " + totalPrice + ",\n" +
                "    \"depositpaid\": " + depositPaid + ",\n" +
                "    \"bookingdates\": {\n" +
                "        \"checkin\": \"" + checkin + "\",\n" +
                "        \"checkout\": \"" + checkout + "\"\n" +
                "    },\n" +
                "    \"additionalneeds\": \"Breakfast\"\n" +
                "}";

        return RestAssured
                .given()
                .header("Content-Type", "application/json") // Especifica el tipo de contenido explícitamente
                .header("Accept", "application/json")       // Especifica que esperas una respuesta JSON
                .body(payload)
                .when().post("/booking");
    }

    // Método para obtener una reserva por ID
    public Response getBookingById(int bookingId) {
        return RestAssured
                .given().pathParam("id", bookingId) // Pasa el ID de la reserva
                .when().get("/booking/{id}");
    }

    // Método para obtener todas las reservas
    public Response getAllBookings() {
        return RestAssured
                .when().get("/booking");
    }

    // Método para obtener reservas con filtros
    public Response getBookingsWithFilters(String firstName, String lastName) {
        return RestAssured
                .given().queryParam("firstname", firstName).queryParam("lastname", lastName)
                .when().get("/booking");
    }

    // Método para intentar obtener una reserva inexistente
    public Response getNonExistentBooking(int bookingId) {
        return RestAssured
                .given().pathParam("id", bookingId) // Pasa el ID inexistente
                .when().get("/booking/{id}");
    }
}

