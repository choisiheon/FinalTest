package net.datasa.finaltest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Integer groupId;
    private Integer boardNum;
    private String memberId;    // 신청자 ID
    private String role;        // LEADER, MEMBER
    private String status;      // PENDING, JOINED, REJECTED
    private LocalDateTime joinedDate;

    // 화면 출력을 위한 추가 필드
    private String boardTitle;      // 모임 제목
    private String boardStatus;     // 모임 상태
    private Integer boardHeadcnt;   // 현재 인원
    private Integer boardCapacity;  // 정원
    private String boardWriter;     // 주최자
}