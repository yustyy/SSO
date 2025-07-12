package main

import (
	"event-service/config"
	"event-service/models"
	"event-service/routes"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/hudl/fargo"
)

func main() {
	// Database bağlantısı ve migration
	config.ConnectToDatabase()
	if err := config.DB.AutoMigrate(&models.Event{}); err != nil {
		log.Fatalf("DB migration failed: %v", err)
	}

	// Gin router setup
	r := gin.Default()

	// Health check endpoint (Eureka için gerekli)
	r.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status":    "UP",
			"service":   "event-service",
			"timestamp": time.Now().Unix(),
		})
	})

	// Info endpoint (Eureka için opsiyonel)
	r.GET("/info", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"name":        "event-service",
			"version":     "1.0.0",
			"description": "Event Management Service",
			"port":        8084,
		})
	})

	// Mevcut routes'ları kaydet
	routes.RegisterEventRoutes(r)

	// Eureka setup
	eurekaURL := getEnvOrDefault("EUREKA_URL", "http://eureka-server:8761/eureka/v2")
	servicePort := getEnvOrDefault("SERVICE_PORT", "8084")
	hostName := getContainerHostname()

	conn := fargo.NewConn(eurekaURL)

	instance := &fargo.Instance{
		InstanceId:       fmt.Sprintf("%s:event-service:%s", hostName, servicePort),
		HostName:         hostName,
		App:              "EVENT-SERVICE",
		IPAddr:           getContainerIP(),
		VipAddress:       "event-service",
		SecureVipAddress: "event-service",
		Status:           fargo.UP,
		Port:             parseInt(servicePort),
		PortEnabled:      true,
		HealthCheckUrl:   fmt.Sprintf("http://%s:%s/health", hostName, servicePort),
		StatusPageUrl:    fmt.Sprintf("http://%s:%s/info", hostName, servicePort),
		HomePageUrl:      fmt.Sprintf("http://%s:%s/", hostName, servicePort),
		DataCenterInfo:   fargo.DataCenterInfo{Name: fargo.MyOwn},
		LeaseInfo: fargo.LeaseInfo{
			RenewalIntervalInSecs: 30,
			DurationInSecs:        90,
		},
	}

	// Eureka'ya register et (retry mekanizması ile)
	registered := false
	for i := 0; i < 5; i++ {
		err := conn.RegisterInstance(instance)
		if err != nil {
			log.Printf("Eureka registration attempt %d failed: %v", i+1, err)
			time.Sleep(time.Duration(i+1) * 5 * time.Second)
		} else {
			log.Println("Event service successfully registered to Eureka!")
			registered = true
			break
		}
	}

	if !registered {
		log.Println("Failed to register to Eureka after 5 attempts, continuing without registration...")
	}

	// Heartbeat gönder (arka planda)
	go func() {
		// İlk başta biraz bekle
		time.Sleep(10 * time.Second)

		ticker := time.NewTicker(30 * time.Second)
		defer ticker.Stop()

		for {
			select {
			case <-ticker.C:
				err := conn.HeartBeatInstance(instance)
				if err != nil {
					log.Printf("Heartbeat error: %v", err)
					// Eğer heartbeat başarısız olursa, yeniden register olmayı dene
					if err.Error() == "heartbeat failed, rcode = 404" {
						log.Println("Instance not found, attempting to re-register...")
						regErr := conn.RegisterInstance(instance)
						if regErr != nil {
							log.Printf("Re-registration failed: %v", regErr)
						} else {
							log.Println("Re-registration successful!")
						}
					}
				}
			}
		}
	}()

	// Graceful shutdown için signal handling
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-c
		log.Println("Shutting down event service...")

		// Eureka'dan deregister et
		err := conn.DeregisterInstance(instance)
		if err != nil {
			log.Printf("Eureka deregistration error: %v", err)
		} else {
			log.Println("Event service successfully deregistered from Eureka")
		}

		os.Exit(0)
	}()

	// HTTP server'ı başlat
	log.Printf("Event service starting on port %s...", servicePort)
	if err := r.Run(":" + servicePort); err != nil {
		log.Fatalf("Failed to run Gin server: %v", err)
	}
}

// Helper functions
func getEnvOrDefault(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

func parseInt(s string) int {
	switch s {
	case "8084":
		return 8084
	case "8080":
		return 8080
	case "8081":
		return 8081
	default:
		return 8084
	}
}

func getContainerHostname() string {
	// Docker container'ın gerçek hostname'ini al
	hostname, err := os.Hostname()
	if err != nil {
		log.Printf("Failed to get hostname: %v", err)
		return "event-service-unknown"
	}
	return hostname
}

func getContainerIP() string {
	// Docker container içinde kendi IP'sini al
	return "event-service" // Docker compose'da service name kullan
}
