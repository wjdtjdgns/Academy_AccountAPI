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
import java.util.List;
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
        RegisterRequest registerRequest = new RegisterRequest("123", "456", "dign552@naver.com", "두레이");
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.existsById(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto result = memberService.registerMember(registerRequest);

        assertEquals(registerRequest.getId(), result.getId());
    }

    @Test
    void registerMember_alreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest("123", "456", "dign552@naver.com", "두레이");
        when(memberRepository.existsById("123")).thenReturn(true);

        assertThrows(IdAlreadyExistsException.class, () -> memberService.registerMember(registerRequest));
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
    void getMember_withWithdrawnStatus() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.WITHDRAWN);
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        assertThrows(StatusIsWithdrawnException.class, () -> memberService.getMember("123"));
    }



    @Test
    void updateMember_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        UpdateRequest updateRequest = new UpdateRequest( "789", "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", updateRequest);

        assertEquals("789", result.getPassword());
        assertEquals("뚜레이", result.getName());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
    }

    @Test
    void updateMember_notFound() {
        UpdateRequest updateRequest = new UpdateRequest("789", "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> memberService.updateMember("123", updateRequest));
    }


    @Test
    void deleteMember_success() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(member));

        memberService.deleteMember("123");

        assertEquals(Status.WITHDRAWN, member.getStatus());
        verify(memberRepository, times(1)).findById("123");
        verify(memberRepository, times(1)).save(member);
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
        Member mockMember = new Member("123", "password", "email@example.com", "name", Status.REGISTERED);
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(mockMember));
        when(memberRepository.existsByIdAndPassword(anyString(), anyString())).thenReturn(true);

        boolean result = memberService.matches("123", "456");

        assertTrue(result);
        verify(memberRepository, times(1)).existsByIdAndPassword(anyString(), anyString());
        verify(memberRepository, times(1)).findById(anyString());
    }

    @Test
    void matches_invalidCredentials() {
        Member mockMember = new Member("123", "password", "email@example.com", "name", Status.REGISTERED);
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(mockMember));
        when(memberRepository.existsByIdAndPassword(anyString(), anyString())).thenReturn(false);

        boolean result = memberService.matches("123", "456");

        assertFalse(result);
        verify(memberRepository, times(1)).existsByIdAndPassword(anyString(), anyString());
        verify(memberRepository, times(1)).findById(anyString());
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
        UpdateRequest updateRequest = new UpdateRequest("456", "dign552@naver.com", "두레이", Status.REGISTERED);
        assertThrows(IllegalArgumentException.class, () -> memberService.updateMember(null, updateRequest));
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
        UpdateRequest updateRequest = new UpdateRequest(null, "wjdtjdgns@naver.com", "뚜레이", Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));
        MemberDto result = memberService.updateMember("123", updateRequest);
        
        assertEquals("456", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals("뚜레이", result.getName());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_emailNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        UpdateRequest updateRequest = new UpdateRequest("789", null, "뚜레이", Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", updateRequest);

        assertEquals("dign552@naver.com", result.getEmail());
        assertEquals("789", result.getPassword());
        assertEquals("뚜레이", result.getName());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_nameNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        UpdateRequest updateRequest = new UpdateRequest("789", "wjdtjdgns@naver.com", null, Status.DORMANT);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", updateRequest);

        assertEquals("두레이", result.getName());
        assertEquals("789", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals(Status.DORMANT, result.getStatus());
    }

    @Test
    void updateMember_statusNull() {
        Member member = new Member("123", "456", "dign552@naver.com", "두레이", Status.REGISTERED);
        UpdateRequest updateRequest = new UpdateRequest("789", "wjdtjdgns@naver.com", "뚜레이", null);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        MemberDto result = memberService.updateMember("123", updateRequest);

        assertEquals(Status.REGISTERED, result.getStatus());
        assertEquals("789", result.getPassword());
        assertEquals("wjdtjdgns@naver.com", result.getEmail());
        assertEquals("뚜레이", result.getName());
    }

    @Test
    void lookupMembers_emptyMemberIds() {
        assertThrows(IllegalArgumentException.class, () -> memberService.lookupMembers(Collections.emptyList()));
    }

    @Test
    void lookupMembers_nullMemberIds() {
        assertThrows(IllegalArgumentException.class, () -> memberService.lookupMembers(null));
    }

    @Test
    void lookupMembers_success() {
        List<String> memberIds = List.of("123", "456");
        Member member1 = new Member("123", "password1", "dign552@naver.com", "두레이", Status.REGISTERED);
        Member member2 = new Member("456", "password2", "dododo@naver.com", "두영호", Status.REGISTERED);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member1));
        when(memberRepository.findById("456")).thenReturn(Optional.of(member2));

        List<MemberInfoDto> result = memberService.lookupMembers(memberIds);

        assertEquals(2, result.size());
        assertEquals("두레이", result.get(0).getName());
        assertEquals("두영호", result.get(1).getName());
    }

    @Test
    void lookupMembers_memberIdNotFound() {
        List<String> memberIds = List.of("123", "789");
        Member member = new Member("123", "password1", "dign552@naver.com", "두레이", Status.REGISTERED);

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));
        when(memberRepository.findById("789")).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> memberService.lookupMembers(memberIds));
    }


}
