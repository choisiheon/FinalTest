package net.datasa.finaltest.repository;

import net.datasa.finaltest.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {
    // 특정 게시글의 신청자 목록 조회
    List<GroupEntity> findByBoard_BoardNum(Integer boardNum);

    // 특정 회원의 참여 목록 조회 (마이페이지용)
    List<GroupEntity> findByMember_MemberId(String memberId);

    // 중복 신청 확인용
    boolean existsByBoard_BoardNumAndMember_MemberId(Integer boardNum, String memberId);
}