package handler

import (
	"net/http"
	"strings"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// UploadHandler handles HTTP requests for uploading transaction visits.
type UploadHandler struct {
	usecase domain.UploadUsecase
}

// NewUploadHandler creates a new instance of UploadHandler.
func NewUploadHandler(u domain.UploadUsecase) *UploadHandler {
	return &UploadHandler{
		usecase: u,
	}
}

// UploadVisit handles the POST upload visit request.
func (h *UploadHandler) UploadVisit(c *gin.Context) {
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

	var payload domain.UploadPayload
	if err := c.ShouldBindJSON(&payload); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := h.usecase.UploadVisit(userID, payload.BatchID, &payload.Visit)
	if err != nil {
		if strings.Contains(err.Error(), "conflict") {
			c.JSON(http.StatusConflict, gin.H{"error": "Conflict: batch_id already exists"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "synced"})
}
