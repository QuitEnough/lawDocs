package com.yana.filestorage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@Setter
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
