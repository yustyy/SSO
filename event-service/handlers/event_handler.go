package handlers

import (
	"net/http"
	"time"

	"event-service/clients"
	"event-service/config"
	"event-service/dto"
	"event-service/models"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
)

var ticketClient = clients.NewTicketClient("http://ticket-service:8083")
var userClient = clients.NewUserClient("http://user-service:8082")

// CreateEvent yeni etkinlik oluşturur
func CreateEvent(c *gin.Context) {
	var req dto.EventRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	event := models.Event{
		ID:          uuid.New(),
		Title:       req.Title,
		Description: req.Description,
		Location:    req.Location,
		Date:        req.Date,
		TicketLimit: req.TicketLimit,
		TicketSold:  0,
		Status:      "ACTIVE",
		CreatedAt:   time.Now(),
	}

	if err := config.DB.Create(&event).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not create event"})
		return
	}

	c.JSON(http.StatusCreated, event)
}

// GetEventByID id ile etkinlik getirir
func GetEventByID(c *gin.Context) {
	id := c.Param("id")

	var event models.Event
	if err := config.DB.First(&event, "id = ?", id).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Event not found"})
		return
	}

	c.JSON(http.StatusOK, event)
}

// GetAllEvents tüm etkinlikleri listeler
func GetAllEvents(c *gin.Context) {
	var events []models.Event

	if err := config.DB.Find(&events).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch events"})
		return
	}

	c.JSON(http.StatusOK, events)
}

// UpdateEvent etkinlik günceller
func UpdateEvent(c *gin.Context) {
	id := c.Param("id")
	var req dto.EventRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var event models.Event
	if err := config.DB.First(&event, "id = ?", id).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Event not found"})
		return
	}

	event.Title = req.Title
	event.Description = req.Description
	event.Location = req.Location
	event.Date = req.Date
	event.TicketLimit = req.TicketLimit

	if err := config.DB.Save(&event).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update event"})
		return
	}

	c.JSON(http.StatusOK, event)
}

// DeleteEvent etkinlik siler
func DeleteEvent(c *gin.Context) {
	id := c.Param("id")

	if err := config.DB.Delete(&models.Event{}, "id = ?", id).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete event"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Event deleted successfully"})
}

func GetEventParticipants(c *gin.Context) {
	eventID := c.Param("id")

	tickets, err := ticketClient.GetTicketsByEventIdAndStatus(eventID, "USED")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch tickets", "details": err.Error()})
		return
	}

	userIDs := make([]string, 0, len(tickets))
	for _, t := range tickets {
		userIDs = append(userIDs, t.UserId)
	}

	users, err := userClient.GetUsersByIds(userIDs)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch users", "details": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success": true,
		"users":   users,
	})
}
