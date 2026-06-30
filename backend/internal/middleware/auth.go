package middleware

import (
	"net/http"
	"strings"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/utils"

	"github.com/gin-gonic/gin"
)

// JWTAuth returns a middleware that validates JWT access tokens.
func JWTAuth(cfg *config.Config) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header is required"})
			c.Abort()
			return
		}

		parts := strings.SplitN(authHeader, " ", 2)
		if !(len(parts) == 2 && parts[0] == "Bearer") {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header must be Bearer token"})
			c.Abort()
			return
		}

		tokenStr := parts[1]
		claims, err := utils.ParseToken(tokenStr, cfg.JWTSecret)
		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid or expired token"})
			c.Abort()
			return
		}

		// Inject claims into Gin context
		c.Set("userID", claims.UserID)
		c.Set("username", claims.Username)
		c.Set("position", claims.Position)

		c.Next()
	}
}
