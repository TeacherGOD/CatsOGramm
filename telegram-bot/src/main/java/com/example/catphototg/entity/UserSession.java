package com.example.catphototg.entity;

import com.example.catphototg.entity.enums.UserState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static com.example.catphototg.constants.BotConstants.USER_ID_PARAM;

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
    @JoinColumn(name = USER_ID_PARAM, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @Column(name = "cat_name")
    private String catName;

    @Column(name = "photo_file_id")
    private String photoFileId;

    @Column(name = "file_path")
    private String filePath;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "current_page")
    private Integer currentPage = 0;

    @Column(name = "viewing_cat_id")
    private Long viewingCatId;

}
