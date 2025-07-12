package models

import (
	"time"

	"github.com/google/uuid"
)

type Event struct {
	ID          uuid.UUID `gorm:"type:uuid;primaryKey"`
	Title       string
	Description string
	Location    string
	Date        time.Time
	TicketLimit int
	TicketSold  int
	Status      string
	CreatedAt   time.Time
}
