package se.mdh.driftavbrott.rest;

import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.service.IcService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DriftavbrottController.class)
public class DriftavbrottControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private IcService icService;

  private static String base = "/v1/driftavbrott";

  @Test
  public void getIngetPagaendeDriftavbrott() throws Exception {
      when(icService.getPagaendeDriftavbrott(any(List.class), anyInt())).thenReturn(Optional.empty());
      this.mockMvc.perform(get(base + "/pagaende").
          param("system", "DriftavbrottControllerTest")).
          andExpect(status().is4xxClientError());
  }

  @Test
  public void getPagaendeDriftavbrott() throws Exception {
    when(icService.getPagaendeDriftavbrott(any(List.class), anyInt())).thenReturn(Optional.of(new Driftavbrott()));
    this.mockMvc.perform(get(base + "/pagaende").
        param("system", "DriftavbrottControllerTest")).
        andExpect(status().is2xxSuccessful());
  }
}