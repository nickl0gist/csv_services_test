package com.horkovtest.client;

import com.horkovtest.client.exception.NoRecordExistsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController()
@RequestMapping("api/v1/client")
public class ClientController {

    public static final String PLEASE_SELECT_A_CSV_FILE_TO_UPLOAD = "Please select a CSV file to upload.";
    private final RecordService recordService;

    @Autowired
    public ClientController(RecordService recordService) {
        this.recordService = recordService;
    }

    /**
     * GET request in order to obtain {@linkplain Record} by given Id.
     *
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
    @PostMapping(path = "/upload_csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerRecord(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().equals("text/csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(PLEASE_SELECT_A_CSV_FILE_TO_UPLOAD);
        } else {
            return ResponseEntity.ok().header("X-Large-Upload", "regular")
                    .body(recordService.processCsvFile(file));
        }
    }

    /**
     * Endpoint used to upload large files in order to not keep them in memory.
     * @param request Request with attached file. Must contain custom header "X-Large-Upload" with "large" value.
     * @return {@linkplain ResponseEntity} with String message of result.
     */
    @PostMapping("/upload_large_csv")
    public ResponseEntity<String> uploadLargeCsvFile(HttpServletRequest request){
        if(!ServletFileUpload.isMultipartContent(request)){
            throw new RuntimeException(PLEASE_SELECT_A_CSV_FILE_TO_UPLOAD);
        }
        ServletFileUpload upload = new ServletFileUpload();
        try{
            return ResponseEntity.ok().header("X-Large-Upload", "large").body(recordService.processLargeCsvFile(upload.getItemIterator(request)));

        } catch (FileUploadException | IOException e) {
            throw new RuntimeException(PLEASE_SELECT_A_CSV_FILE_TO_UPLOAD);
        }
    }
}
