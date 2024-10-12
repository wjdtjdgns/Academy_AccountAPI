package com.nhnacademy.miniDooray.repository;

import com.nhnacademy.miniDooray.entity.Member;
import com.nhnacademy.miniDooray.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void existsById() {
        String id = "testId";
        String password = "testPassword";
        String email = "testEmail@naver.com";
        String name = "testName";
        Status status = Status.REGISTERED;

        boolean exists = memberRepository.existsById(id);
        assertFalse(exists);

        memberRepository.save(new Member(id, password, email, name, status));
        exists = memberRepository.existsById(id);
        assertTrue(exists);

        exists = memberRepository.existsById("wrongId");
        assertFalse(exists);

        memberRepository.deleteAll();
        exists = memberRepository.existsById(id);
        assertFalse(exists);
    }

    @Test
    void save() {
        String id = "testId";
        String password = "testPassword";
        String email = "testEmail@naver.com";
        String name = "testName";
        Status status = Status.REGISTERED;

        Member member = new Member(id, password, email, name, status);
        Member savedMember = memberRepository.save(member);

        assertNotNull(savedMember);
        assertEquals(id, savedMember.getId());
        assertEquals(password, savedMember.getPassword());
        assertEquals(email, savedMember.getEmail());
        assertEquals(name, savedMember.getName());
        assertEquals(status, savedMember.getStatus());
    }

    @Test
    void findById() {
        String id = "testId";
        String password = "testPassword";
        String email = "testEmail@naver.com";
        String name = "testName";
        Status status = Status.REGISTERED;

        Member member = new Member(id, password, email, name, status);
        memberRepository.save(member);

        Member foundMember = memberRepository.findById(id).orElse(null);

        assertNotNull(foundMember);
        assertEquals(id, foundMember.getId());
        assertEquals(password, foundMember.getPassword());
        assertEquals(email, foundMember.getEmail());
        assertEquals(name, foundMember.getName());
        assertEquals(status, foundMember.getStatus());
    }

    @Test
    void delete() {
        String id = "testId";
        String password = "testPassword";
        String email = "testEmail@naver.com";
        String name = "testName";
        Status status = Status.REGISTERED;

        Member member = new Member(id, password, email, name, status);
        memberRepository.save(member);

        Member foundMember = memberRepository.findById(id).orElse(null);
        assertNotNull(foundMember);
        assertEquals(id, foundMember.getId());
        assertEquals(password, foundMember.getPassword());
        assertEquals(email, foundMember.getEmail());
        assertEquals(name, foundMember.getName());
        assertEquals(status, foundMember.getStatus());

        memberRepository.delete(member);

        foundMember = memberRepository.findById(id).orElse(null);

        assertNull(foundMember);
    }

    @Test
    void pageFindAll() {
        String id1 = "testId1";
        String password1 = "testPassword1";
        String email1 = "testEmail1@naver.com";
        String name1 = "testName1";
        Status status1 = Status.REGISTERED;

        String id2 = "testId2";
        String password2 = "testPassword2";
        String email2 = "testEmail2@naver.com";
        String name2 = "testName2";
        Status status2 = Status.WITHDRAWN;

        String id3 = "testId3";
        String password3 = "testPassword3";
        String email3 = "testEmail3@naver.com";
        String name3 = "testName3";
        Status status3 = Status.DORMANT;

        memberRepository.save(new Member(id1, password1, email1, name1, status1));
        memberRepository.save(new Member(id2, password2, email2, name2, status2));
        memberRepository.save(new Member(id3, password3, email3, name3, status3));

        Pageable pageable = PageRequest.of(0, 2);
        Page<Member> membersPage = memberRepository.findAll(pageable);

        assertEquals(2, membersPage.getSize());
        assertEquals(3, membersPage.getTotalElements());
        assertEquals(2, membersPage.getTotalPages());
    }

    @Test
    void existsByIdAndPassword() {
        String id = "testId";
        String password = "testPassword";
        String email = "testEmail@naver.com";
        String name = "testName";
        Status status = Status.REGISTERED;

        boolean exists = memberRepository.existsByIdAndPassword(id, password);
        assertFalse(exists);

        memberRepository.save(new Member(id, password, email, name, status));
        exists = memberRepository.existsByIdAndPassword(id, password);
        assertTrue(exists);

        exists = memberRepository.existsByIdAndPassword("testId", "wrongPassword");
        assertFalse(exists);

        exists = memberRepository.existsByIdAndPassword("wrongId", "testPassword");
        assertFalse(exists);

        exists = memberRepository.existsByIdAndPassword("wrongId", "wrongPassword");
        assertFalse(exists);

        exists = memberRepository.existsByIdAndPassword(null, "testPassword");
        assertFalse(exists);

        exists = memberRepository.existsByIdAndPassword("testId", null);
        assertFalse(exists);

        exists = memberRepository.existsByIdAndPassword(null, null);
        assertFalse(exists);

        memberRepository.deleteAll();
        exists = memberRepository.existsByIdAndPassword(id, password);
        assertFalse(exists);
    }
}