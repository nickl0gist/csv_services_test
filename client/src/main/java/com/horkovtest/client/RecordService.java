package com.horkovtest.client;

import com.horkovtest.client.exception.NoRecordExistsException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecordService {

    @Value("${settings.csv.separator}")
    private Character CSV_SEPARATOR;

    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    /**
     * Once the received file has CSV format the method reads it's content and {@linkplain #getRecordCollection(Reader)}
     * convert it to Collection of {@linkplain Record}s. After that method {@linkplain #getPossibleViolations(List)} checks
     * if there are any constraint violations of {@linkplain Record} class. If any violations were found return
     * {@linkplain HttpStatus} - 406. Otherwise, {@linkplain HttpStatus} - 202 returned with information how many rows were saved.
     *
     * @param file CSV {@linkplain MultipartFile} to be processed
     * @return {@linkplain ResponseEntity} with with String message of result.
     */
    public ResponseEntity<String> processCsvFile(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            List<Record> records = getRecordCollection(reader);

            StringBuilder str = getPossibleViolations(records);

            if (str.length() > 0) {
                //remove new line and spaces at the very end
                str.deleteCharAt(str.length()-1);
                str.deleteCharAt(str.length()-1);
                str.deleteCharAt(str.length()-1);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(str.toString());
            }

            saveAll(records);
            return ResponseEntity.accepted().body(records.size() + " records were saved.");

        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("An error occurred while processing the CSV file.");
        }
    }

    /**
     * Convert CSV file to {@linkplain Record} collection.
     *
     * @param reader {@linkplain Reader} of the given file.
     * @return List of {@linkplain Record}s
     */
    private List<Record> getRecordCollection(Reader reader) {
        CsvToBean<Record> csvToBean = new CsvToBeanBuilder(reader)
                .withSeparator(CSV_SEPARATOR)
                .withSkipLines(0)
                .withType(Record.class)
                .withIgnoreEmptyLine(true)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }

    /**
     * The method calls {@linkplain #validation validation(Record rec)} method for each record in the given collection.
     *
     * @param records List of {@linkplain Record}s
     * @return String with specified place of error.
     */
    private StringBuilder getPossibleViolations(List<Record> records) {
        StringBuilder str = new StringBuilder();
        records.forEach(r -> str.append(validation(r)));
        return str;
    }

    /**
     * Save the List of {@linkplain Record}s
     * @param records List of {@linkplain Record}s
     */
    private void saveAll(List<Record> records) {
        recordRepository.saveAll(records);
    }

    /**
     * Check  {@linkplain Record} entity for possible violations.
     *
     * @param rec {@linkplain Record} entity.
     * @return String message with indication specific information about the place of violation.
     * Empty String if no violations were found.
     */
    private String validation(Record rec) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Record>> constraintViolations = validator.validate(rec);
        if (!constraintViolations.isEmpty()) {

            String violations = constraintViolations.stream()
                    .map(cV -> cV.getPropertyPath() + " " + cV.getMessage() + "; ")
                    .collect(Collectors.joining());

            String message = String.format("%s would not be persisted: %s%n", rec.toString(), violations);
            log.error(message);
            return message;
        }
        return "";
    }

    public Record getRecordById(String id) throws NoRecordExistsException {
        return recordRepository.getRecordByPrimaryKey(id)
                .orElseThrow(() -> new NoRecordExistsException("Record with PRIMARY_KEY " + id + " does not exist."));
    }
}
