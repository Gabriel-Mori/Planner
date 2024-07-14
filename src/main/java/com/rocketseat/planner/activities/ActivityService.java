package com.rocketseat.planner.activities;

import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActitivitiesRepository repository;

    public ActivityResponse saveActivity(ActivityRequest activityRequest, Trip trip) {
        Activity activity = new Activity(activityRequest.title(), activityRequest.occurs_at(), trip);

        this.repository.save(activity);

        return new ActivityResponse(activity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID tripId){
        return this.repository.findByTripId(tripId).stream().map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}
