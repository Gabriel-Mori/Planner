package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){
      List<Participant> participants =  participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.participantRepository.saveAll(participants);
      System.out.println(participants.get(0).getId());


    }

    public ParticipantsCreateResponse registerParticipant(String email, Trip trip){
        Participant participant = new Participant(email, trip);

        this.participantRepository.save(participant);

        return new ParticipantsCreateResponse(participant.getId());
    }

    public List<ParticipantData> getAllParticipantsFromEvent(UUID tripId){
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantData(participant.getName(), participant.getEmail(), participant.getIsConfirmed(), participant.getId())).toList();


    }

    public void triggerConfirmationEmailToParticiapants(UUID tripId){}

    public void triggerConfirmationEmailToParticiapant(String email){}
}
