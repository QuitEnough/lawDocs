package com.yana.dbservice.rest;

import com.yana.dbservice.dto.Node;
import com.yana.dbservice.service.DirectoryService;
import com.yana.dbservice.service.StructureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directories")
@AllArgsConstructor
@Slf4j
public class DirectoryRestController {

    private final DirectoryService directoryService;
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

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDirectory(@RequestParam("id") Long directoryId) {
        log.info("[RequestParams] deleting the directory with id {}", directoryId);
        directoryService.deleteDirectory(directoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
