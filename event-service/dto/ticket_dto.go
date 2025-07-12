package dto

import (
	"time"

	"github.com/google/uuid"
)

type TicketStatus string

const (
	Created TicketStatus = "CREATED"
	Used    TicketStatus = "USED"
)

type TicketDto struct {
	ID        uuid.UUID    `json:"id"`
	UserId    uuid.UUID    `json:"userId"`
	EventId   uuid.UUID    `json:"eventId"`
	Status    TicketStatus `json:"status"`
	CreatedAt time.Time    `json:"createdAt"`
}
