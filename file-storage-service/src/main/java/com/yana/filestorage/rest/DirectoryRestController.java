package com.yana.filestorage.rest;

import com.yana.filestorage.dto.Node;
import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.entity.UserDetailsImpl;
import com.yana.filestorage.service.DirectoryService;
import com.yana.filestorage.service.StructureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directories")
@AllArgsConstructor
@Slf4j
public class DirectoryRestController {

    private final DirectoryService directoryService;
    private final StructureService structureService;

    @PreAuthorize("@UserAccessor.canUserAccessResource('dir', #directoryId)")
    @GetMapping
    public Node getDataForDir(@RequestParam("id") Long directoryId) {
        log.info("[Response] data for the directory with id {}", directoryId);
        return structureService.getDataForCertainDir(directoryId);
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('user', #userId)")
    @GetMapping("/user")
    public Node getAllDataForUser(@RequestParam("id") Long userId) {
        log.info("[Response] with directories and files data for the user with id {}", userId);
        return structureService.getRootDirsWithFilesForUser(userId);
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('dir', #directoryId)")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDirectory(@RequestParam("id") Long directoryId) {
        log.info("[RequestParams] deleting the directory with id {}", directoryId);
        directoryService.deleteDirectory(directoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Directory> createDirectory(@RequestParam String name,
                                                     @RequestParam(required = false) Long parentId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetailsImpl) authentication.getPrincipal();

        log.info("[Request] creating directory with name {} and parentId {}", name, parentId);
        Directory directory = directoryService.createDirectory(name, parentId, user.getId());

        log.info("[Response] created directory with id {}", directory.getId());
        return new ResponseEntity<>(directory, HttpStatus.CREATED);
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('dir', #directoryId)")
    @PutMapping("/rename")
    public ResponseEntity<Void> renameDirectory(@RequestParam Long directoryId,
                                                @RequestParam String newName) {
        log.info("[Request] renaming directory with id {} to {}", directoryId, newName);
        directoryService.renameDirectory(directoryId, newName);

        log.info("[Response] directory renamed successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
