package ch.kbw.sl.repository;


import ch.kbw.sl.entity.Interaction;
import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    Page<Interaction> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);

    List<Interaction> findByPostAndInteractionType(Post post, Interaction.InteractionType type);

    Optional<Interaction> findByUserAndPostAndInteractionType(User user, Post post, Interaction.InteractionType type);

    boolean existsByUserAndPostAndInteractionType(User user, Post post, Interaction.InteractionType type);

    @Query("SELECT COUNT(i) FROM Interaction i WHERE i.post = :post AND i.interactionType = :type")
    long countByPostAndInteractionType(@Param("post") Post post, @Param("type") Interaction.InteractionType type);

    List<Interaction> findByUserOrderByCreatedAtDesc(User user);
}