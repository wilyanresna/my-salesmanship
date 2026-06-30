package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	Port       string
	JWTSecret  string
	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string
	DBSSLMode  string
	DBTimeZone string
	SeedDB     bool
}

func LoadConfig() *Config {
	// Load .env file if it exists. In docker/production environments, variables might be passed directly.
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using system environment variables")
	}

	cfg := &Config{
		Port:       getEnv("PORT", "8080"),
		JWTSecret:  getEnv("JWT_SECRET", "supersecretjwtkey123!"),
		DBHost:     getEnv("DB_HOST", "localhost"),
		DBPort:     getEnv("DB_PORT", "5432"),
		DBUser:     getEnv("DB_USER", "postgres"),
		DBPassword: getEnv("DB_PASSWORD", "secretpassword"),
		DBName:     getEnv("DB_NAME", "salesmanship"),
		DBSSLMode:  getEnv("DB_SSLMODE", "disable"),
		DBTimeZone: getEnv("DB_TIMEZONE", "Asia/Jakarta"),
		SeedDB:     getEnv("SEED_DB", "false") == "true",
	}

	log.Printf("Configuration loaded successfully. DB Host: %s, DB Port: %s, DB Name: %s", cfg.DBHost, cfg.DBPort, cfg.DBName)
	return cfg
}

func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}
