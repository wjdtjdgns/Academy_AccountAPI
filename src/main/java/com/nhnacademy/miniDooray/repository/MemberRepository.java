package com.nhnacademy.miniDooray.repository;

import com.nhnacademy.miniDooray.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByIdAndPassword(String id, String password);
}
