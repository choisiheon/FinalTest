package net.datasa.finaltest.controller;

import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.dto.BoardDTO;
import net.datasa.finaltest.dto.GroupDTO;
import net.datasa.finaltest.service.BoardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 페이지 이동
    @GetMapping("/list")
    public String list() {
        return "board/list";
    }

    // Ajax 목록 조회
    @ResponseBody
    @PostMapping("/ajaxList")
    public List<BoardDTO> ajaxList(@RequestParam(defaultValue = "ALL") String category) {
        return boardService.getList(category);
    }

    // 글쓰기 폼
    @GetMapping("/writeForm")
    public String writeForm() {
        return "board/writeForm";
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String write(BoardDTO dto, @AuthenticationPrincipal UserDetails user) {
        boardService.write(dto, user.getUsername());
        return "redirect:/board/list";
    }

    // 상세 보기
    @GetMapping("/detail")
    public String detail(@RequestParam Integer boardNum, Model model) {
        BoardDTO dto = boardService.getBoard(boardNum);
        model.addAttribute("board", dto);
        return "board/read";
    }

    // 글 삭제
    @GetMapping("/delete")
    public String delete(@RequestParam Integer boardNum, @AuthenticationPrincipal UserDetails user) {
        boardService.delete(boardNum, user.getUsername());
        return "redirect:/board/list";
    }

    // 모임 신청
    @GetMapping("/apply")
    public String apply(@RequestParam Integer boardNum, @AuthenticationPrincipal UserDetails user) {
        try {
            boardService.apply(boardNum, user.getUsername());
        } catch (Exception e) {
            // 이미 신청한 경우 등 에러 처리
            return "redirect:/board/detail?boardNum=" + boardNum + "&error=duplicate";
        }
        return "redirect:/board/detail?boardNum=" + boardNum;
    }

    // 신청 현황 (작성자 전용)
    @GetMapping("/applicants")
    public String applicants(@RequestParam Integer boardNum, Model model) {
        List<GroupDTO> list = boardService.getApplicants(boardNum);
        BoardDTO board = boardService.getBoard(boardNum);
        model.addAttribute("list", list);
        model.addAttribute("board", board);
        return "board/applicants";
    }

    // 승인/거절 처리
    @GetMapping("/process")
    public String process(@RequestParam Integer groupId, @RequestParam String action, @RequestParam Integer boardNum) {
        boardService.process(groupId, action);
        return "redirect:/board/applicants?boardNum=" + boardNum;
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal UserDetails user, Model model) {
        Map<String, List<GroupDTO>> map = boardService.getMyPage(user.getUsername());
        model.addAttribute("leaderList", map.get("leader"));
        model.addAttribute("memberList", map.get("member"));
        model.addAttribute("id", user.getUsername());
        return "mypage/myList";
    }
}