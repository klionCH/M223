package ch.kbw.sl.service;

import ch.kbw.sl.entity.Group;
import ch.kbw.sl.entity.GroupMember;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.GroupRepository;
import ch.kbw.sl.repository.GroupMemberRepository;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public Group createGroup(Group group, User creator) {
        if (groupRepository.existsByNameAndCreator(group.getName(), creator)) {
            throw new IllegalArgumentException("Group with this name already exists");
        }

        group.setCreator(creator);
        Group savedGroup = groupRepository.save(group);

        // Add creator as admin
        GroupMember creatorMember = GroupMember.builder()
                .group(savedGroup)
                .user(creator)
                .role(GroupMember.Role.ADMIN)
                .build();
        groupMemberRepository.save(creatorMember);

        return savedGroup;
    }

    @Transactional(readOnly = true)
    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Group> findPublicGroups(Pageable pageable) {
        return groupRepository.findByIsPrivateFalseAndActiveTrueOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsByMember(User user) {
        return groupRepository.findGroupsByMember(user);
    }

    @Transactional(readOnly = true)
    public List<Group> searchPublicGroups(String query) {
        return groupRepository.searchPublicGroups(query);
    }

    public Group updateGroup(Long id, Group updatedGroup) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setIsPrivate(updatedGroup.getIsPrivate());

        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        group.setActive(false);
        groupRepository.save(group);
    }

    public GroupMember joinGroup(Group group, User user) {
        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("Already a member of this group");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupMember.Role.MEMBER)
                .build();

        return groupMemberRepository.save(member);
    }

    public void leaveGroup(Group group, User user) {
        groupMemberRepository.deleteByGroupAndUser(group, user);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getGroupMembers(Group group) {
        return groupMemberRepository.findByGroupOrderByJoinedAtAsc(group);
    }

    @Transactional(readOnly = true)
    public boolean isMember(Group group, User user) {
        return groupMemberRepository.existsByGroupAndUser(group, user);
    }

    public GroupMember updateMemberRole(Group group, User user, GroupMember.Role role) {
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setRole(role);
        return groupMemberRepository.save(member);
    }
}