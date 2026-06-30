package handler

import (
	"net/http"
	"strconv"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// ReportHandler handles HTTP requests for Supervisor reports.
type ReportHandler struct {
	usecase domain.ReportUsecase
}

// NewReportHandler creates a new instance of ReportHandler.
func NewReportHandler(u domain.ReportUsecase) *ReportHandler {
	return &ReportHandler{
		usecase: u,
	}
}

// GetVisitsReport handles GET /report/visits.
func (h *ReportHandler) GetVisitsReport(c *gin.Context) {
	salesIDStr := c.Query("sales_id")
	var salesID uint
	if salesIDStr != "" {
		id, _ := strconv.Atoi(salesIDStr)
		salesID = uint(id)
	}

	dateStr := c.Query("date")

	res, err := h.usecase.GetVisitsReport(salesID, dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

// GetSalesReport handles GET /report/sales.
func (h *ReportHandler) GetSalesReport(c *gin.Context) {
	salesIDStr := c.Query("sales_id")
	var salesID uint
	if salesIDStr != "" {
		id, _ := strconv.Atoi(salesIDStr)
		salesID = uint(id)
	}

	startDateStr := c.Query("start_date")
	endDateStr := c.Query("end_date")

	res, err := h.usecase.GetSalesReport(salesID, startDateStr, endDateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

// GetStockReport handles GET /report/stock.
func (h *ReportHandler) GetStockReport(c *gin.Context) {
	salesIDStr := c.Query("sales_id")
	var salesID uint
	if salesIDStr != "" {
		id, _ := strconv.Atoi(salesIDStr)
		salesID = uint(id)
	}

	dateStr := c.Query("date")

	res, err := h.usecase.GetStockReport(salesID, dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

// GetAchievementReport handles GET /report/achievement.
func (h *ReportHandler) GetAchievementReport(c *gin.Context) {
	salesIDStr := c.Query("sales_id")
	var salesID uint
	if salesIDStr != "" {
		id, _ := strconv.Atoi(salesIDStr)
		salesID = uint(id)
	}

	dateStr := c.Query("date")

	res, err := h.usecase.GetAchievementReport(salesID, dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}
