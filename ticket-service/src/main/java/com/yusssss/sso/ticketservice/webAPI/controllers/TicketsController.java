package com.yusssss.sso.ticketservice.webAPI.controllers;

import com.yusssss.sso.ticketservice.business.TicketService;
import com.yusssss.sso.ticketservice.core.results.DataResult;
import com.yusssss.sso.ticketservice.core.results.SuccessDataResult;
import com.yusssss.sso.ticketservice.dtos.ticket.TicketDto;
import com.yusssss.sso.ticketservice.dtos.ticket.TicketRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketsController {


    private final TicketService ticketService;


    public TicketsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping("/")
    public ResponseEntity<DataResult<TicketDto>> createTicket(@RequestBody @Valid  TicketRequest ticketRequest,
                                                              HttpServletRequest request){
        TicketDto result = ticketService.createTicket(ticketRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessDataResult<>(result,
                "Ticket created successfully", HttpStatus.CREATED, request.getRequestURI()));
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<TicketDto>>> getAllTickets(HttpServletRequest request) {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(new SuccessDataResult<>(tickets, "Tickets retrieved successfully", HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<TicketDto>> getTicketById(@PathVariable UUID id, HttpServletRequest request) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(new SuccessDataResult<>(ticket, "Ticket retrieved successfully", HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<List<TicketDto>>> getCurrentUsersTickets(HttpServletRequest request) {
        List<TicketDto> tickets = ticketService.getCurrentUsersTickets(request);
        return ResponseEntity.ok(new SuccessDataResult<>(tickets, "User tickets retrieved successfully", HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<DataResult<List<TicketDto>>> getTicketsByEventId(@PathVariable UUID eventId, HttpServletRequest request) {
        List<TicketDto> tickets = ticketService.getTicketsByEventId(eventId);
        return ResponseEntity.ok(new SuccessDataResult<>(tickets, "Tickets for event retrieved successfully", HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DataResult<TicketDto>> updateTicketStatus(@PathVariable UUID id,
                                                                     @RequestParam("status") String status,
                                                                     HttpServletRequest request) {
        TicketDto updatedTicket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(new SuccessDataResult<>(updatedTicket, "Ticket status updated successfully", HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResult<Void>> deleteTicket(@PathVariable UUID id, HttpServletRequest request) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(new SuccessDataResult<>(null, "Ticket deleted successfully", HttpStatus.OK, request.getRequestURI()));
    }




}
