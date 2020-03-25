package tho.nill.datenlieferung;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class ZertifikateVonDerItsgEinlesenTest {

	@Autowired
	private ZertifikatRepository certifikatRepo;

	@Autowired
	private VersenderKeyRepository keyRepo;

	public ZertifikateVonDerItsgEinlesenTest() {
		super();
	}

	@Before
	public void init() {
		Check.clearDb(certifikatRepo, keyRepo);
	}

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void getDatenlieferungListe() throws Exception {

		this.mockMvc.perform(get("/api/action/itsg/einlesen")).andDo(print()).andExpect(status().isOk());
		assertTrue(certifikatRepo.count() > 5);

	}

}