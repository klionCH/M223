package ch.kbw.sl.controller;
import ch.kbw.sl.entity.MediaFile;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.MediaFileService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MediaController {

    private final MediaFileService mediaFileService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        if (file.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            MediaFile mediaFile = mediaFileService.uploadFile(file, currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileId", mediaFile.getId());
            response.put("filename", mediaFile.getFilename());
            response.put("originalFilename", mediaFile.getOriginalFilename());
            response.put("fileSize", mediaFile.getFileSize());
            response.put("contentType", mediaFile.getContentType());
            response.put("url", "/api/media/" + mediaFile.getFilename());

            return ResponseEntity.ok(response);
        } catch (IOException | IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getMediaFile(@PathVariable String filename) {
        try {
            MediaFile mediaFile = mediaFileService.findByFilename(filename).orElse(null);
            if (mediaFile == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(mediaFile.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mediaFile.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + mediaFile.getOriginalFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<MediaFile> getMediaFileInfo(@PathVariable Long id) {
        return mediaFileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<MediaFile>> getAllMediaFiles(
            Pageable pageable,
            @RequestParam(required = false) String contentType) {

        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<MediaFile> mediaFiles = contentType != null
                ? mediaFileService.findByUserAndContentType(currentUser, contentType, pageable)
                : mediaFileService.findByUser(currentUser, pageable);

        return ResponseEntity.ok(mediaFiles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMediaFile(@PathVariable Long id) {
        try {
            mediaFileService.deleteFile(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "File not found");

            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete file");

            return ResponseEntity.internalServerError().body(response);
        }
    }
}