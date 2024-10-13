package com.nhnacademy.miniDooray.service.impl;

import com.nhnacademy.miniDooray.dto.MemberDto;
import com.nhnacademy.miniDooray.dto.MemberInfoDto;
import com.nhnacademy.miniDooray.dto.RegisterRequest;
import com.nhnacademy.miniDooray.dto.UpdateRequest;
import com.nhnacademy.miniDooray.entity.Member;
import com.nhnacademy.miniDooray.entity.Status;
import com.nhnacademy.miniDooray.exception.IdAlreadyExistsException;
import com.nhnacademy.miniDooray.exception.IdNotFoundException;
import com.nhnacademy.miniDooray.exception.StatusIsWithdrawnException;
import com.nhnacademy.miniDooray.repository.MemberRepository;
import com.nhnacademy.miniDooray.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDto registerMember(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException();
        }

        if (memberRepository.existsById(registerRequest.getId())) {
            throw new IdAlreadyExistsException("해당 ID가 이미 존재합니다. id: " + registerRequest.getId());
        }

        Member member = new Member(
                registerRequest.getId(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getName(),
                Status.REGISTERED
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

        if(member.getStatus() == Status.WITHDRAWN){
            throw new StatusIsWithdrawnException("탈퇴한 회원입니다. id:" + memberId);
        }

        return convertToDto(member);
    }

    @Override
    public MemberDto updateMember(String memberId, UpdateRequest updateRequest) {
        if (memberId == null) {
            throw new IllegalArgumentException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다."));

        if (updateRequest.getPassword() != null) {
            member.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getEmail() != null) {
            member.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getName() != null) {
            member.setName(updateRequest.getName());
        }
        if (updateRequest.getStatus() != null) {
            member.setStatus(updateRequest.getStatus());
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

        member.setStatus(Status.WITHDRAWN);
        memberRepository.save(member);
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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다."));

        if(member.getStatus() == Status.WITHDRAWN){
            throw new StatusIsWithdrawnException("탈퇴한 회원입니다. id:" + memberId);
        }

        return memberRepository.existsByIdAndPassword(memberId, password);
    }

    public List<MemberInfoDto> lookupMembers(List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<MemberInfoDto> memberInfos = new ArrayList<>();

        for (String memberId : memberIds) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IdNotFoundException("해당 ID가 없습니다: "));
            memberInfos.add(new MemberInfoDto(member.getId(), member.getName()));
        }

        return memberInfos;
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
