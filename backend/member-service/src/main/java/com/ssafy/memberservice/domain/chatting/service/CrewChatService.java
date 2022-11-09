package com.ssafy.memberservice.domain.chatting.service;

import com.ssafy.memberservice.domain.chatting.dao.CrewChatRepository;
import com.ssafy.memberservice.domain.chatting.domain.CrewChatRoom;
import com.ssafy.memberservice.domain.chatting.domain.Participant;
import com.ssafy.memberservice.domain.chatting.domain.PloggingChatRoom;
import com.ssafy.memberservice.domain.chatting.domain.enums.Color;
import com.ssafy.memberservice.domain.chatting.domain.enums.MessageType;
import com.ssafy.memberservice.domain.chatting.dto.ChatMessage;
import com.ssafy.memberservice.domain.chatting.dto.PloggingChatRoomResponse;
import com.ssafy.memberservice.domain.member.dao.MemberRepository;
import com.ssafy.memberservice.domain.member.domain.Member;
import com.ssafy.memberservice.domain.membercrew.dao.MemberCrewRepository;
import com.ssafy.memberservice.domain.membercrew.domain.MemberCrew;
import com.ssafy.memberservice.global.common.error.exception.DuplicateException;
import com.ssafy.memberservice.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static com.ssafy.memberservice.global.common.error.exception.NotFoundException.JOINWAITING_NOT_FOUND;
import static com.ssafy.memberservice.global.common.error.exception.NotFoundException.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CrewChatService {

    private final ChannelTopic crewTopic;

    private final RedisTemplate redisTemplate;

    private final CrewChatRepository crewChatRepository;

    private final MemberRepository memberRepository;

    private final MemberCrewRepository memberCrewRepository;


    public CrewChatRoom makeRoom(String memberId, Long crewId) {
        // 멤버가 해당 크루인지 확인
        MemberCrew memberCrew = memberCrewRepository.findMemberCrewByMemberIdAndCrewId(UUID.fromString(memberId), crewId).orElseThrow(() -> new NotFoundException(JOINWAITING_NOT_FOUND));

        // 문제 없으니 방 생성
        CrewChatRoom crewChatRoom = crewChatRepository.save(CrewChatRoom.create(crewId, memberCrew));

        return crewChatRoom;
    }

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    public String getUriWithoutRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(10, lastIndex);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
//        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + "님이 방에 입장했습니다.");
            chatMessage.setSender(Participant.builder().nickname("[알림]").build());
        } else if (MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender().getNickname() + "님이 방에서 나갔습니다.");
            chatMessage.setSender(Participant.builder().nickname("[알림]").build());
        }

        log.info("plogging - {}", chatMessage.getMessage());
        redisTemplate.convertAndSend(crewTopic.getTopic(), chatMessage);

    }

    public void sendChatMessage(ChatMessage chatMessage, String memberId) {
        Member member = memberRepository.findById(UUID.fromString(memberId)).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        chatMessage.setSender(Participant.from(member));
        sendChatMessage(chatMessage);
    }

    public CrewChatRoom joinRoom(Long roomId, Member member) {
        CrewChatRoom crewChatRoom = crewChatRepository.findById(roomId).orElseThrow(() -> new NotFoundException("해당 방이 존재하지 않습니다."));


        Participant participant = Participant.from(member);
        crewChatRoom.getPlayerMap().put(member.getId().toString(), participant);
        return crewChatRepository.save(crewChatRoom);
    }

    public void quitRoom(Long roomId, Member member) {
        CrewChatRoom crewChatRoom = crewChatRepository.findById(roomId).orElseThrow(() -> new NotFoundException("방이 없습니다."));

        crewChatRoom.getPlayerMap().remove(member.getId().toString());
    }
}