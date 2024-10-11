package com.nhnacademy.miniDooray.entity;

public enum Status {
    REGISTERED("가입"),
    WITHDRAWN("탈퇴"),
    DORMANT("휴면");

    private final String koreanValue;

    Status(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public String getKoreanValue() {
        return koreanValue;
    }
}
