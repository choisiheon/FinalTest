package net.datasa.finaltest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "community_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @Column(name = "member_id", length = 20)
    private String memberId;

    @Column(name = "member_pw", nullable = false, length = 100)
    private String memberPw;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "email", length = 200)
    private String email;
}