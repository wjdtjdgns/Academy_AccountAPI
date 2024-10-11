package com.nhnacademy.miniDooray.service;

import com.nhnacademy.miniDooray.dto.MemberDto;
import org.springframework.data.domain.Page;

public interface MemberService {
    MemberDto registerMember(MemberDto memberDto);
    MemberDto getMember(String memberId);
    MemberDto updateMember(String memberId, MemberDto memberDto);
    void deleteMember(String memberId);
    Page<MemberDto> getMembers(int page, int size);
    boolean matches(String memberId, String password);
}
