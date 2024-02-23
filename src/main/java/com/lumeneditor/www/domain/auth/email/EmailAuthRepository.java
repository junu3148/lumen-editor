package com.lumeneditor.www.domain.auth.email;

import com.lumeneditor.www.web.dto.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

 }