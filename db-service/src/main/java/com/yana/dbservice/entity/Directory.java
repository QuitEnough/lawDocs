package com.yana.dbservice.entity;

import jakarta.persistence.*;
import lombok.*;

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

}
