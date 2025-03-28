package com.partymaker.party.dto.response;

import com.partymaker.party.dto.CharacterInfo;

public record GetCharacterResponse(
        String serverId,
        String characterName,
        String jobName,
        int power
) {
    public static GetCharacterResponse of(CharacterInfo info, int power){
        return new GetCharacterResponse(info.serverId(), info.characterName(), info.jobGrowName().substring(2), power);
    }
}
