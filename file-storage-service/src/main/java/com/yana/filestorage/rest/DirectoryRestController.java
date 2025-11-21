package com.yana.filestorage.rest;

import com.yana.filestorage.api.client.DirectoriesApi;
import com.yana.filestorage.api.dto.DirectoryResponse;
import com.yana.filestorage.api.dto.Node;
import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.exception.AccessDeniedException;
import com.yana.filestorage.mapper.DirectoryMapper;
import com.yana.filestorage.service.DirectoryService;
import com.yana.filestorage.service.StructureService;
import com.yana.filestorage.service.TokenExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/directories")
@AllArgsConstructor
@Slf4j
public class DirectoryRestController implements DirectoriesApi {

    private final DirectoryService directoryService;
    private final StructureService structureService;
    private final TokenExtractor tokenExtractor;
    private final DirectoryMapper directoryMapper;

    @Override
    public ResponseEntity<Node> getAllDataForUser(String authorization,
                                                  Long requestedUserId) {
        var user = tokenExtractor.extractFromHeader(authorization);
        if (!user.getUserId().equals(requestedUserId)) {
            throw new AccessDeniedException("You can only access your own data");
        }

        log.info("[Response] with directories and files data for the user with id {}", requestedUserId);
        return ResponseEntity.ok(
                structureService.getRootDirsWithFilesForUser(requestedUserId)
        );
    }

    @Override
    public ResponseEntity<Node> getDataForDir(String authorization,
                                              Long directoryId) {
        var user = tokenExtractor.extractFromHeader(authorization);
        directoryService.ensureDirectoryOwnership(directoryId, user.getUserId());

        log.info("[Response] Data for directory {} for user {}", directoryId, user.getUserId());
        return ResponseEntity.ok(
                structureService.getDataForCertainDir(directoryId)
        );
    }

    @Override
    public ResponseEntity<DirectoryResponse> createDirectory(String authorization,
                                                             String name,
                                                             Long parentId) {
        var user = tokenExtractor.extractFromHeader(authorization);

        log.info("[Request] creating directory with name {} and parentId {} for user {}", name, parentId, user.getUserId());
        Directory directory = directoryService.createDirectory(name, parentId, user.getUserId());

        log.info("[Response] created directory with id {}", directory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                directoryMapper.toDto(directory)
        );
    }

    @Override
    public ResponseEntity<Void> renameDirectory(String authorization,
                                                Long directoryId,
                                                String newName) {
        var user = tokenExtractor.extractFromHeader(authorization);
        directoryService.ensureDirectoryOwnership(directoryId, user.getUserId());

        log.info("[Request] renaming directory with id {} to '{}' for user {}", directoryId, newName, user.getUserId());
        directoryService.renameDirectory(directoryId, newName);

        log.info("[Response] directory renamed successfully");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteDirectory(String authorization,
                                                Long directoryId) {
        var user = tokenExtractor.extractFromHeader(authorization);
        directoryService.ensureDirectoryOwnership(directoryId, user.getUserId());

        log.info("[Request] Deleting directory {} for user {}", directoryId, user.getUserId());
        directoryService.deleteDirectory(directoryId);
        return ResponseEntity.ok().build();
    }

}
