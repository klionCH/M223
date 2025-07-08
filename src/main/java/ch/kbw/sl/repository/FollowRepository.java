package ch.kbw.sl.repository;


import ch.kbw.sl.entity.Follow;
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
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    Page<Follow> findByFollowingOrderByCreatedAtDesc(User following, Pageable pageable);

    Page<Follow> findByFollowerOrderByCreatedAtDesc(User follower, Pageable pageable);

    long countByFollowing(User following);

    long countByFollower(User follower);

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower = :user")
    List<Long> findFollowingUserIds(@Param("user") User user);
}