package com.yusssss.sso.ticketservice.dtos.ticket;

import com.yusssss.sso.ticketservice.entities.Ticket;
import com.yusssss.sso.ticketservice.entities.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TicketDto {

    //will implement includeUser and includeEvent later
    private UUID id;

    private UUID userId;

    private UUID eventId;

    private TicketStatus status;

    private LocalDateTime createdAt;

    public TicketDto(Ticket ticket) {
        this.id = ticket.getId();
        this.userId = ticket.getUserId();
        this.eventId = ticket.getEventId();
        this.status = ticket.getStatus();
        this.createdAt = ticket.getCreatedAt();
    }
}
