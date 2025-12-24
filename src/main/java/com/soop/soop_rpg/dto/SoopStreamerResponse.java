package com.soop.soop_rpg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * [클래스 역할]: SOOP API로부터 받은 데이터를 자바 객체로 변환하기 위한 그릇(DTO)입니다.
 * @JsonIgnoreProperties(ignoreUnknown = true):
 * API 응답 데이터 중 우리가 만든 변수 외에 모르는 데이터가 있어도 에러를 내지 말고 무시하라는 뜻입니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SoopStreamerResponse(
        // SOOP API의 "broad_list"라는 이름의 데이터를 자바의 List(목록)로 연결합니다.
        @JsonProperty("broad_list") List<BroadInfo> broadList
) {
    /**
     * [내부 클래스 역할]: 방송 한 개 한 개의 상세 정보를 담습니다.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BroadInfo(
            @JsonProperty("user_nick") String nickname, // API의 "user_nick"을 닉네임 변수에 저장
            @JsonProperty("total_view_cnt") int viewCount // API의 "total_view_cnt"를 시청자 수 변수에 저장
    ) {}
}