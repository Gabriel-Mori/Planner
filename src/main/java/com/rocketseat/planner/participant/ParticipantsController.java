package com.rocketseat.planner.participant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantsController {
    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequest payload){
        Optional<Participant> participant = this.participantRepository.findById(id);

        if(participant.isPresent()){
         Participant updatedParticipant = participant.get();

         updatedParticipant.setIsConfirmed(true);
         updatedParticipant.setName(payload.name());

         this.participantRepository.save(updatedParticipant);
         return ResponseEntity.ok(updatedParticipant);

        }

        return ResponseEntity.notFound().build();
    }

}
