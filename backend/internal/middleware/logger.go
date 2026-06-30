package middleware

import (
	"log"
	"time"

	"github.com/gin-gonic/gin"
)

// Logger is a middleware to log HTTP request details.
func Logger() gin.HandlerFunc {
	return func(c *gin.Context) {
		startTime := time.Now()

		c.Next()

		latency := time.Since(startTime)
		statusCode := c.Writer.Status()
		clientIP := c.ClientIP()
		method := c.Request.Method
		path := c.Request.URL.Path

		log.Printf("[API] IP:%s - %s %s | Status:%d | Latency:%v",
			clientIP,
			method,
			path,
			statusCode,
			latency,
		)

		if len(c.Errors) > 0 {
			log.Printf("[API ERRORS] %v", c.Errors.String())
		}
	}
}
