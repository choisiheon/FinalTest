package net.datasa.finaltest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_num")
    private Integer boardNum;

    // 작성자 (MemberEntity와 관계 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String contents;

    @Column(columnDefinition = "int default 0")
    private Integer headcnt;

    @Column(columnDefinition = "int default 5")
    private Integer capacity;

    @Column(columnDefinition = "varchar(100) default 'OPEN'")
    private String status;

    @Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createDate;
}