package net.datasa.finaltest.controller;

import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.dto.MemberDTO;
import net.datasa.finaltest.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 폼 이동
    @GetMapping("/joinForm")
    public String joinForm() {
        return "member/joinForm";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String join(MemberDTO dto) {
        memberService.join(dto);
        return "redirect:/";
    }

    // 로그인 폼 이동
    @GetMapping("/loginForm")
    public String loginForm() {
        return "member/loginForm";
    }
}