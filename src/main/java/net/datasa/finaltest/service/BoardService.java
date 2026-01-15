package net.datasa.finaltest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.dto.BoardDTO;
import net.datasa.finaltest.dto.GroupDTO;
import net.datasa.finaltest.entity.BoardEntity;
import net.datasa.finaltest.entity.GroupEntity;
import net.datasa.finaltest.entity.MemberEntity;
import net.datasa.finaltest.repository.BoardRepository;
import net.datasa.finaltest.repository.GroupRepository;
import net.datasa.finaltest.repository.MemberRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ... imports ...

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    // 글 작성
    public void write(BoardDTO dto, String memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        BoardEntity board = BoardEntity.builder()
                .member(member)
                .category(dto.getCategory())
                .title(dto.getTitle())
                .contents(dto.getContents())
                .headcnt(0)  // 초기 0명 (리더 제외 카운트인 경우)
                .capacity(dto.getCapacity())
                .status("OPEN")
                .createDate(LocalDateTime.now())
                .build();

        BoardEntity savedBoard = boardRepository.save(board);

        // 작성자를 LEADER로 Group 테이블에 등록 (요구사항 12p: 작성자는 LEADER)
        GroupEntity leaderGroup = GroupEntity.builder()
                .board(savedBoard)
                .member(member)
                .role("LEADER")
                .status("JOINED")
                .joinedDate(LocalDateTime.now())
                .build();
        groupRepository.save(leaderGroup);
    }

    // 목록 조회 (Ajax용)
    public List<BoardDTO> getList(String category) {
        List<BoardEntity> entityList;
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");

        if (category == null || category.equals("ALL") || category.isEmpty()) {
            entityList = boardRepository.findAll(sort);
        } else {
            entityList = boardRepository.findByCategoryOrderByCreateDateDesc(category);
        }

        List<BoardDTO> dtoList = new ArrayList<>();
        for (BoardEntity entity : entityList) {
            dtoList.add(BoardDTO.builder()
                    .boardNum(entity.getBoardNum())
                    .memberId(entity.getMember() != null ? entity.getMember().getMemberId() : null)
                    .category(entity.getCategory())
                    .title(entity.getTitle())
                    .headcnt(entity.getHeadcnt())
                    .capacity(entity.getCapacity())
                    .status(entity.getStatus())
                    .createDate(entity.getCreateDate())
                    .build());
        }
        return dtoList;
    }

    // 게시글 상세 조회
    public BoardDTO getBoard(Integer boardNum) {
        BoardEntity entity = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        return BoardDTO.builder()
                .boardNum(entity.getBoardNum())
                .memberId(entity.getMember().getMemberId())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .headcnt(entity.getHeadcnt())
                .capacity(entity.getCapacity())
                .status(entity.getStatus())
                .createDate(entity.getCreateDate())
                .build();
    }

    // 게시글 삭제
    public void delete(Integer boardNum, String memberId) {
        BoardEntity entity = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        if (!entity.getMember().getMemberId().equals(memberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }
        boardRepository.delete(entity);
    }

    // 모임 신청
    public void apply(Integer boardNum, String memberId) {
        BoardEntity board = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        if (groupRepository.existsByBoard_BoardNumAndMember_MemberId(boardNum, memberId)) {
            throw new RuntimeException("이미 신청했거나 가입된 모임입니다.");
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        GroupEntity group = GroupEntity.builder()
                .board(board)
                .member(member)
                .role("MEMBER")
                .status("PENDING")
                .joinedDate(LocalDateTime.now())
                .build();
        groupRepository.save(group);
    }

    // 신청자 목록 조회 (확인하기)
    public List<GroupDTO> getApplicants(Integer boardNum) {
        List<GroupEntity> entities = groupRepository.findByBoard_BoardNum(boardNum);
        List<GroupDTO> dtoList = new ArrayList<>();

        for (GroupEntity entity : entities) {
            dtoList.add(GroupDTO.builder()
                    .groupId(entity.getGroupId())
                    .boardNum(entity.getBoard().getBoardNum())
                    .memberId(entity.getMember().getMemberId())
                    .role(entity.getRole())
                    .status(entity.getStatus())
                    .joinedDate(entity.getJoinedDate())
                    .build());
        }
        return dtoList;
    }

    // 승인/거절 처리
    public void process(Integer groupId, String action) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("신청 정보 없음"));
        BoardEntity board = group.getBoard();

        if (action.equals("APPROVE")) {
            if (board.getHeadcnt() >= board.getCapacity()) {
                throw new RuntimeException("정원 초과");
            }
            group.setStatus("JOINED");
            board.setHeadcnt(board.getHeadcnt() + 1);

            if (board.getHeadcnt() >= board.getCapacity()) {
                board.setStatus("CLOSED");
            }
        } else if (action.equals("REJECT")) {
            if (group.getStatus().equals("JOINED")) {
                board.setHeadcnt(board.getHeadcnt() - 1);
                if (board.getHeadcnt() < board.getCapacity()) {
                    board.setStatus("OPEN");
                }
            }
            group.setStatus("REJECTED");
        }
    }

    // 마이페이지 (내 모임 + 참여 모임)
    public Map<String, List<GroupDTO>> getMyPage(String memberId) {
        List<GroupEntity> myGroups = groupRepository.findByMember_MemberId(memberId);
        List<GroupDTO> leaderList = new ArrayList<>(); // 내가 주최
        List<GroupDTO> memberList = new ArrayList<>(); // 내가 참여

        for (GroupEntity entity : myGroups) {
            GroupDTO dto = GroupDTO.builder()
                    .boardNum(entity.getBoard().getBoardNum())
                    .boardTitle(entity.getBoard().getTitle())
                    .boardWriter(entity.getBoard().getMember().getMemberId())
                    .boardStatus(entity.getBoard().getStatus())
                    .boardHeadcnt(entity.getBoard().getHeadcnt())
                    .boardCapacity(entity.getBoard().getCapacity())
                    .role(entity.getRole())
                    .status(entity.getStatus())
                    .build();

            if (entity.getRole().equals("LEADER")) {
                leaderList.add(dto);
            } else if (entity.getStatus().equals("JOINED")) { // 참여 승인된 것만
                memberList.add(dto);
            }
        }

        Map<String, List<GroupDTO>> result = new HashMap<>();
        result.put("leader", leaderList);
        result.put("member", memberList);
        return result;
    }
}