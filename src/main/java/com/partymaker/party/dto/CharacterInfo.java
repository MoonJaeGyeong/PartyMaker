package com.partymaker.party.dto;

public record CharacterInfo(
        String serverId,
        String characterId,
        String characterName,
        int level,
        String jobId,
        String jobGrowId,
        String jobName,
        String jobGrowName,
        int fame
) {
}