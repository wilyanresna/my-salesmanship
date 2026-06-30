package main

import (
	"fmt"
	"log"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/database"
	"my-salesmanship/internal/router"
)

func main() {
	log.Println("Starting Salesmanship API server...")

	// Load configuration
	cfg := config.LoadConfig()

	// Connect to database
	db, err := database.ConnectDB(cfg)
	if err != nil {
		log.Fatalf("Fatal error connecting to database: %v", err)
	}

	sqlDB, err := db.DB()
	if err != nil {
		log.Printf("Warning: could not get sql.DB: %v", err)
	} else {
		log.Printf("Database connection state: OpenConnections=%d", sqlDB.Stats().OpenConnections)
	}

	// Run migrations
	if err := database.MigrateDB(db); err != nil {
		log.Fatalf("Fatal error running migrations: %v", err)
	}

	// Seed database if configured
	if cfg.SeedDB {
		if err := database.SeedDB(db); err != nil {
			log.Fatalf("Fatal error seeding database: %v", err)
		}
	}

	// Set up Gin Router
	r := router.SetupRouter(cfg, db)

	serverAddr := fmt.Sprintf(":%s", cfg.Port)
	log.Printf("Server is starting on %s", serverAddr)
	if err := r.Run(serverAddr); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}

