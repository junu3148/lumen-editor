package com.lumeneditor.www.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workspaces")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSpaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspacesKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_key") // DB 컬럼 이름과 일치해야 합니다.
    private User user; // User 엔티티 참조. User 클래스는 해당 관계에 맞게 정의되어야 합니다.
}