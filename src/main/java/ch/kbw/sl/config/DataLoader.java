package ch.kbw.sl.config;

import ch.kbw.sl.entity.*;
import ch.kbw.sl.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final InteractionRepository interactionRepository;
    private final FollowRepository followRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationRepository notificationRepository;
    private final MediaFileRepository mediaFileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Loading test data...");
            loadTestData();
            log.info("Test data loaded successfully!");
        } else {
            log.info("Test data already exists, skipping data loading.");
        }
    }

    private void loadTestData() {
        User[] users = createUsers();

        MediaFile[] mediaFiles = createMediaFiles(users);

        Post[] posts = createPosts(users, mediaFiles);

        createInteractions(users, posts);

        createFollowRelationships(users);

        Group[] groups = createGroups(users);

        createGroupMembers(groups, users);

        createNotifications(users, posts);
    }

    private User[] createUsers() {
        User user1 = User.builder()
                .username("johndoe")
                .email("john.doe@creativehub.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .bio("Professional photographer and visual storyteller.")
                .profileImage("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face")
                .active(true)
                .joinDate(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .username("janesmith")
                .email("jane.smith@creativehub.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .bio("UI/UX Designer passionate about digital experiences.")
                .profileImage("https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face")
                .active(true)
                .joinDate(LocalDateTime.now())
                .build();

        User[] users = {user1, user2};
        userRepository.saveAll(Arrays.asList(users));
        log.info("Created {} users", users.length);

        return users;
    }

    private MediaFile[] createMediaFiles(User[] users) {
        MediaFile media1 = MediaFile.builder()
                .filename("sunset_beach.jpg")
                .originalFilename("sunset_beach.jpg")
                .contentType("image/jpeg")
                .fileSize(2457600L)
                .filePath("uploads/sunset_beach.jpg")
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(users[0])
                .build();

        MediaFile media2 = MediaFile.builder()
                .filename("ui_design.mp4")
                .originalFilename("ui_design.mp4")
                .contentType("video/mp4")
                .fileSize(15728640L)
                .filePath("uploads/ui_design.mp4")
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(users[1])
                .build();

        MediaFile[] mediaFiles = {media1, media2};
        mediaFileRepository.saveAll(Arrays.asList(mediaFiles));
        log.info("Created {} media files", mediaFiles.length);

        return mediaFiles;
    }

    private Post[] createPosts(User[] users, MediaFile[] mediaFiles) {
        Post post1 = Post.builder()
                .title("Golden Hour Magic at Santa Monica Beach")
                .content("Captured this breathtaking sunset yesterday evening.")
                .contentType(Post.ContentType.IMAGE)
                .mediaUrl("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600")
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .user(users[0])
                .build();

        Post post2 = Post.builder()
                .title("5 Essential UI Design Principles")
                .content("After 5 years in UI/UX design, here are fundamental principles...")
                .contentType(Post.ContentType.TEXT)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .user(users[1])
                .build();

        Post[] posts = {post1, post2};
        postRepository.saveAll(Arrays.asList(posts));
        log.info("Created {} posts", posts.length);

        return posts;
    }

    private void createInteractions(User[] users, Post[] posts) {
        Interaction like1 = Interaction.builder()
                .user(users[1])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.LIKE)
                .createdAt(LocalDateTime.now())
                .build();

        Interaction comment1 = Interaction.builder()
                .user(users[1])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.COMMENT)
                .commentText("Top Post!")
                .createdAt(LocalDateTime.now())
                .build();

        Interaction share1 = Interaction.builder()
                .user(users[0])
                .post(posts[1])
                .interactionType(Interaction.InteractionType.SHARE)
                .createdAt(LocalDateTime.now())
                .build();

        Interaction[] interactions = {like1, comment1, share1};
        interactionRepository.saveAll(Arrays.asList(interactions));
        log.info("Created {} interactions", interactions.length);
    }

    private void createFollowRelationships(User[] users) {
        Follow follow1 = Follow.builder()
                .follower(users[1])
                .following(users[0])
                .createdAt(LocalDateTime.now())
                .build();

        Follow[] follows = {follow1};
        followRepository.saveAll(Arrays.asList(follows));
        log.info("Created {} follow relationships", follows.length);
    }

    private Group[] createGroups(User[] users) {
        Group group1 = Group.builder()
                .name("Photography Enthusiasts")
                .description("A community for photography lovers.")
                .creator(users[0])
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Group group2 = Group.builder()
                .name("UI/UX Design Collective")
                .description("Designers helping designers!")
                .creator(users[1])
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Group[] groups = {group1, group2};
        groupRepository.saveAll(Arrays.asList(groups));
        log.info("Created {} groups", groups.length);

        return groups;
    }

    private void createGroupMembers(Group[] groups, User[] users) {
        GroupMember member1 = GroupMember.builder()
                .group(groups[0])
                .user(users[0])
                .role(GroupMember.Role.ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();

        GroupMember member2 = GroupMember.builder()
                .group(groups[0])
                .user(users[1])
                .role(GroupMember.Role.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        GroupMember[] members = {member1, member2};
        groupMemberRepository.saveAll(Arrays.asList(members));
        log.info("Created {} group members", members.length);
    }

    private void createNotifications(User[] users, Post[] posts) {
        Notification notif1 = Notification.builder()
                .user(users[0])
                .type(Notification.NotificationType.LIKE)
                .message("Jane hat deinen Beitrag geliket")
                .relatedPost(posts[0])
                .relatedUser(users[1])
                .createdAt(LocalDateTime.now())
                .build();

        Notification notif2 = Notification.builder()
                .user(users[0])
                .type(Notification.NotificationType.COMMENT)
                .message("Jane hat kommentiert")
                .relatedPost(posts[0])
                .relatedUser(users[1])
                .createdAt(LocalDateTime.now())
                .build();

        Notification[] notifications = {notif1, notif2};
        notificationRepository.saveAll(Arrays.asList(notifications));
        log.info("Created {} notifications", notifications.length);
    }
}
