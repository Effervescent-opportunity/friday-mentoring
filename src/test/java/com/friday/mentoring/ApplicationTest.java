package com.friday.mentoring;

import com.friday.mentoring.service.ClockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.friday.mentoring.util.TestConstants.ISO_FORMATTER;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private ClockService clockService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getNowInUtcTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        MockHttpServletResponse response = mockMvc.perform(get("/time/current/utc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ZonedDateTime dateFromService = ZonedDateTime.parse(response.getContentAsString(), ISO_FORMATTER);

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).isAfter(dateFromService));
    }

    @Test
    public void getNowInTimezoneTest() throws Exception {
        String ianaTimezone = "Europe/Paris";
        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        MockHttpServletResponse response = mockMvc.perform(get("/time/current?timezone=" + ianaTimezone))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ZonedDateTime dateFromService = ZonedDateTime.parse(response.getContentAsString(), ISO_FORMATTER);

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).isAfter(dateFromService));
    }

    @Test
    public void getNowInIncorrectTimezoneTest() throws Exception {
        mockMvc.perform(get("/time/current?timezone=Asia/Paris"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect timezone")));
    }

}
