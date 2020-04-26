package nio.bg.workshop.reservationclient;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

@Component
public class ReservationClient {

    private final TcpClientTransport tcpClientTransport;

    private final ObjectMapper objectMapper;

    public ReservationClient (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.tcpClientTransport = TcpClientTransport.create(7000);
    }

    Flux<Reservation> getAllReservations() {
        return RSocketFactory
                .connect()
                .transport(this.tcpClientTransport)
                .start()
                .flatMapMany(rs ->
                        rs.requestStream(DefaultPayload.create(new byte[0]))
                                .map(Payload::getDataUtf8)
                                .map(this::to)
                );       
    }

    private Reservation to(String json) {
        try {
            return this.objectMapper.readValue(json, Reservation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
@Component
class ReservationClient {
	private final WebClient webClient;
	ReservationClient(WebClient webClient) {
		this.webClient = webClient;
	}
	Flux<Reservation> getAllReservations() {
		return this.webClient
			.get()
			.uri("http://localhost:8080/reservations")
			.retrieve()
			.bodyToFlux(Reservation.class);
	}
}*/

}