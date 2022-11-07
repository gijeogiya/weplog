package com.ssafy.challengeservice.dto;

import java.util.UUID;

public interface ChallengeRankingDtoInterface {
    UUID getMemberId();
    String getName();
    String getNickname();
    String getProfileImageUrl();
    Double getContribution();
    Integer getRanking();
    Long getChallengeId();
}
