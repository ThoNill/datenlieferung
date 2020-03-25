package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import entities.Datenaustausch;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class DatenaustauschEntityTest {

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;
	@Autowired
	public MockMvc mockMvc;

	public DatenaustauschEntityTest() {
		super();
	}

	@Test
	public void nichts() {

	}

	@Test
	public void requestCreate() throws Exception {
		long anz = datenaustauschRepo.count();
		this.mockMvc.perform(post("/api/entity/datenaustausch/insert").param("versenderIK", "999999999")
				.param("datenAnnahmeIK", "999999999").param("datenPrüfungsIK", "999999999")
				.param("richtung", Richtung.AUSGANG.name()).param("datenArt", DatenArt.CD_NUMMER.name())
				.param("verbindung", Verbindungsart.CD.name()).param("host", "localhost").param("port", "22")
				.param("hostVerzeichnis", "/").param("emailTo", "email@To").param("emailFrom", "email@From")
				.param("loginNutzer", "nutzer").param("loginPasswort", "password").param("name", "name")
				.param("strasse", "Zum Steeg 5").param("plz", "74541").param("annahmeClassName", "")
				.param("codepage", "UTF8")).andDo(print()).andExpect(status().isOk());
		assertEquals(anz + 1L, datenaustauschRepo.count());
	}

	@Test
	public void requestUpdate() throws Exception {
		requestCreate();
		requestCreate();
		long anz = datenaustauschRepo.count();
		List<Datenaustausch> liste = datenaustauschRepo.findAll();
		for (Datenaustausch d : liste) {
			this.mockMvc.perform(put("/api/entity/datenaustausch/update/" + d.getDatenaustauschId())
					.param("versenderIK", "999999999").param("datenAnnahmeIK", "999999999")
					.param("datenPrüfungsIK", "999999999").param("richtung", Richtung.AUSGANG.name())
					.param("datenArt", DatenArt.CD_NUMMER.name()).param("verbindung", Verbindungsart.CD.name())
					.param("host", "localhost").param("port", "22").param("hostVerzeichnis", "/")
					.param("emailTo", "email@To").param("emailFrom", "email@From").param("loginNutzer", "nutzer")
					.param("loginPasswort", "password").param("name", "name").param("strasse", "Zum Steeg 5")
					.param("plz", "74541").param("annahmeClassName", "").param("codepage", "UTF8")).andDo(print())
					.andExpect(status().isOk());
		}
		assertEquals(anz, datenaustauschRepo.count());
	}

	@Test
	public void requestDelete() throws Exception {
		requestCreate();
		requestCreate();
		long anz = datenaustauschRepo.count();
		List<Datenaustausch> liste = datenaustauschRepo.findAll();
		for (Datenaustausch d : liste) {
			this.mockMvc.perform(delete("/api/entity/datenaustausch/delete/" + d.getDatenaustauschId())).andDo(print())
					.andExpect(status().isOk());
		}
		assertEquals(0L, datenaustauschRepo.count());
	}

	@Test
	public void requestGet() throws Exception {
		requestCreate();
		requestCreate();
		long anz = datenaustauschRepo.count();
		List<Datenaustausch> liste = datenaustauschRepo.findAll();
		for (Datenaustausch d : liste) {
			this.mockMvc.perform(get("/api/entity/datenaustausch/get/" + d.getDatenaustauschId())).andDo(print())
					.andExpect(status().isOk());
		}
		assertEquals(anz, datenaustauschRepo.count());
	}

}