package routes

import (
	"event-service/handlers"

	"github.com/gin-gonic/gin"
)

func RegisterEventRoutes(router *gin.Engine) {
	eventGroup := router.Group("/api/events")
	{
		eventGroup.POST("/", handlers.CreateEvent)
		eventGroup.GET("/", handlers.GetAllEvents)
		eventGroup.GET("/:id", handlers.GetEventByID)
		eventGroup.PUT("/:id", handlers.UpdateEvent)
		eventGroup.DELETE("/:id", handlers.DeleteEvent)
		eventGroup.GET("/:id/participants", handlers.GetEventParticipants)
	}
}
