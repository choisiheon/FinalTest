package net.datasa.finaltest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_group")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_num")
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(columnDefinition = "varchar(20) default 'MEMBER'")
    private String role; // LEADER, MEMBER

    @Column(columnDefinition = "varchar(20) default 'PENDING'")
    private String status; // PENDING, JOINED, REJECTED

    @Column(name = "joined_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime joinedDate;
}