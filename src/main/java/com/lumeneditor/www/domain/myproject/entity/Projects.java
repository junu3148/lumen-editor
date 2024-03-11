package com.lumeneditor.www.domain.myproject.entity;


import com.lumeneditor.www.comm.eunm.YesNo;
import com.lumeneditor.www.domain.auth.entity.WorkSpaces;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectKey;

    @Column(nullable = false)
    private String projectName;

    @Enumerated(EnumType.STRING)
    private YesNo disclosureStatus;

    @Column(nullable = false)
    private LocalDateTime projectDate;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspaces_key", nullable = false)
    private WorkSpaces workSpaces;

    @PrePersist
    protected void onCreate() {
        if (disclosureStatus == null) {
            disclosureStatus = YesNo.N;
        }
    }


}

