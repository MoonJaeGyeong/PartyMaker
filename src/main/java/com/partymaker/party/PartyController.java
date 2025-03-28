package com.partymaker.party;

import com.partymaker.party.dto.request.Character;
import com.partymaker.party.dto.response.GetCharacterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/party")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @PostMapping("/create")
    public ResponseEntity<?> createParty(@RequestBody List<Character> request){
        Object response = partyService.createParty(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCharacter")
    public ResponseEntity<GetCharacterResponse> getCharacter(@RequestParam("serverId") String serverId, @RequestParam("characterName") String characterName){
        GetCharacterResponse response = partyService.getCharacter(serverId, characterName);
        return ResponseEntity.ok(response);
    }
}
