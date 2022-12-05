package com.horkovtest.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest {

    private final String BASE_URL = "/api/v1/client/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordRepository recordRepository;

    @BeforeEach
    void clearDb() {
        recordRepository.deleteAll();
    }

    @Test
    void getRecordByIdWithNotExistingPrimaryKey() throws Exception {
        //Given
        String recordPrimKey = "aa1";

        //When
        mockMvc.perform(get(BASE_URL + recordPrimKey))

                //Then
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Record with PRIMARY_KEY " + recordPrimKey + " does not exist."));
    }

    @Test
    void getRecordByIdWithExistingPrimaryKey() throws Exception {
        //Given
        String recordPrimKey = "aa1";
        Record r = new Record.RecordBuilder()
                .primaryKey(recordPrimKey)
                .name("SomeName")
                .description("Some Description")
                .timestamp("22:05")
                .build();

        recordRepository.save(r);

        //When
        mockMvc.perform(get(BASE_URL + recordPrimKey))

                //Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value(recordPrimKey))
                .andExpect(jsonPath("$.name").value(r.getName()))
                .andExpect(jsonPath("$.description").value(r.getDescription()))
                .andExpect(jsonPath("$.timestamp").value(r.getTimestamp()));
    }

    @Test
    void processCsvFileOkTest() throws Exception {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Book1.csv").getFile());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "",
                "text/csv",
                Files.readAllBytes(file.toPath()));

        //When
        mockMvc.perform(multipart(BASE_URL + "/upload_csv")
                .file(mockMultipartFile))

                // Then
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().string("36 records were saved."));
    }

    @Test
    void processCsvFileBadRequestTest() throws Exception {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("POST_CSV.http").getFile());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                Files.readAllBytes(file.toPath()));

        //When
        mockMvc.perform(multipart(BASE_URL + "/upload_csv")
                .file(mockMultipartFile))

                // Then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please select a CSV file to upload."));
    }
}