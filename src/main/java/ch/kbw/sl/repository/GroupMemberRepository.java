package ch.kbw.sl.repository;


import ch.kbw.sl.entity.Group;
import ch.kbw.sl.entity.GroupMember;
import ch.kbw.sl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    boolean existsByGroupAndUser(Group group, User user);

    List<GroupMember> findByGroupOrderByJoinedAtAsc(Group group);

    List<GroupMember> findByUserOrderByJoinedAtDesc(User user);

    long countByGroup(Group group);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.role = :role")
    List<GroupMember> findByGroupAndRole(@Param("group") Group group, @Param("role") GroupMember.Role role);

    void deleteByGroupAndUser(Group group, User user);
}
