package com.yusssss.sso.ticketservice.dataAccess;

import com.yusssss.sso.ticketservice.entities.Ticket;
import com.yusssss.sso.ticketservice.entities.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketDao extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUserId(UUID uuid);

    List<Ticket> findByEventId(UUID eventId);

    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

    List<Ticket> findByEventIdAndStatus(UUID eventId, TicketStatus ticketStatus);
}
