package com.yana.dbservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "directories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Builder.Default
    @OneToMany(mappedBy = "directory", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

}
