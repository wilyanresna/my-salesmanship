package utils

import (
	"errors"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

// JWTClaims defines the structure for JWT payload claims.
type JWTClaims struct {
	UserID   uint   `json:"user_id"`
	Username string `json:"username"`
	Position string `json:"position"`
	jwt.RegisteredClaims
}

// GenerateAccessToken generates a short-lived access token (15 minutes).
func GenerateAccessToken(userID uint, username, position string, secret string) (string, error) {
	claims := JWTClaims{
		UserID:   userID,
		Username: username,
		Position: position,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(15 * time.Minute)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(secret))
}

// GenerateRefreshToken generates a long-lived refresh token (7 days).
func GenerateRefreshToken(userID uint, username, position string, secret string) (string, error) {
	claims := JWTClaims{
		UserID:   userID,
		Username: username,
		Position: position,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(7 * 24 * time.Hour)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(secret))
}

// ParseToken parses and validates a JWT token using the signing key.
func ParseToken(tokenStr string, secret string) (*JWTClaims, error) {
	token, err := jwt.ParseWithClaims(tokenStr, &JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, errors.New("unexpected signing method")
		}
		return []byte(secret), nil
	})

	if err != nil {
		return nil, err
	}

	claims, ok := token.Claims.(*JWTClaims)
	if !ok || !token.Valid {
		return nil, errors.New("invalid token claims")
	}

	return claims, nil
}
