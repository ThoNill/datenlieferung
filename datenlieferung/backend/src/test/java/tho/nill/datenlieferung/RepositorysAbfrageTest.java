package tho.nill.datenlieferung;

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

import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.allgemein.DummyVoid;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class RepositorysAbfrageTest {

	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	private GenerateTestdatenService service;

	public RepositorysAbfrageTest() {
		super();
	}

	@Before
	public void init() {
		Check.clearDb(rechnungAuftragRepo, datenaustauschRepo, datenlieferungenRepo);
		try {
			service.service(DummyVoid.VOID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void getDatenlieferungListe() throws Exception {
		this.mockMvc.perform(get("/api/daten/datenlieferung")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void getRechnungsAuftragListe() throws Exception {
		this.mockMvc.perform(get("/api/daten/rechnungauftrag")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void getDatenAustauschListe() throws Exception {
		this.mockMvc.perform(get("/api/daten/datenaustausch")).andDo(print()).andExpect(status().isOk());
	}

}