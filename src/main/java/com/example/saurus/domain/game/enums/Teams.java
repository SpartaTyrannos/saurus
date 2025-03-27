package com.example.saurus.domain.game.enums;

import lombok.Getter;

@Getter
public enum Teams {
    SAMSUNG_LIONS("삼성 라이온즈"),
    DOOSAN_BEARS("두산 베어스"),
    LG_TWINS("엘지 트윈스"),
    SSG_LANDERS("SSG 랜더스"),
    KIA_TIGERS("기아 타이거즈"),
    NC_DINOS("엔씨 다이노스"),
    HANWHA_EAGLES("한화 이글스"),
    KT_WIZ("케이티 위즈"),
    LOTTE_GIANTS("롯데 자이언츠"),
    KIWOOM_HEROES("키움 히어로즈");

    private final String description;

    Teams(String description) {
        this.description = description;
    }
}