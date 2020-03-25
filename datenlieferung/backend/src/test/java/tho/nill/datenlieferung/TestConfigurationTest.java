package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.testUnterstützung.TestConfiguration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestConfigurationTest {

	@Autowired
	TestConfiguration config;

	public TestConfigurationTest() {
		super();
	}

	@Test
	public void configuration() {
		log.debug("Test der Konfiguration");
		assertEquals("tnill", config.BENUTZERNAME);
	}
}
