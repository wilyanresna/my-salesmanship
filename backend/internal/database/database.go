package database

import (
	"fmt"
	"log"
	"time"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/domain"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

func ConnectDB(cfg *config.Config) (*gorm.DB, error) {
	dsn := fmt.Sprintf(
		"host=%s user=%s password=%s dbname=%s port=%s sslmode=%s TimeZone=%s",
		cfg.DBHost,
		cfg.DBUser,
		cfg.DBPassword,
		cfg.DBName,
		cfg.DBPort,
		cfg.DBSSLMode,
		cfg.DBTimeZone,
	)

	var db *gorm.DB
	var err error

	// Retry database connection 5 times with delay
	for i := 1; i <= 5; i++ {
		log.Printf("Connecting to database (attempt %d/5)...", i)
		db, err = gorm.Open(postgres.Open(dsn), &gorm.Config{
			Logger: logger.Default.LogMode(logger.Info),
		})
		if err == nil {
			break
		}
		log.Printf("Failed to connect to database: %v. Retrying in 3 seconds...", err)
		time.Sleep(3 * time.Second)
	}

	if err != nil {
		return nil, fmt.Errorf("could not connect to database: %w", err)
	}

	sqlDB, err := db.DB()
	if err != nil {
		return nil, fmt.Errorf("could not get standard sql.DB instance: %w", err)
	}

	if err := sqlDB.Ping(); err != nil {
		return nil, fmt.Errorf("database ping failed: %w", err)
	}

	// Set generic database pool settings
	sqlDB.SetMaxIdleConns(10)
	sqlDB.SetMaxOpenConns(100)
	sqlDB.SetConnMaxLifetime(time.Hour)

	log.Println("Database connection established successfully!")
	return db, nil
}

func MigrateDB(db *gorm.DB) error {
	log.Println("Running database migrations...")
	return db.AutoMigrate(
		&domain.Area{},
		&domain.Territory{},
		&domain.District{},
		&domain.Route{},
		&domain.Outlet{},
		&domain.Product{},
		&domain.Param{},
		&domain.Employee{},
		&domain.MappingSpv{},
		&domain.MappingSales{},
		&domain.MappingOutlet{},
		&domain.StockRokok{},
		&domain.StockRokokItem{},
		&domain.SalesTarget{},
		&domain.TrOutlet{},
		&domain.TrCheckStock{},
		&domain.TrSales{},
		&domain.TrSalesDetail{},
	)
}

