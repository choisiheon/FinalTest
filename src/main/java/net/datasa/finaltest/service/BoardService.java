package net.datasa.finaltest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.entity.BoardEntity;
import net.datasa.finaltest.entity.GroupEntity;
import net.datasa.finaltest.entity.MemberEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final GroupRepository groupRepository;

    /**
     * 모임 신청 기능
     * - 중복 신청 체크
     * - PENDING 상태로 저장
     */
    public void applyGroup(Integer boardNum, String memberId) {
        BoardEntity board = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        // 이미 신청했는지 확인
        boolean exists = groupRepository.existsByBoard_BoardNumAndMember_MemberId(boardNum, memberId);
        if (exists) {
            throw new IllegalStateException("이미 신청한 모임입니다.");
        }

        MemberEntity member = MemberEntity.builder().memberId(memberId).build();

        GroupEntity group = GroupEntity.builder()
                .board(board)
                .member(member)
                .role("MEMBER")
                .status("PENDING")
                .joinedDate(LocalDateTime.now())
                .build();

        groupRepository.save(group);
    }

    /**
     * 승인/거절 처리
     * - 승인 시: 상태 JOINED 변경, 게시글 headcnt + 1
     * - 거절 시: 상태 REJECTED 변경 (인원수 변동 없음)
     * - 정원 초과 시: 게시글 상태 CLOSED 로 변경
     */
    public void processApplication(Integer groupId, String action) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("신청 정보 없음"));

        BoardEntity board = group.getBoard();

        if ("APPROVE".equals(action)) {
            // 정원 초과 체크
            if (board.getHeadcnt() >= board.getCapacity()) {
                throw new IllegalStateException("정원이 초과되었습니다.");
            }

            group.setStatus("JOINED");
            board.setHeadcnt(board.getHeadcnt() + 1); // 인원 증가

            // 인원이 다 찼으면 CLOSED 처리
            if (board.getHeadcnt() >= board.getCapacity()) {
                board.setStatus("CLOSED");
            }
        } else if ("REJECT".equals(action)) {
            // 이미 승인된 상태에서 거절로 바꿀 경우 인원 감소 로직 필요 (요구사항 12p 하단)
            if ("JOINED".equals(group.getStatus())) {
                board.setHeadcnt(board.getHeadcnt() - 1);
                // 인원이 줄었으므로 다시 OPEN 될 수도 있음 (선택사항)
                if (board.getHeadcnt() < board.getCapacity()) {
                    board.setStatus("OPEN");
                }
            }
            group.setStatus("REJECTED");
        }
    }
}
