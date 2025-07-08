package ch.kbw.sl.service;

import ch.kbw.sl.entity.Follow;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.FollowRepository;
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
public class FollowService {

    private final FollowRepository followRepository;

    public Follow followUser(User follower, User following) {
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        return followRepository.save(follow);
    }

    public void unfollowUser(User follower, User following) {
        Optional<Follow> follow = followRepository.findByFollowerAndFollowing(follower, following);
        follow.ifPresent(followRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<Follow> getFollowers(User user, Pageable pageable) {
        return followRepository.findByFollowingOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Follow> getFollowing(User user, Pageable pageable) {
        return followRepository.findByFollowerOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(User user) {
        return followRepository.countByFollowing(user);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(User user) {
        return followRepository.countByFollower(user);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowingUserIds(User user) {
        return followRepository.findFollowingUserIds(user);
    }
}