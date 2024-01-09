package com.example.dezdemoniyslab.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "log_table")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private Long id;

    @Column(name = "log_type")
    @Enumerated(EnumType.STRING)
    private LogType logType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "description")
    private String description;
}
