package com.yusssss.sso.ticketservice.dtos.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TicketRequest {

    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

}
