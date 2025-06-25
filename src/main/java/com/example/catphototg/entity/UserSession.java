package com.example.catphototg.entity;

import com.example.catphototg.entity.enums.UserState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @Column(name = "cat_name")
    private String catName;

    @Column(name = "photo_file_id")
    private String photoFileId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
