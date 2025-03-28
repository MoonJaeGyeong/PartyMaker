package com.partymaker.party.dto.response;

import com.partymaker.party.dto.request.Character;

import java.util.List;

public record CreatePartyResponse(
        List<Character> characters,
        String totalPower
) {
}
