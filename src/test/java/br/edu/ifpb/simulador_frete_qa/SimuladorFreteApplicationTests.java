package br.edu.ifpb.simulador_frete_qa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") //Isola o ambiente de teste, garantindo que o banco de produção não seja afetado
class SimuladorFreteApplicationTests {

	@Test
	void contextLoads() {
	}

}
