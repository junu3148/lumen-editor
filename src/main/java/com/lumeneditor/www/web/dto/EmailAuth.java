package com.lumeneditor.www.web.dto;


import com.lumeneditor.www.comm.eunm.YesNo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuth {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailAuthKey;

    @Column(unique = true)
    private String authEmail;

    private String authCode;

    @Enumerated(EnumType.STRING)
    private YesNo authStatus;

    @PrePersist
    void prePersist() {
        if (authStatus == null) {
            authStatus = YesNo.N;
        }
    }


}
