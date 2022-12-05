package com.horkovtest.client;

import com.horkovtest.client.exception.NoRecordExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController()
@RequestMapping("api/v1/client")
public class ClientController {

    private final RecordService recordService;

    @Autowired
    public ClientController(RecordService recordService) {
        this.recordService = recordService;
    }

    /**
     * GET request in order to obtain {@linkplain Record} by given Id.
     * @param id String Primary_key of the {@linkplain Record}.
     * @return {@linkplain Record}
     * @throws NoRecordExistsException in case when {@linkplain Record} wasn't found.
     */
    @GetMapping("/{id:^[0-9A-Za-z]+$}")
    public ResponseEntity<Record> getRecordById(@PathVariable String id) throws NoRecordExistsException {
        Record rec = recordService.getRecordById(id);
        return ResponseEntity.ok(rec);
    }

    /**
     * In case of empty file or file format other than "text/csv" returns {@linkplain HttpStatus} 400. Otherwise,
     * {@linkplain HttpStatus} 202 returned with information how many rows were saved.
     * @param file received from client.
     * @return {@linkplain ResponseEntity} with String message of result.
     */
    @PostMapping(path = "/upload_csv")
    public ResponseEntity<String> registerRecord(@RequestParam(value = "file") MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().equals("text/csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a CSV file to upload.");
        } else {
            return recordService.processCsvFile(file);
        }
    }

}
