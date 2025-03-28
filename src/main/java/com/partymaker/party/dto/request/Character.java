package com.partymaker.party.dto.request;

public record Character(
        String characterName,
        String ownedName,
        boolean isBuffer,
        long power
) {
}
