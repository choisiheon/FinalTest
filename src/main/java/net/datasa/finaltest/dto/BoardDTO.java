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
public class BoardDTO {
    private Integer boardNum;
    private String memberId;    // 작성자 ID
    private String category;    // STUDY, HEALTHY, TRIP
    private String title;
    private String contents;
    private Integer headcnt;
    private Integer capacity;
    private String status;      // OPEN, CLOSED
    private LocalDateTime createDate;
}