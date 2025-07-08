package ch.kbw.sl.service;

import ch.kbw.sl.entity.MediaFile;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaFileService {

    private final MediaFileRepository mediaFileRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public MediaFile uploadFile(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file to filesystem
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Save file metadata to database
        MediaFile mediaFile = MediaFile.builder()
                .filename(uniqueFilename)
                .originalFilename(originalFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(filePath.toString())
                .uploadedBy(user)
                .build();

        return mediaFileRepository.save(mediaFile);
    }

    @Transactional(readOnly = true)
    public Optional<MediaFile> findById(Long id) {
        return mediaFileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<MediaFile> findByFilename(String filename) {
        return mediaFileRepository.findByFilename(filename);
    }

    @Transactional(readOnly = true)
    public Page<MediaFile> findByUser(User user, Pageable pageable) {
        return mediaFileRepository.findByUploadedByOrderByUploadedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MediaFile> findByUserAndContentType(User user, String contentType, Pageable pageable) {
        return mediaFileRepository.findByUploadedByAndContentTypeStartingWith(user, contentType, pageable);
    }

    public void deleteFile(Long id) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Delete from filesystem
        Path filePath = Paths.get(mediaFile.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete from database
        mediaFileRepository.delete(mediaFile);
    }
}