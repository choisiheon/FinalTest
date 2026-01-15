package net.datasa.finaltest.service;

import lombok.RequiredArgsConstructor;
import net.datasa.finaltest.dto.MemberDTO;
import net.datasa.finaltest.entity.MemberEntity;
import net.datasa.finaltest.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입 처리
    public void join(MemberDTO dto) {
        MemberEntity entity = MemberEntity.builder()
                .memberId(dto.getMemberId())
                .memberPw(passwordEncoder.encode(dto.getMemberPw()))
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
        memberRepository.save(entity);
    }
}