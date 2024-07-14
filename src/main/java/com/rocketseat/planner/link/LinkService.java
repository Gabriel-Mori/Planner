package com.rocketseat.planner.link;

import com.rocketseat.planner.activities.Activity;
import com.rocketseat.planner.activities.ActivityData;
import com.rocketseat.planner.activities.ActivityRequest;
import com.rocketseat.planner.activities.ActivityResponse;
import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public LinkResponse saveLink(LinkRequest linkRequest, Trip trip) {
        Link link = new Link(linkRequest.title(), linkRequest.url(), trip);

        this.linkRepository.save(link);

        return new LinkResponse(link.getId());
    }


    public List<LinkData> getLinkFromId(UUID tripId){
        return this.linkRepository.findByTripId(tripId).stream().map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl())).toList();
    }

}
