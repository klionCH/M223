package com.example.demo.controller;

import com.example.demo.entity.MediaFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@CrossOrigin(origins = "*")
public class MediaController {

    private static List<MediaFile> mediaFiles = new ArrayList<>();
    private static final String UPLOAD_DIR = "uploads/";

    static {
        // Static sample media files
        MediaFile media1 = new MediaFile(1L, "sunset_12345.jpg", "sunset.jpg",
                "image/jpeg", 245760L);
        media1.setFilePath(UPLOAD_DIR + "sunset_12345.jpg");
        media1.setUploadedBy(1L);

        MediaFile media2 = new MediaFile(2L, "video_67890.mp4", "process.mp4",
                "video/mp4", 5242880L);
        media2.setFilePath(UPLOAD_DIR + "video_67890.mp4");
        media2.setUploadedBy(2L);

        mediaFiles.add(media1);
        mediaFiles.add(media2);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Simulate file storage (in real app, would save to filesystem or cloud storage)
            MediaFile mediaFile = new MediaFile(
                    (long) (mediaFiles.size() + 1),
                    uniqueFilename,
                    originalFilename,
                    file.getContentType(),
                    file.getSize()
            );
            mediaFile.setFilePath(UPLOAD_DIR + uniqueFilename);
            mediaFile.setUploadedBy(1L); // Simulate current user

            mediaFiles.add(mediaFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileId", mediaFile.getId());
            response.put("filename", uniqueFilename);
            response.put("originalFilename", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("url", "/media/" + uniqueFilename);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Map<String, Object>> getMediaFile(@PathVariable String filename) {
        // Find media file by filename
        MediaFile mediaFile = mediaFiles.stream()
                .filter(mf -> mf.getFilename().equals(filename))
                .findFirst()
                .orElse(null);

        if (mediaFile == null) {
            return ResponseEntity.notFound().build();
        }

        // In a real application, you would return the actual file content
        // For this example, we return file metadata
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("id", mediaFile.getId());
        fileInfo.put("filename", mediaFile.getFilename());
        fileInfo.put("originalFilename", mediaFile.getOriginalFilename());
        fileInfo.put("contentType", mediaFile.getContentType());
        fileInfo.put("fileSize", mediaFile.getFileSize());
        fileInfo.put("uploadedBy", mediaFile.getUploadedBy());
        fileInfo.put("uploadedAt", mediaFile.getUploadedAt());
        fileInfo.put("url", "/media/" + filename);

        return ResponseEntity.ok(fileInfo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMediaFile(@PathVariable Long id) {
        boolean removed = mediaFiles.removeIf(mf -> mf.getId().equals(id));

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "File not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MediaFile>> getAllMediaFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String contentType) {

        // Simulate current user
        Long currentUserId = 1L;

        List<MediaFile> userMediaFiles = mediaFiles.stream()
                .filter(mf -> mf.getUploadedBy().equals(currentUserId))
                .filter(mf -> contentType == null || mf.getContentType().startsWith(contentType))
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(userMediaFiles);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<MediaFile> getMediaFileInfo(@PathVariable Long id) {
        MediaFile mediaFile = mediaFiles.stream()
                .filter(mf -> mf.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (mediaFile != null) {
            return ResponseEntity.ok(mediaFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Utility method to validate file types
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/") ||
                        contentType.startsWith("video/") ||
                        contentType.startsWith("audio/") ||
                        contentType.equals("application/pdf")
        );
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files) {

        List<Map<String, Object>> uploadResults = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (MultipartFile file : files) {
            Map<String, Object> fileResult = new HashMap<>();

            if (file.isEmpty()) {
                fileResult.put("success", false);
                fileResult.put("filename", file.getOriginalFilename());
                fileResult.put("message", "File is empty");
                failureCount++;
            } else if (!isValidFileType(file.getContentType())) {
                fileResult.put("success", false);
                fileResult.put("filename", file.getOriginalFilename());
                fileResult.put("message", "Invalid file type");
                failureCount++;
            } else {
                try {
                    String originalFilename = file.getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                    MediaFile mediaFile = new MediaFile(
                            (long) (mediaFiles.size() + 1),
                            uniqueFilename,
                            originalFilename,
                            file.getContentType(),
                            file.getSize()
                    );
                    mediaFile.setFilePath(UPLOAD_DIR + uniqueFilename);
                    mediaFile.setUploadedBy(1L);

                    mediaFiles.add(mediaFile);

                    fileResult.put("success", true);
                    fileResult.put("fileId", mediaFile.getId());
                    fileResult.put("filename", uniqueFilename);
                    fileResult.put("originalFilename", originalFilename);
                    fileResult.put("url", "/media/" + uniqueFilename);
                    successCount++;

                } catch (Exception e) {
                    fileResult.put("success", false);
                    fileResult.put("filename", file.getOriginalFilename());
                    fileResult.put("message", "Upload failed: " + e.getMessage());
                    failureCount++;
                }
            }

            uploadResults.add(fileResult);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalFiles", files.length);
        response.put("successCount", successCount);
        response.put("failureCount", failureCount);
        response.put("results", uploadResults);

        return ResponseEntity.ok(response);
    }
}