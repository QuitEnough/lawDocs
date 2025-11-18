package com.yana.filestorage.rest;

import com.yana.filestorage.dto.NodeBACKUP;
import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.exception.AccessDeniedException;
import com.yana.filestorage.service.DirectoryService;
import com.yana.filestorage.service.StructureService;
import com.yana.filestorage.service.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TokenExtractor tokenExtractor;

    @GetMapping
    public NodeBACKUP getDataForDir(@RequestParam("id") Long directoryId,
                                    HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        directoryService.ensureDirectoryOwnership(directoryId, user.userId());

        log.info("[Response] Data for directory {} for user {}", directoryId, user.userId());
        return structureService.getDataForCertainDir(directoryId);
    }

    @GetMapping("/user")
    public NodeBACKUP getAllDataForUser(@RequestParam("id") Long requestedUserId,
                                        HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        if (!user.userId().equals(requestedUserId)) {
            throw new AccessDeniedException("You can only access your own data");
        }

        log.info("[Response] with directories and files data for the user with id {}", requestedUserId);
        return structureService.getRootDirsWithFilesForUser(requestedUserId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDirectory(@RequestParam("id") Long directoryId,
                                                HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        directoryService.ensureDirectoryOwnership(directoryId, user.userId());

        log.info("[Request] Deleting directory {} for user {}", directoryId, user.userId());
        directoryService.deleteDirectory(directoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Directory> createDirectory(@RequestParam String name,
                                                     @RequestParam(required = false) Long parentId,
                                                     HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);

        log.info("[Request] creating directory with name {} and parentId {} for user {}", name, parentId, user.userId());
        Directory directory = directoryService.createDirectory(name, parentId, user.userId());

        log.info("[Response] created directory with id {}", directory.getId());
        return new ResponseEntity<>(directory, HttpStatus.CREATED);
    }

    @PutMapping("/rename")
    public ResponseEntity<Void> renameDirectory(@RequestParam Long directoryId,
                                                @RequestParam String newName,
                                                HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        directoryService.ensureDirectoryOwnership(directoryId, user.userId());

        log.info("[Request] renaming directory with id {} to '{}' for user {}", directoryId, newName, user.userId());
        directoryService.renameDirectory(directoryId, newName);

        log.info("[Response] directory renamed successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
