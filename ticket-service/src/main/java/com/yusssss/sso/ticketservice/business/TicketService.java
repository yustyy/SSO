package com.yusssss.sso.ticketservice.business;

import com.nimbusds.jwt.SignedJWT;
import com.yusssss.sso.ticketservice.core.externalServices.UserFeignClient;
import com.yusssss.sso.ticketservice.dataAccess.TicketDao;
import com.yusssss.sso.ticketservice.dtos.ticket.TicketDto;
import com.yusssss.sso.ticketservice.dtos.ticket.TicketRequest;
import com.yusssss.sso.ticketservice.dtos.user.UserResult;
import com.yusssss.sso.ticketservice.entities.Ticket;
import com.yusssss.sso.ticketservice.entities.TicketStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketDao ticketDao;
    private final UserFeignClient userFeignClient;

    public TicketService(TicketDao ticketDao, UserFeignClient userFeignClient) {
        this.ticketDao = ticketDao;
        this.userFeignClient = userFeignClient;
    }


    public TicketDto createTicket(TicketRequest ticketRequest) {
        UserResult userResult = userFeignClient.getUserById(ticketRequest.getUserId()).getBody();
        if (userResult == null || !userResult.isSuccess()) {
            throw new RuntimeException("User not found with id: " + ticketRequest.getUserId());
        }

        /*
        DataResult<EventDto> event = eventService.getEventById(ticketRequest.getEventId());

        if (!event.isSuccess()) {
            throw new RuntimeException(event.getMessage());


         */

        var existingTicket = ticketDao.existsByEventIdAndUserId(ticketRequest.getEventId(), ticketRequest.getUserId());
        if (existingTicket) {
            throw new RuntimeException("Ticket already exists for user with id: " + ticketRequest.getUserId() +
                    " and event with id: " + ticketRequest.getEventId());
        }

        Ticket ticket = new Ticket();
        ticket.setEventId(ticketRequest.getEventId());
        ticket.setUserId(userResult.getData().getId());
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedAt(LocalDateTime.now());

        var savedTicket = ticketDao.save(ticket);
        return new TicketDto(savedTicket);
    }

    public List<TicketDto> getAllTickets() {
        List<Ticket> tickets = ticketDao.findAll();
        return tickets.stream()
                .map(TicketDto::new)
                .toList();
    }

    public TicketDto getTicketById(UUID id) {
        Ticket ticket = ticketDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        return new TicketDto(ticket);
    }

    public List<TicketDto> getCurrentUsersTickets(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);

        String userId;
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            userId = jwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user ID from token", e);
        }

        List<Ticket> tickets = ticketDao.findByUserId(UUID.fromString(userId));
        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found for user with id: " + userId);
        }

        return tickets.stream()
                .map(TicketDto::new)
                .toList();
    }

    public TicketDto updateTicketStatus(UUID id, String status) {

        Ticket ticket = ticketDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        TicketStatus ticketStatus;
        try {
            ticketStatus = TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ticket status: " + status);
        }

        if (ticketStatus == ticket.getStatus()) {
            throw new RuntimeException("Ticket status is already: " + ticketStatus);

        }

        ticket.setStatus(ticketStatus);
        var updatedTicket = ticketDao.save(ticket);
        return new TicketDto(updatedTicket);


    }

    public void deleteTicket(UUID id) {
        Ticket ticket = ticketDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        ticketDao.delete(ticket);
    }

    public List<TicketDto> getTicketsByEventId(UUID eventId) {
        List<Ticket> tickets = ticketDao.findByEventId(eventId);
        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found for event with id: " + eventId);
        }
        return tickets.stream()
                .map(TicketDto::new)
                .toList();
    }
}
