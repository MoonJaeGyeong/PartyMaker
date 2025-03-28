package com.partymaker.party;

public record Character(
        String characterId,
        String serverName,
        String ownedName
) {
}
