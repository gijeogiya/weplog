package com.ssafy.challengeservice.dto;

import com.ssafy.challengeservice.domain.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDetailRes {
    private String title;
    private int goal;
    private LocalDate endDate;
    private int participantsCnt;
    private int rewardPoint;
    private int progress;
    private double progressRate;
    private boolean finishFlag;
    private String imageUrl;
    private String memberId;
    private int totalDistance;
    private int totalTime;
    private int totalPloggingCnt;

    public static ChallengeDetailRes from(Challenge challenge){
        ChallengeDetailRes challengeDetailRes = new ChallengeDetailRes();

        challengeDetailRes.title = challenge.getTitle();
        challengeDetailRes.goal = challenge.getGoal();
        challengeDetailRes.endDate = challenge.getEndDate();
        challengeDetailRes.participantsCnt = challenge.getParticipantsCnt();
        challengeDetailRes.rewardPoint = challenge.getRewardPoint();
        challengeDetailRes.progress = challenge.getProgress();
        challengeDetailRes.progressRate = (challenge.getProgress() / challenge.getGoal()) * 100;
        challengeDetailRes.finishFlag = challenge.getFinishFlag();
        challengeDetailRes.imageUrl = challenge.getImageUrl();
        challengeDetailRes.memberId = challenge.getMember().getId().toString();
        challengeDetailRes.totalDistance = challenge.getTotalDistance();
        challengeDetailRes.totalTime = challenge.getTotalTime();
        challengeDetailRes.totalPloggingCnt = challenge.getTotalPloggingCnt();

        return challengeDetailRes;
    }
}
