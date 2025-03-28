package com.partymaker.party.dto.request;

import java.math.BigInteger;

public record Character(
        String characterName,
        String ownedName,
        boolean isBuffer,
        BigInteger power
) {
}
