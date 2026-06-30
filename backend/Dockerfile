# Build stage
FROM golang:1.25-alpine AS builder

WORKDIR /app

# Copy go.mod and go.sum files
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download

# Copy the rest of the application code
COPY . .

# Build the Go app
RUN CGO_ENABLED=0 GOOS=linux go build -o main ./cmd/main.go

# Final stage
FROM alpine:latest

RUN apk --no-cache add tzdata

WORKDIR /app

# Copy binary from builder
COPY --from=builder /app/main .
# Copy .env.example as placeholder or fallback
COPY --from=builder /app/.env.example .env

# Expose port
EXPOSE 8080

# Command to run
CMD ["./main"]
