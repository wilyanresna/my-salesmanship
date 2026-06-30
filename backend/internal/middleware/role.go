package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

// RoleSpv restricts access only to employees with position 'SPV'.
func RoleSpv() gin.HandlerFunc {
	return func(c *gin.Context) {
		positionVal, exists := c.Get("position")
		if !exists {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
			c.Abort()
			return
		}

		position, ok := positionVal.(string)
		if !ok || position != "SPV" {
			c.JSON(http.StatusForbidden, gin.H{"error": "Forbidden: Supervisor role required"})
			c.Abort()
			return
		}

		c.Next()
	}
}

// RoleSales restricts access only to employees with position 'SALES'.
func RoleSales() gin.HandlerFunc {
	return func(c *gin.Context) {
		positionVal, exists := c.Get("position")
		if !exists {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
			c.Abort()
			return
		}

		position, ok := positionVal.(string)
		if !ok || position != "SALES" {
			c.JSON(http.StatusForbidden, gin.H{"error": "Forbidden: Salesman role required"})
			c.Abort()
			return
		}

		c.Next()
	}
}
