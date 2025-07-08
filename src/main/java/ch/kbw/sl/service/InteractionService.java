package ch.kbw.sl.service;

import ch.kbw.sl.entity.Interaction;
import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;

    public Interaction createInteraction(Interaction interaction) {
        // Check if like already exists
        if (interaction.getInteractionType() == Interaction.InteractionType.LIKE) {
            if (interactionRepository.existsByUserAndPostAndInteractionType(
                    interaction.getUser(), interaction.getPost(), Interaction.InteractionType.LIKE)) {
                throw new IllegalArgumentException("Already liked this post");
            }
        }
        return interactionRepository.save(interaction);
    }

    @Transactional(readOnly = true)
    public Page<Interaction> findByPost(Post post, Pageable pageable) {
        return interactionRepository.findByPostOrderByCreatedAtDesc(post, pageable);
    }

    @Transactional(readOnly = true)
    public List<Interaction> findLikesByPost(Post post) {
        return interactionRepository.findByPostAndInteractionType(post, Interaction.InteractionType.LIKE);
    }

    @Transactional(readOnly = true)
    public List<Interaction> findCommentsByPost(Post post) {
        return interactionRepository.findByPostAndInteractionType(post, Interaction.InteractionType.COMMENT);
    }

    @Transactional(readOnly = true)
    public long countLikesByPost(Post post) {
        return interactionRepository.countByPostAndInteractionType(post, Interaction.InteractionType.LIKE);
    }

    @Transactional(readOnly = true)
    public long countCommentsByPost(Post post) {
        return interactionRepository.countByPostAndInteractionType(post, Interaction.InteractionType.COMMENT);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(User user, Post post) {
        return interactionRepository.existsByUserAndPostAndInteractionType(user, post, Interaction.InteractionType.LIKE);
    }

    public void unlikePost(User user, Post post) {
        Optional<Interaction> like = interactionRepository.findByUserAndPostAndInteractionType(
                user, post, Interaction.InteractionType.LIKE);
        like.ifPresent(interactionRepository::delete);
    }

    public void deleteInteraction(Long id) {
        interactionRepository.deleteById(id);
    }
}