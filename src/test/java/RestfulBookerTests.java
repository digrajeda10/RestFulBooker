import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class RestfulBookerTests {

    RestfulBookerHelper helper = new RestfulBookerHelper();

    @Test
    public void obtenerTodasLasReservas() {
        // Usar la función auxiliar para obtener todas las reservas
        Response response = helper.getAllBookings();
        // Verificar que el código de estado sea 200 (OK)
        response.then().assertThat().statusCode(200);
        // Verificar que la lista de reservas no esté vacía
        response.then().assertThat().body("size()", not(0));
        response.then().log().body();
    }

    @Test
    public void obtenerReservaPorId() {
        int bookingId = 3; // Asumiendo que este ID existe
        Response response = helper.getBookingById(bookingId);
        response.then().assertThat().statusCode(200);
        // Verificar que los campos no sean nulos
        response.then().assertThat().body("firstname", Matchers.notNullValue());
        response.then().assertThat().body("lastname", Matchers.notNullValue());
        response.then().assertThat().body("totalprice", Matchers.notNullValue());
        response.then().assertThat().body("bookingdates", Matchers.notNullValue());
        response.then().assertThat().body("bookingdates.checkin", Matchers.notNullValue());
        response.then().assertThat().body("bookingdates.checkout", Matchers.notNullValue());
        response.then().log().body();
    }

    @Test
    public void obtenerReservaInexistente() {
        int nonExistentId = 9999; // ID que no existe
        Response response = helper.getNonExistentBooking(nonExistentId);
        // Verificar que el código de estado sea 404 (Not Found)
        response.then().assertThat().statusCode(404);
        // Verificar que el cuerpo de la respuesta contenga el texto 'Not Found'
        response.then().assertThat().body(Matchers.containsString("Not Found"));
        response.then().log().body();
    }

    @Test
    public void obtenerReservasConFiltros() {
        String firstName = "Sally";
        String lastName = "Wilson";
        Response response = helper.getBookingsWithFilters(firstName, lastName);
        response.then().assertThat().statusCode(200);
        // Verificar que la respuesta contenga un 'bookingid'
        response.then().assertThat().body("bookingid", Matchers.notNullValue());
        response.then().log().body();
    }

    @Test
    public void obtenerReservasConFiltrosInvalidos() {
        String firstName = "NombreInexistente";
        String lastName = "ApellidoInexistente";
        Response response = helper.getBookingsWithFilters(firstName, lastName);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("size()", equalTo(0)); // Se espera que no haya reservas
        response.then().log().body();
    }

    @Test
    public void crearReservaConDatosInvalidos() {
        String invalidPayload = "{ \"firstname\": \"\", \"lastname\": \"\" }"; // Payload inválido
        // Enviar la solicitud POST con datos inválidos
        Response response = RestAssured
                .given().contentType(ContentType.JSON).accept(ContentType.JSON).body(invalidPayload)
                .when().post("/booking");

        // Obtener el código de estado de la respuesta
        int statusCode = response.getStatusCode();
        if (statusCode == 500) {
            System.err.println("Error: Se esperaba un código de estado 400, pero se recibió un 500. Esto indica un posible bug en el servidor.");
        }

        // Existe un bug y el error debe ser 400 (Bad request)
        response.then().assertThat().statusCode(400);
        response.then().log().body();
    }

    @Test
    public void crearNuevaReserva() {
        // Usar la función auxiliar con datos válidos
        Response response = helper.createBooking("Jim", "Brown", 111, true, "2018-01-01", "2019-01-01");
        // Verificar que el código de estado sea 200 (OK)
        response.then().assertThat().statusCode(200);

        // Verificar los campos de la respuesta
        response.then().assertThat().body("booking.firstname", Matchers.equalTo("Jim"));
        response.then().assertThat().body("booking.lastname", Matchers.equalTo("Brown"));
        response.then().assertThat().body("booking.totalprice", Matchers.equalTo(111));
        response.then().assertThat().body("booking.bookingdates.checkin", Matchers.equalTo("2018-01-01"));
        response.then().assertThat().body("booking.bookingdates.checkout", Matchers.equalTo("2019-01-01"));
        response.then().assertThat().body("booking.additionalneeds", Matchers.equalTo("Breakfast"));

        response.then().log().body();
    }
}
