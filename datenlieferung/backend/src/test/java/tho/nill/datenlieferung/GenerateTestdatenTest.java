package tho.nill.datenlieferung;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import repositories.RechnungAuftragRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class GenerateTestdatenTest {
	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	protected MockMvc mockMvc;

	@Before
	public void init() {
		Check.clearDb(rechnungAuftragRepo);
	}

	public GenerateTestdatenTest() {
		super();
	}

	@Test
	public void returnMessageFromService() throws Exception {
		this.mockMvc.perform(get("/api/testdaten/generate")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("wurden generiert")));
		assertTrue(rechnungAuftragRepo.count() > 0);
	}

}