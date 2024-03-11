package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.domain.auth.entity.WorkSpaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkSpacesRepository extends JpaRepository<WorkSpaces, Long> {


}
