package com.horkovtest.owner;

import com.horkovtest.owner.exception.NoRecordExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/owner")
public class OwnerController {

    private final OwnerRecordService recordService;

    @Autowired
    public OwnerController(OwnerRecordService recordService) {
        this.recordService = recordService;
    }


    @DeleteMapping("/{id:^[0-9A-Za-z]+$}")
    public ResponseEntity<String> deleteRecordById(@PathVariable String id) throws NoRecordExistsException {
        recordService.deleteRecordById(id);
        return ResponseEntity.ok("Record with PRIMARY_KEY " + id + " has been removed.");
    }
}
