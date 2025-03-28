package com.partymaker.party.dto.response;

import com.partymaker.party.dto.CharacterInfo;
import com.partymaker.party.dto.GetResponse;

public record GetCharacterResponse(
        String serverId,
        String characterName,
        String jobName,
        Long power
) {
    public static GetCharacterResponse of(GetResponse getResponse, long power){
        if(getResponse == null){
            return null;
        }

        CharacterInfo info = getResponse.rows().get(0);

        return new GetCharacterResponse(info.serverId(), info.characterName(), info.jobGrowName().substring(2), power);
    }
}
