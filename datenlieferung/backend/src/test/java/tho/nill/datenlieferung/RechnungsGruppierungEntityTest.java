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

import entities.RechnungsGruppierung;
import repositories.RechnungsGruppierungRepository;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class RechnungsGruppierungEntityTest {

	@Autowired
	RechnungsGruppierungRepository rechnungsGruppierungRepo;

	@Autowired
	public MockMvc mockMvc;

	public RechnungsGruppierungEntityTest() {
		super();
	}

	@Test
	public void nichts() {

	}

	@Test
	public void requestCreate() throws Exception {
		long anz = rechnungsGruppierungRepo.count();
		this.mockMvc
				.perform(post("/api/entity/rechnungsGruppierung/insert").param("versenderIK", "999999999")
						.param("datenPrüfungsIK", "999999999").param("datenArt", DatenArt.CD_NUMMER.name())
						.param("verbindung", Verbindungsart.CD.name()).param("maxRechnungsAnzahl", "20"))
				.andDo(print()).andExpect(status().isOk());
		assertEquals(anz + 1L, rechnungsGruppierungRepo.count());
	}

	@Test
	public void requestUpdate() throws Exception {
		requestCreate();
		requestCreate();
		long anz = rechnungsGruppierungRepo.count();
		List<RechnungsGruppierung> liste = rechnungsGruppierungRepo.findAll();
		for (RechnungsGruppierung d : liste) {
			this.mockMvc.perform(put("/api/entity/rechnungsGruppierung/update/" + d.getRechnungsGruppierungId())
					.param("versenderIK", "999999999").param("datenPrüfungsIK", "999999999")
					.param("datenArt", DatenArt.CD_NUMMER.name()).param("verbindung", Verbindungsart.CD.name())
					.param("maxRechnungsAnzahl", "20")).andDo(print()).andExpect(status().isOk());
		}
		assertEquals(anz, rechnungsGruppierungRepo.count());
	}

	@Test
	public void requestDelete() throws Exception {
		requestCreate();
		requestCreate();
		long anz = rechnungsGruppierungRepo.count();
		List<RechnungsGruppierung> liste = rechnungsGruppierungRepo.findAll();
		for (RechnungsGruppierung d : liste) {
			this.mockMvc.perform(delete("/api/entity/rechnungsGruppierung/delete/" + d.getRechnungsGruppierungId()))
					.andDo(print()).andExpect(status().isOk());
		}
		assertEquals(0L, rechnungsGruppierungRepo.count());
	}

	@Test
	public void requestGet() throws Exception {
		requestCreate();
		requestCreate();
		long anz = rechnungsGruppierungRepo.count();
		List<RechnungsGruppierung> liste = rechnungsGruppierungRepo.findAll();
		for (RechnungsGruppierung d : liste) {
			this.mockMvc.perform(get("/api/entity/rechnungsGruppierung/get/" + d.getRechnungsGruppierungId()))
					.andDo(print()).andExpect(status().isOk());
		}
		assertEquals(anz, rechnungsGruppierungRepo.count());
	}

}