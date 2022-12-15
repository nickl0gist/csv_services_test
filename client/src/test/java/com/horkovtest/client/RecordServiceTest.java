package com.horkovtest.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;
    private AutoCloseable autoCloseable;
    private RecordService recordService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        recordService = new RecordService(recordRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void saveAllRecordsWithFile() throws IOException {
        //When
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Book1.csv").getFile());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "",
                "text/csv",
                Files.readAllBytes(file.toPath()));
    }

    @Test
    void saveAllRecords() {
        Record r1 = new Record.RecordBuilder()
                .primaryKey("qq1")
                .name("Quoquo1")
                .description("Description1")
                .timestamp("20:01")
                .build();
        Record r2 = new Record.RecordBuilder()
                .primaryKey("oo2")
                .name("OooAaaaIiiLllBbb1")
                .description("Description2")
                .timestamp("15:51")
                .build();
    }
}
