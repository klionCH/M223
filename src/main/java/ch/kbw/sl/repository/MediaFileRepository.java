package ch.kbw.sl.repository;

import ch.kbw.sl.entity.MediaFile;
import ch.kbw.sl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    Optional<MediaFile> findByFilename(String filename);

    Page<MediaFile> findByUploadedByOrderByUploadedAtDesc(User uploadedBy, Pageable pageable);

    @Query("SELECT mf FROM MediaFile mf WHERE mf.uploadedBy = :user AND mf.contentType LIKE :contentType% ORDER BY mf.uploadedAt DESC")
    Page<MediaFile> findByUploadedByAndContentTypeStartingWith(@Param("user") User user, @Param("contentType") String contentType, Pageable pageable);

    long countByUploadedBy(User uploadedBy);

    @Query("SELECT SUM(mf.fileSize) FROM MediaFile mf WHERE mf.uploadedBy = :user")
    Long getTotalFileSizeByUser(@Param("user") User user);
}