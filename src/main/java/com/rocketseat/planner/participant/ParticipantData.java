package com.rocketseat.planner.participant;

import java.util.UUID;

public record ParticipantData(String name, String email, Boolean isConfirmed, UUID id) {
}
