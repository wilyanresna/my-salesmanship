package handler

import (
	"net/http"
	"time"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// PullHandler handles HTTP requests for pulling synchronization data.
type PullHandler struct {
	usecase domain.PullUsecase
}

// NewPullHandler creates a new instance of PullHandler.
func NewPullHandler(u domain.PullUsecase) *PullHandler {
	return &PullHandler{
		usecase: u,
	}
}

// PullData handles the pull request for active week data.
func (h *PullHandler) PullData(c *gin.Context) {
	userIDVal, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		return
	}
	userID, ok := userIDVal.(uint)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Invalid user context"})
		return
	}

	// Pull data based on today's date
	date := time.Now()

	res, err := h.usecase.PullData(userID, date)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}
