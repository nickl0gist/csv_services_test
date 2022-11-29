package com.horkovtest.owner;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "record_table")
public class Record {
    @Id
    private String primaryKey;

    private String name;

    private String description;

    private String timestamp;
}
