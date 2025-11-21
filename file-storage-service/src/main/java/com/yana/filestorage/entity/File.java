package com.yana.filestorage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Entity
@Table(name = "files")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "DIRECTORY")
    private Directory directory;

    @Column(name = "USER_ID")
    private Long userId;

}
