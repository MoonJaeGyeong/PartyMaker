package com.partymaker.party.dto.response;

import com.partymaker.party.dto.CharacterInfo;

import java.util.List;

public record GetResponse(List<CharacterInfo> rows) {
}
