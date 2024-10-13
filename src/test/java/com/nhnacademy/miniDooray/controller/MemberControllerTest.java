package com.nhnacademy.miniDooray.controller;

import com.nhnacademy.miniDooray.dto.*;
import com.nhnacademy.miniDooray.entity.Status;
import com.nhnacademy.miniDooray.exception.IdAlreadyExistsException;
import com.nhnacademy.miniDooray.exception.IllegalIdOrPasswordException;
import com.nhnacademy.miniDooray.exception.StatusIsWithdrawnException;
import com.nhnacademy.miniDooray.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("POST - /members/register")
    void testRegisterMember() throws Exception {
        MemberDto memberDto = new MemberDto("testId", "testPassword", "test@Email.com", "testName", Status.REGISTERED);
        when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(memberDto);

        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testId\",\"password\":\"testPassword\",\"email\":\"test@Email.com\", \"name\": \"testName\",\"status\": \"REGISTERED\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.password").value("testPassword"))
                .andExpect(jsonPath("$.email").value("test@Email.com"))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.status").value("REGISTERED"));

    }

    @Test
    @DisplayName("POST - /members/register 실패 - 중복된 ID")
    void testRegisterMember_Failure_DuplicateId() throws Exception {
        when(memberService.registerMember(any(RegisterRequest.class)))
                .thenThrow(new IdAlreadyExistsException("해당 ID가 이미 존재합니다."));

        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"duplicateId\",\"password\":\"testPassword\",\"email\":\"test@Email.com\", \"name\": \"testName\",\"status\": \"REGISTERED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST - /members/register 실패 - 유효성 검증 실패")
    public void testRegisterMember_Failure_InvalidInput() throws Exception {
        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"\",\"password\":\"\",\"email\":\"invalid\",\"name\": \"\",\"status\": \"REGISTERED\"}"))
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("GET - /members/{memberId}")
    void testGetMember() throws Exception {
        MemberDto memberDto = new MemberDto("testId", "testPassword", "test@Email.com", "testName", Status.DORMANT);
        when(memberService.getMember(anyString())).thenReturn(memberDto);

        mockMvc.perform(get("/members/testId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.password").value("testPassword"))
                .andExpect(jsonPath("$.email").value("test@Email.com"))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.status").value("DORMANT"));
    }

    @Test
    @DisplayName("GET - /members/{memberId} - Forbidden")
    void testGetMember_Forbidden() throws Exception {
        when(memberService.getMember(anyString())).thenThrow(new StatusIsWithdrawnException("탈퇴한 회원입니다."));

        mockMvc.perform(get("/members/forbiddenId"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("GET - /members")
    void testGetMembers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> page = new PageImpl<>(Collections.singletonList(new MemberDto("testId", "testPassword","test@Email.com","testName",Status.WITHDRAWN)), pageable, 1);
        when(memberService.getMembers(any(Integer.class), any(Integer.class))).thenReturn(page);

        mockMvc.perform(get("/members")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("testId"))
                .andExpect(jsonPath("$.content[0].password").value("testPassword"))
                .andExpect(jsonPath("$.content[0].email").value("test@Email.com"))
                .andExpect(jsonPath("$.content[0].name").value("testName"))
                .andExpect(jsonPath("$.content[0].status").value("WITHDRAWN"));
    }


    @Test
    @DisplayName("PUT - /members/{memberId}")
    void testUpdateMember() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest("updatedPassword", "updated@Email.com", "updatedName", Status.DORMANT);
        MemberDto updatedMemberDto = new MemberDto("updatedId", "updatedPassword", "updated@Email.com", "updatedName", Status.DORMANT);

        when(memberService.updateMember(anyString(), any(UpdateRequest.class))).thenReturn(updatedMemberDto);

        mockMvc.perform(put("/members/updatedId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"updatedPassword\",\"email\":\"updated@Email.com\",\"name\":\"updatedName\",\"status\":\"DORMANT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("updatedId"))
                .andExpect(jsonPath("$.password").value("updatedPassword"))
                .andExpect(jsonPath("$.email").value("updated@Email.com"))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.status").value("DORMANT"));

        verify(memberService, times(1)).updateMember(anyString(), any(UpdateRequest.class));
    }

    @Test
    @DisplayName("DELETE - /members/{memberId}")
    void testDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(anyString());

        mockMvc.perform(delete("/members/testId"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST - /members/login")
    void testDoLogin_Success() throws Exception {
        when(memberService.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testId\",\"password\":\"testPassword\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST - /members/login 실패 - ID나 Password 불일치")
    void testDoLogin_Failure() throws Exception {
        when(memberService.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"wrongId\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST - /members/lookup")
    void testLookupMembers() throws Exception {
        List<MemberInfoDto> memberInfoList = List.of(new MemberInfoDto("testId", "testName"));

        when(memberService.lookupMembers(any())).thenReturn(memberInfoList);

        mockMvc.perform(post("/members/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberIds\":[\"testId\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("testId"))
                .andExpect(jsonPath("$[0].name").value("testName"));
    }
}