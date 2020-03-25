package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.senden.email.EMailConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class SendeZertifikatRequestTest {

	@Autowired
	private ZertifikatRepository certifikatRepo;

	@Autowired
	private VersenderKeyRepository keyRepo;

	@Autowired
	public EMailConfiguration config;

	@Autowired
	protected MockMvc mockMvc;

	public EMailServer server;

	public SendeZertifikatRequestTest() {
		super();
	}

	@Before
	public void init() {
		try {
			Check.clearDb(certifikatRepo, keyRepo);
			server = new EMailServer(config.BENUTZERNAME, config.PASSWORT, config.PORT, config.HOST);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@After
	public void deinit() {
		server.after();
	}

	@Test
	public void requestErstellenUndSenden() throws Exception {
		this.mockMvc
				.perform(post("/api/action/itsg/request").param("ik", "999999999").param("name", "Herr Thomas Nill"))
				.andDo(print()).andExpect(status().isOk());
		assertEquals(1L, keyRepo.count());

	}

}