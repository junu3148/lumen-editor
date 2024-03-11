package com.lumeneditor.www.domain.myproject;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my-project/")
public class MyProjectController {


    // 프로젝트 생성
/*    @PostMapping("project")
    public void createProject(@RequestBody Projects projects){
        System.out.println("호출");
        System.out.println(projects);

    }*/

    @PostMapping("project")
    public void createProject(@RequestBody String projectName, @RequestBody int workspacesKey){
        System.out.println("호출");
        System.out.println(projectName);
        System.out.println(workspacesKey);

    }


}
