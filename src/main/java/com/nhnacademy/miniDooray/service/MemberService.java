package com.nhnacademy.miniDooray.service;

import com.nhnacademy.miniDooray.dto.MemberDto;
import com.nhnacademy.miniDooray.dto.MemberInfoDto;
import com.nhnacademy.miniDooray.dto.RegisterRequest;
import com.nhnacademy.miniDooray.dto.UpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    MemberDto registerMember(RegisterRequest registerRequest);
    MemberDto getMember(String memberId);
    MemberDto updateMember(String memberId, UpdateRequest updateRequest);
    void deleteMember(String memberId);
    Page<MemberDto> getMembers(int page, int size);
    boolean matches(String memberId, String password);
    List<MemberInfoDto> lookupMembers(List<String> memberIds);
}
