package com.yana.dbservice.rest;

import com.yana.dbservice.dto.Node;
import com.yana.dbservice.service.StructureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/directories")
@AllArgsConstructor
@Slf4j
public class DirectoryRestController {

    private final StructureService structureService;

    @GetMapping
    public Node getDataForDir(@RequestParam("id") Long dirId) {
        log.info("[Response] data for the directory with id {}", dirId);
        return structureService.getDataForCertainDir(dirId);
    }

    @GetMapping("/user")
    public Node getAllDataForUser(@RequestParam("id") Long userId) {
        log.info("[Response] with directories and files data for the user with id {}", userId);
        return structureService.getRootDirsWithFilesForUser(userId);
    }

}
