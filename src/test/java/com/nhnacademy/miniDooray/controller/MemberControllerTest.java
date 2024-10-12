package com.nhnacademy.miniDooray.controller;

import com.nhnacademy.miniDooray.dto.MemberDto;
import com.nhnacademy.miniDooray.entity.Status;
import com.nhnacademy.miniDooray.exception.IllegalIdOrPasswordException;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("POST - /member/register")
    void testRegisterMember() throws Exception {
        MemberDto memberDto = new MemberDto("testId", "testPassword", "test@Email.com", "testName", Status.REGISTERED);
        when(memberService.registerMember(any(MemberDto.class))).thenReturn(memberDto);

        mockMvc.perform(post("/member/register")
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
    @DisplayName("GET - /member/{memberId}")
    void testGetMember() throws Exception {
        MemberDto memberDto = new MemberDto("testId", "testPassword", "test@Email.com", "testName", Status.DORMANT);
        when(memberService.getMember(anyString())).thenReturn(memberDto);

        mockMvc.perform(get("/member/testId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.password").value("testPassword"))
                .andExpect(jsonPath("$.email").value("test@Email.com"))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.status").value("DORMANT"));
    }

    @Test
    @DisplayName("GET - /member")
    void testGetMembers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> page = new PageImpl<>(Collections.singletonList(new MemberDto("testId", "testPassword","test@Email.com","testName",Status.WITHDRAWN)), pageable, 1);
        when(memberService.getMembers(any(Integer.class), any(Integer.class))).thenReturn(page);

        mockMvc.perform(get("/member")
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
    @DisplayName("PUT - /member/{memberId}")
    void testUpdateMember() throws Exception {
        MemberDto updatedMemberDto = new MemberDto("updatedId", "updatedPassword", "updated@Email.com", "updatedName", Status.DORMANT);
        when(memberService.updateMember(anyString(), any(MemberDto.class))).thenReturn(updatedMemberDto);

        mockMvc.perform(put("/member/updatedId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"updatedId\",\"name\":\"updatedName\",\"password\":\"updatedPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("updatedId"))
                .andExpect(jsonPath("$.password").value("updatedPassword"))
                .andExpect(jsonPath("$.email").value("updated@Email.com"))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.status").value("DORMANT"));
    }

    @Test
    @DisplayName("DELETE - /member/{memberId}")
    void testDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(anyString());

        mockMvc.perform(delete("/member/testId"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST - /member/login")
    void testDoLogin_Success() throws Exception {
        when(memberService.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/member/login")
                        .param("memberId", "testId")
                        .param("password", "testPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST - /member/login")
    void testDoLogin_Failure() throws Exception {
        when(memberService.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/member/login")
                        .param("memberId", "wrongId")
                        .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
    }
}