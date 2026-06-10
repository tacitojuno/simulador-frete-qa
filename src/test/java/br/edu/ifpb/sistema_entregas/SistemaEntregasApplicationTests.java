package br.edu.ifpb.sistema_entregas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") //Isola o ambiente de teste, garantindo que o banco de produção não seja afetado
class SistemaEntregasApplicationTests {

	@Test
	void contextLoads() {
	}

}
