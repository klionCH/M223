// DataLoader.java
package ch.kbw.sl.config;

import ch.kbw.sl.entity.*;
import ch.kbw.sl.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
                .bio("Professional photographer and visual storyteller. Capturing moments that matter. Available for portraits, events, and commercial work.")
                .profileImage("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face")
                .build();

        User user2 = User.builder()
                .username("janesmith")
                .email("jane.smith@creativehub.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .bio("UI/UX Designer passionate about creating beautiful and functional digital experiences. Coffee lover ☕ and design enthusiast.")
                .profileImage("https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face")
                .build();



        User[] users = {user1, user2};
        userRepository.saveAll(java.util.Arrays.asList(users));
        log.info("Created {} users", users.length);

        return users;
    }

    private MediaFile[] createMediaFiles(User[] users) {

        MediaFile media1 = MediaFile.builder()
                .filename("sunset_beach_2024_abc123.jpg")
                .originalFilename("sunset_beach.jpg")
                .contentType("image/jpeg")
                .fileSize(2457600L) // 2.4 MB
                .filePath("uploads/sunset_beach_2024_abc123.jpg")
                .uploadedBy(users[0]) // John
                .build();

        MediaFile media2 = MediaFile.builder()
                .filename("ui_design_process_def456.mp4")
                .originalFilename("ui_design_process.mp4")
                .contentType("video/mp4")
                .fileSize(15728640L) // 15 MB
                .filePath("uploads/ui_design_process_def456.mp4")
                .uploadedBy(users[1]) // Jane
                .build();


        MediaFile[] mediaFiles = {media1, media2};
        mediaFileRepository.saveAll(java.util.Arrays.asList(mediaFiles));
        log.info("Created {} media files", mediaFiles.length);

        return mediaFiles;
    }

    private Post[] createPosts(User[] users, MediaFile[] mediaFiles) {

        Post post1 = Post.builder()
                .title("Golden Hour Magic at Santa Monica Beach")
                .content("Captured this breathtaking sunset yesterday evening. There's something magical about the golden hour that makes everything look like a dream. The waves were perfect, the sky was painted in warm hues, and the whole scene felt like a natural masterpiece. 🌅\n\nCamera: Canon EOS R5\nLens: 24-70mm f/2.8\nSettings: f/8, 1/125s, ISO 100")
                .contentType(Post.ContentType.IMAGE)
                .mediaUrl("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600")
                .user(users[0]) // John
                .build();

        Post post2 = Post.builder()
                .title("5 Essential UI Design Principles Every Designer Should Know")
                .content("After 5 years in UI/UX design, here are the fundamental principles that have consistently led to successful projects:\n\n1. 🎯 Clarity over cleverness\n2. 📱 Mobile-first approach\n3. 🔄 Consistency in patterns\n4. ♿ Accessibility from day one\n5. 📊 Data-driven decisions\n\nWhich principle do you find most challenging to implement? Let me know in the comments! 👇")
                .contentType(Post.ContentType.TEXT)
                .user(users[1]) // Jane
                .build();



        Post[] posts = {post1, post2};
        postRepository.saveAll(java.util.Arrays.asList(posts));
        log.info("Created {} posts", posts.length);

        return posts;
    }

    private void createInteractions(User[] users, Post[] posts) {

        // Likes
        Interaction like1 = Interaction.builder()
                .user(users[1])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.LIKE)
                .build();

        Interaction like2 = Interaction.builder()
                .user(users[2])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.LIKE)
                .build();


        // Comments
        Interaction comment1 = Interaction.builder()
                .user(users[1])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.COMMENT)
                .commentText("Top Post!")
                .build();

        Interaction comment2 = Interaction.builder()
                .user(users[2])
                .post(posts[0])
                .interactionType(Interaction.InteractionType.COMMENT)
                .commentText("Geht so")
                .build();


        // Shares
        Interaction share1 = Interaction.builder()
                .user(users[2])
                .post(posts[1])
                .interactionType(Interaction.InteractionType.SHARE)
                .build();

        Interaction share2 = Interaction.builder()
                .user(users[3])
                .post(posts[5])
                .interactionType(Interaction.InteractionType.SHARE)
                .build();

        Interaction[] interactions = {like1, like2, comment1, comment2, share1, share2};
        interactionRepository.saveAll(java.util.Arrays.asList(interactions));
        log.info("Created {} interactions", interactions.length);
    }

    private void createFollowRelationships(User[] users) {
        log.info("Creating follow relationships...");

        Follow follow1 = Follow.builder()
                .follower(users[1])
                .following(users[0])
                .build();

        Follow follow2 = Follow.builder()
                .follower(users[2])
                .following(users[0])
                .build();


        Follow[] follows = {follow1, follow2};
        followRepository.saveAll(java.util.Arrays.asList(follows));
        log.info("Created {} follow relationships", follows.length);
    }

    private Group[] createGroups(User[] users) {
        log.info("Creating groups...");

        Group group1 = Group.builder()
                .name("Photography Enthusiasts")
                .description("A community for photography lovers to share techniques, equipment reviews, and inspire each other with stunning visuals. Whether you're a beginner or professional, all skill levels welcome!")
                .creator(users[0])
                .build();

        Group group2 = Group.builder()
                .name("UI/UX Design Collective")
                .description("Designers helping designers! Share your work, get feedback, discuss industry trends, and collaborate on projects. Let's create better user experiences together!")
                .creator(users[1])
                .build();


        Group[] groups = {group1, group2};
        groupRepository.saveAll(java.util.Arrays.asList(groups));
        log.info("Created {} groups", groups.length);

        return groups;
    }

    private void createGroupMembers(Group[] groups, User[] users) {

        // Photography Enthusiasts Group
        GroupMember member1 = GroupMember.builder()
                .group(groups[0])
                .user(users[0])
                .role(GroupMember.Role.ADMIN)
                .build();

        GroupMember member2 = GroupMember.builder()
                .group(groups[0])
                .user(users[4])
                .role(GroupMember.Role.MEMBER)
                .build();


        GroupMember[] members = {member1, member2};
        groupMemberRepository.saveAll(java.util.Arrays.asList(members));
        log.info("Created {} group members", members.length);
    }

    private void createNotifications(User[] users, Post[] posts) {

        Notification notif1 = Notification.builder()
                .user(users[0])
                .type(Notification.NotificationType.LIKE)
                .message("Jean hat deinen Beitrag geliket")
                .relatedPost(posts[0])
                .relatedUser(users[1])
                .build();

        Notification notif2 = Notification.builder()
                .user(users[0])
                .type(Notification.NotificationType.COMMENT)
                .message("Mike hat kommentiert")
                .relatedPost(posts[0])
                .relatedUser(users[2])
                .build();


        Notification[] notifications = {notif1, notif2};
        notificationRepository.saveAll(java.util.Arrays.asList(notifications));
        log.info("Created {} notifications", notifications.length);
    }
}