package com.horkovtest.owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, String> {
    Optional<Record> getRecordByPrimaryKey(String key);
}
