package net.datasa.finaltest.controller;

import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.entity.BoardEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    // ... repository 주입

    // 일반 페이지 접속
    @GetMapping("/list")
    public String list() {
        return "board/list";
    }

    // Ajax 요청 처리 (JSON 리턴)
    @ResponseBody
    @PostMapping("/ajaxList")
    public List<BoardDTO> getList(@RequestParam(required = false) String category) {
        List<BoardEntity> entities;
        if (category == null || category.isEmpty() || "ALL".equals(category)) {
            entities = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
        } else {
            entities = boardRepository.findByCategoryOrderByCreateDateDesc(category);
        }

        // Entity -> DTO 변환하여 리턴
        return entities.stream().map(e -> new BoardDTO(e)).collect(Collectors.toList());
    }
}