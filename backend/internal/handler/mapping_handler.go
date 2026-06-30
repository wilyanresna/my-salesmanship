package handler

import (
	"net/http"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// MappingHandler handles HTTP requests for weekly mapping.
type MappingHandler struct {
	usecase domain.MappingUsecase
}

// NewMappingHandler creates a new instance of MappingHandler.
func NewMappingHandler(u domain.MappingUsecase) *MappingHandler {
	return &MappingHandler{
		usecase: u,
	}
}

// CreateMappingSpv handles creating SPV mapping.
func (h *MappingHandler) CreateMappingSpv(c *gin.Context) {
	var req struct {
		SpvID       uint   `json:"spv_id" binding:"required"`
		TerritoryID uint   `json:"territory_id" binding:"required"`
		WeekStart   string `json:"week_start" binding:"required"`
		WeekEnd     string `json:"week_end" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateMappingSpv(req.SpvID, req.TerritoryID, req.WeekStart, req.WeekEnd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

// ListMappingSpv handles retrieving all SPV mappings.
func (h *MappingHandler) ListMappingSpv(c *gin.Context) {
	res, err := h.usecase.ListMappingSpv()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

// CreateMappingSales handles creating Sales mapping.
func (h *MappingHandler) CreateMappingSales(c *gin.Context) {
	var req struct {
		SpvID      uint   `json:"spv_id" binding:"required"`
		SalesID    uint   `json:"sales_id" binding:"required"`
		DistrictID uint   `json:"district_id" binding:"required"`
		WeekStart  string `json:"week_start" binding:"required"`
		WeekEnd    string `json:"week_end" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateMappingSales(req.SpvID, req.SalesID, req.DistrictID, req.WeekStart, req.WeekEnd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

// ListMappingSales handles retrieving all Sales mappings.
func (h *MappingHandler) ListMappingSales(c *gin.Context) {
	res, err := h.usecase.ListMappingSales()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

// CreateMappingOutlet handles creating Outlet mapping.
func (h *MappingHandler) CreateMappingOutlet(c *gin.Context) {
	var req struct {
		OutletID  uint   `json:"outlet_id" binding:"required"`
		RouteID   uint   `json:"route_id" binding:"required"`
		WeekStart string `json:"week_start" binding:"required"`
		WeekEnd   string `json:"week_end" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateMappingOutlet(req.OutletID, req.RouteID, req.WeekStart, req.WeekEnd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

// ListMappingOutlet handles retrieving all Outlet mappings.
func (h *MappingHandler) ListMappingOutlet(c *gin.Context) {
	res, err := h.usecase.ListMappingOutlet()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}
