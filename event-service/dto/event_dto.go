package dto

import "time"

type EventRequest struct {
	Title       string    `json:"title" binding:"required"`
	Description string    `json:"description"`
	Location    string    `json:"location"`
	Date        time.Time `json:"date" binding:"required"`
	TicketLimit int       `json:"ticket_limit" binding:"required"`
}
