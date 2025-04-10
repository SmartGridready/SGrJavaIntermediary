package com.smartgridready.intermediary.service;

import com.smartgridready.intermediary.controller.ExternalInterfaceXmlController;
import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalInterfaceXmlController.class)
class ExternalInterfaceXmlControllerTest {

    private static final  String FILE_NAME = "TEST.xml";
    private static final String TEST_XML = "<result>OK</result>";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntermediaryService intermediaryService;

    @Test
    void serviceCallSuccess() throws Exception {

        when(intermediaryService.loadEiXmlFromLocalFile(any(), any())).thenReturn(
                new ExternalInterfaceXml(FILE_NAME, TEST_XML));

        mockMvc.perform(multipart("/eiXml/local-file")
                .file("file", TEST_XML.getBytes(StandardCharsets.UTF_8)))
                .andExpect((status().isOk()))
                .andExpect(content().json(
                        "{\"name\":\"" + FILE_NAME + "\"," +
                                "\"xml\":\"" + TEST_XML + "\"}"));
    }

    @Test
    void serviceCallErrorNotFound() throws Exception {

        when(intermediaryService.loadEiXmlFromLocalFile(any(), any())).thenThrow(new ExtIfXmlNotFoundException(FILE_NAME));

        mockMvc.perform(multipart("/eiXml/local-file")
                        .file("file", TEST_XML.getBytes(StandardCharsets.UTF_8)))
                .andExpect((status().isNotFound()))
                .andExpect(content().json(
                        "{ \"className\":\"" + ExtIfXmlNotFoundException.class.getSimpleName() + "\","
                                + "\"errorMsg\":\"Could not find externalInterfaceXml: " + FILE_NAME + "\"}"));
    }

    @Test
    void serviceCallErrorBadRequest() throws Exception {

        when(intermediaryService.loadEiXmlFromLocalFile(any(), any())).thenThrow(new IllegalArgumentException("Parameter invalid"));

        mockMvc.perform(multipart("/eiXml/local-file")
                        .file("file", TEST_XML.getBytes(StandardCharsets.UTF_8)))
                .andExpect((status().isBadRequest()))
                .andExpect(content().json(
                        "{ \"className\":\"" + IllegalArgumentException.class.getSimpleName() + "\","
                                + "\"errorMsg\":\"Parameter invalid\"}"));


    }

}
