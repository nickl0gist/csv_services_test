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
                .file(mockMultipartFile)
                .header("Content-Type", "text/csv"))

                // Then
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().string("36 records were saved."));
    }

    @Test
    void processCsvFileWithViolatedConstraintsTest() throws Exception {
        // Given
        String resp = "Record(primaryKey=a_1, name=name1, description=Description1, timestamp=12:00) would not be persisted: primaryKey must match \"^[0-9A-Za-z]+$\"; \r\n" +
                "Record(primaryKey=a2, name=name2/, description=Description2, timestamp=22:00) would not be persisted: name must match \"^[A-Za-z0-9 ]+\"; \r\n" +
                "Record(primaryKey=a3, name=name3, description=Description3#, timestamp=08:00) would not be persisted: description must match \"^[A-Za-z0-9 ]+\"; \r\n" +
                "Record(primaryKey=a4, name=name4, description=Description4, timestamp=18:78) would not be persisted: timestamp must match \"^[0-2][0-9]:[0-5][0-9]$\";";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Book_Constraint_Violations.csv").getFile());
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
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(resp));
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

    @Test
    void processCsvFileInternalServerErrorTest() throws Exception {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("POST_CSV.http").getFile());
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
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while processing the CSV file."));
    }
}