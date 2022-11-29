package com.horkovtest.owner;

import com.horkovtest.owner.exception.NoRecordExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OwnerRecordService {

    private final RecordRepository recordRepository;

    @Autowired
    public OwnerRecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    private Record getRecordByPrimaryKey(String id) throws NoRecordExistsException {
        return recordRepository.getRecordByPrimaryKey(id)
                .orElseThrow(() ->new NoRecordExistsException("Record with PRIMARY_KEY " + id + " does not exist."));
    }

    public void deleteRecordById(String id) throws NoRecordExistsException {
        Record rec = getRecordByPrimaryKey(id);
        recordRepository.delete(rec);
    }
}