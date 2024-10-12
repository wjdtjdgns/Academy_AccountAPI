package com.nhnacademy.miniDooray.controller;

import com.nhnacademy.miniDooray.dto.*;
import com.nhnacademy.miniDooray.exception.IllegalIdOrPasswordException;
import com.nhnacademy.miniDooray.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/member")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Register a new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Member ID already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<MemberDto> registerMember(@Validated @RequestBody RegisterRequest registerRequest) {
        MemberDto registerDto = memberService.registerMember(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerDto);
    }


    @Operation(summary = "Get a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden to this member's data"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> getMember(@PathVariable String memberId) {
        MemberDto memberDto = memberService.getMember(memberId);
        return ResponseEntity.ok(memberDto);
    }


    @Operation(summary = "Get all members with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<Page<MemberDto>> getMembers(Pageable pageable) {
        Page<MemberDto> memberDtoList = memberService.getMembers(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(memberDtoList);
    }


    @Operation(summary = "Update a member's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable String memberId, @Validated @RequestBody UpdateRequest updateRequest) {
        MemberDto updateMember = memberService.updateMember(memberId, updateRequest);
        return ResponseEntity.ok(updateMember);
    }


    @Operation(summary = "Delete a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member status updated to WITHDRAWN"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid member ID or password")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> doLogin(@RequestBody @Validated LoginRequest loginRequest) {

        if (!memberService.matches(loginRequest.getId(), loginRequest.getPassword())) {
            throw new IllegalIdOrPasswordException("Id 나 Password가 일치하지 않습니다.");
        }

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Lookup members by their IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members found"),
            @ApiResponse(responseCode = "400", description = "Invalid member IDs")
    })
    @PostMapping("/lookup")
    public ResponseEntity<List<MemberInfoDto>> lookupMembers(@RequestBody MemberRequest memberRequest){
        List<MemberInfoDto> memberInfoList = memberService.lookupMembers(memberRequest.getMemberIds());
        return ResponseEntity.ok(memberInfoList);
    }

}
