package com.nhnacademy.miniDooray.service.impl;

import com.nhnacademy.miniDooray.dto.MemberDto;
import com.nhnacademy.miniDooray.entity.Member;
import com.nhnacademy.miniDooray.exception.IdAlreadyExistsException;
import com.nhnacademy.miniDooray.exception.IdNotFoundException;
import com.nhnacademy.miniDooray.repository.MemberRepository;
import com.nhnacademy.miniDooray.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDto registerMember(MemberDto memberDto) {
        if (memberDto == null) {
            throw new IllegalArgumentException();
        }

        if (memberRepository.existsById(memberDto.getId())) {
            throw new IdAlreadyExistsException("Member id가 이미 존재합니다. " + memberDto.getId());
        }

        Member member = new Member(
                memberDto.getId(),
                memberDto.getPassword(),
                memberDto.getEmail(),
                memberDto.getName(),
                memberDto.getStatus()
        );

        memberRepository.save(member);

        return convertToDto(member);
    }

    @Override
    public MemberDto getMember(String memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다."));

        return convertToDto(member);
    }

    @Override
    public MemberDto updateMember(String memberId, MemberDto memberDto) {
        if (memberId == null) {
            throw new IllegalArgumentException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다."));

        if (memberDto.getPassword() != null) {
            member.setPassword(memberDto.getPassword());
        }
        if (memberDto.getEmail() != null) {
            member.setEmail(memberDto.getEmail());
        }
        if (memberDto.getName() != null) {
            member.setName(memberDto.getName());
        }
        if (memberDto.getStatus() != null) {
            member.setStatus(memberDto.getStatus());
        }

        memberRepository.save(member);

        return convertToDto(member);
    }

    @Override
    public void deleteMember(String memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다."));

        memberRepository.delete(member);
    }

    @Override
    public Page<MemberDto> getMembers(int page, int size) {
        if (page < 0 || size < 0) {
            throw new IllegalArgumentException();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> membersPage = memberRepository.findAll(pageable);

        return membersPage.map(member -> new MemberDto(
                member.getId(),
                member.getPassword(),
                member.getEmail(),
                member.getName(),
                member.getStatus()
        ));
    }

    @Override
    public boolean matches(String memberId, String password) {
        if (memberId == null || password == null){
            throw new IllegalArgumentException();
        }

        return memberRepository.existsByIdAndPassword(memberId, password);
    }

    private MemberDto convertToDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getPassword(),
                member.getEmail(),
                member.getName(),
                member.getStatus()
        );
    }

}
