package ch.kbw.sl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String token;

    private long expiresIn;

    private String username;

    private String firstName;

    private String lastName;

    private String bio;

    private String profileImage;

    private LocalDateTime joinDate;

    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interaction> interactions;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> following;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> followers;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Group> createdGroups;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMember> groupMemberships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaFile> uploadedFiles;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ======================
    // UserDetails Interface
    // ======================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // falls du später Rollen einbaust, hier ersetzen
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // oder eigene Logik
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // oder eigene Logik
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // oder eigene Logik
    }

    @Override
    public boolean isEnabled() {
        return this.active; // aktiviere/deaktiviere Account über dein active-Feld
    }
}
