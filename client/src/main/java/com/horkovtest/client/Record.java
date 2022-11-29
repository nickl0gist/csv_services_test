package com.horkovtest.client;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "record_table")
public class Record {
    @Id
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[0-9A-Za-z]+$")
    @CsvBindByName(column = "PRIMARY_KEY")
    private String primaryKey;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9 ]+")
    @CsvBindByName(column = "NAME")
    private String name;

    @Pattern(regexp = "^[A-Za-z0-9 ]+")
    @CsvBindByName(column = "DESCRIPTION")
    private String description;

    @Pattern(regexp = "^[0-2][0-9]:[0-5][0-9]$")
    @CsvBindByName(column = "UPDATED_TIMESTAMP")
    private String timestamp;
}
