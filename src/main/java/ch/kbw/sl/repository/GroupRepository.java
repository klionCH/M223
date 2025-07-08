package ch.kbw.sl.repository;

import ch.kbw.sl.entity.Group;
import ch.kbw.sl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Page<Group> findByIsPrivateFalseAndActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    List<Group> findByCreatorOrderByCreatedAtDesc(User creator);

    @Query("SELECT g FROM Group g JOIN g.members gm WHERE gm.user = :user AND g.active = true ORDER BY g.createdAt DESC")
    List<Group> findGroupsByMember(@Param("user") User user);

    @Query("SELECT g FROM Group g WHERE g.name LIKE %:query% AND g.isPrivate = false AND g.active = true")
    List<Group> searchPublicGroups(@Param("query") String query);

    boolean existsByNameAndCreator(String name, User creator);
}