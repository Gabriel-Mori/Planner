package com.rocketseat.planner.trip;


import com.rocketseat.planner.activities.ActivityData;
import com.rocketseat.planner.activities.ActivityRequest;
import com.rocketseat.planner.activities.ActivityResponse;
import com.rocketseat.planner.activities.ActivityService;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequest;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.Participant;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.ParticipantRequest;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.participant.ParticipantsCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequest payload){
        Trip newTrip = new Trip(payload);


        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(),  newTrip);
        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID tripId){
        Optional<Trip> trip = this.tripRepository.findById(tripId);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{tripId}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID tripId, @RequestBody TripRequest payload){
        Optional<Trip> trip = this.tripRepository.findById(tripId);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();
            updatedTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setDestination(payload.destination());

            this.tripRepository.save(updatedTrip);

            return ResponseEntity.ok(updatedTrip);
        }
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{tripId}/confirmed")
    public ResponseEntity<Trip> confirmedTrip(@PathVariable UUID tripId){
        Optional<Trip> trip = this.tripRepository.findById(tripId);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();
           updatedTrip.setIsConfirmed(true);

            this.tripRepository.save(updatedTrip);
            this.participantService.triggerConfirmationEmailToParticiapants(tripId);

            return ResponseEntity.ok(updatedTrip);
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantsCreateResponse> inviteParticipants(@PathVariable UUID id, @RequestBody ParticipantRequest payload){
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();


            ParticipantsCreateResponse participantResponse = this.participantService.registerParticipant(payload.email(), updatedTrip);

            if(updatedTrip.getIsConfirmed())this.participantService.triggerConfirmationEmailToParticiapant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }
        return  ResponseEntity.notFound().build();
    }

    @GetMapping("/{tripId}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID tripId){
        List<ParticipantData> participant = this.participantService.getAllParticipantsFromEvent(tripId);
        return ResponseEntity.ok(participant);

    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActtivities(@PathVariable UUID id, @RequestBody ActivityRequest payload){
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            ActivityResponse activityResponse = this.activityService.saveActivity(payload, updatedTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return  ResponseEntity.notFound().build();
    }


    @GetMapping("/{tripId}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID tripId){
        List<ActivityData> activityData = this.activityService.getAllActivitiesFromId(tripId);
        return ResponseEntity.ok(activityData);

    }


    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequest payload){
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            LinkResponse linkResponse = this.linkService.saveLink(payload, updatedTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return  ResponseEntity.notFound().build();
    }


    @GetMapping("/{tripId}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID tripId){
        List<LinkData> linkData = this.linkService.getLinkFromId(tripId);
        return ResponseEntity.ok(linkData);

    }
}
