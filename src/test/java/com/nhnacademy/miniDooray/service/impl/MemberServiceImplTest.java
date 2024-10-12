package com.nhnacademy.miniDooray.service.impl;

import com.nhnacademy.miniDooray.dto.MemberDto;
import com.nhnacademy.miniDooray.entity.Member;
import com.nhnacademy.miniDooray.entity.Status;
import com.nhnacademy.miniDooray.exception.IdAlreadyExistsException;
import com.nhnacademy.miniDooray.exception.IdNotFoundException;
import com.nhnacademy.miniDooray.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void registerMember_success() {
        MemberDto memberDto = new MemberDto("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.existsById(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto result = memberService.registerMember(memberDto);

        assertEquals(memberDto.getId(), result.getId());
    }

    @Test
    void registerMember_alreadyExists() {
        MemberDto memberDto = new MemberDto("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.existsById("123")).thenReturn(true);

        assertThrows(IdAlreadyExistsException.class, () -> memberService.registerMember(memberDto));
    }

    @Test
    void getMember_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.getMember("123");

        assertEquals("123", result.getId());
        assertEquals("두레이", result.getName());
    }


    @Test
    void getMember_notFound() {
        when(memberRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> memberService.getMember("123"));
    }


    @Test
    void updateMember_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        MemberDto updatedDto = new MemberDto("123", "789", "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", updatedDto);

        assertEquals("789", result.getPassword());
        assertEquals("뚜레이", result.getName());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
    }

    @Test
    void updateMember_notFound() {
        MemberDto memberDto = new MemberDto("123", "789", "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> memberService.updateMember("123", memberDto));
    }


    @Test
    void deleteMember_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(member));

        memberService.deleteMember("123");

        verify(memberRepository, times(1)).delete(any(Member.class));
    }

    @Test
    void deleteMember_notFound() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> memberService.deleteMember("123"));
    }

    @Test
    void getMembers_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        Page<Member> membersPage = new PageImpl<>(Collections.singletonList(member));
        Pageable pageable = PageRequest.of(0, 1);
        when(memberRepository.findAll(pageable)).thenReturn(membersPage);

        Page<MemberDto> result = memberService.getMembers(0, 1);

        assertEquals(1, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    void matches_success() {
        when(memberRepository.existsByIdAndPassword(anyString(), anyString())).thenReturn(true);

        boolean result = memberService.matches("123", "456");

        assertTrue(result);
        verify(memberRepository, times(1)).existsByIdAndPassword(anyString(), anyString());
    }

    @Test
    void matches_invalidCredentials() {
        when(memberRepository.existsByIdAndPassword(anyString(), anyString())).thenReturn(false);

        boolean result = memberService.matches("123", "456");

        assertFalse(result);
        verify(memberRepository, times(1)).existsByIdAndPassword(anyString(), anyString());
    }

    @Test
    void registerMember_nullMemberDto() {
        assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(null));
    }

    @Test
    void getMember_nullMemberId() {
        assertThrows(IllegalArgumentException.class, () -> memberService.getMember(null));
    }

    @Test
    void updateMember_nullMemberId() {
        MemberDto memberDto = new MemberDto("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        assertThrows(IllegalArgumentException.class, () -> memberService.updateMember(null, memberDto));
    }

    @Test
    void deleteMember_nullMemberId() {
        assertThrows(IllegalArgumentException.class, () -> memberService.deleteMember(null));
    }

    @Test
    void getMembers_invalidPageOrSize() {
        assertThrows(IllegalArgumentException.class, () -> memberService.getMembers(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> memberService.getMembers(0, -1));
    }

    @Test
    void matches_nullMemberIdOrPassword() {
        assertThrows(IllegalArgumentException.class, () -> memberService.matches(null, "456"));
        assertThrows(IllegalArgumentException.class, () -> memberService.matches("memberId", null));
    }

    @Test
    void updateMember_passwordNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        MemberDto memberDto = new MemberDto("123", null, "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));
        MemberDto result = memberService.updateMember("123", memberDto);
        
        assertEquals("456", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals("뚜레이", result.getName());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_emailNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        MemberDto memberDto = new MemberDto("123", "789", null, "뚜레이", Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", memberDto);

        assertEquals("dign552@naver.com", result.getEmail());
        assertEquals("789", result.getPassword());
        assertEquals("뚜레이", result.getName());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_nameNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        MemberDto memberDto = new MemberDto("123", "789", "wjdtjdgns@naver.com", null, Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", memberDto);

        assertEquals("두레이", result.getName());
        assertEquals("789", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_statusNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        MemberDto memberDto = new MemberDto("123", "789", "wjdtjdgns@naver.com", "뚜레이", null);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", memberDto);

        assertEquals(Status.REGISTERED, result.getStatus());
        assertEquals("789", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals("뚜레이", result.getName());
    }

}
