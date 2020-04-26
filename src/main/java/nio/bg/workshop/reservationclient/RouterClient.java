package nio.bg.workshop.reservationclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.reactivestreams.Publisher;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.context.annotation.Bean;

@Component
public class RouterClient {

    @Bean
    RouterFunction<ServerResponse> routes(ReservationClient client) {
        return route(GET("/reservations/name"), sr -> {

            Flux<String> names = client.getAllReservations().map(Reservation::getName);

            Publisher<String> cb = HystrixCommands
                    .from(names)
                    .eager()
                    .commandName("names")
                    .fallback(Flux.just("EKEKE!"))
                    .build();

            /*	// hedging
			WebClient wc = null; ///
			DiscoveryClient dc = null; ///
			List<ServiceInstance> instances = dc.getInstances("foo-service");
			if (instances.size() >= 3) {
				List<ServiceInstance> serviceInstances = instances.subList(0, 3);
				Flux<Reservation> callThreeServicesAtTheSameTime = Flux
					.fromStream(serviceInstances.stream())
					.map(si -> si.getHost() + ':' + si.getPort())
					.flatMap(uri -> wc.get().uri(uri).retrieve().bodyToFlux(Reservation.class));
				Flux<Reservation> first = Flux.first(callThreeServicesAtTheSameTime);
			}
*/
            
            return ServerResponse.ok().body(cb, String.class);
 
        });                                            
    }

    /*
    
    @Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.build();
    }
    
    */ 

}