package handler

import (
	"net/http"
	"strconv"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// StockTargetHandler handles HTTP requests for stock allocation and sales target.
type StockTargetHandler struct {
	stockUsecase  domain.StockUsecase
	targetUsecase domain.TargetUsecase
}

// NewStockTargetHandler creates a new instance of StockTargetHandler.
func NewStockTargetHandler(su domain.StockUsecase, tu domain.TargetUsecase) *StockTargetHandler {
	return &StockTargetHandler{
		stockUsecase:  su,
		targetUsecase: tu,
	}
}

// ---- Stock Endpoints ----

type stockItemReq struct {
	ProductID  uint  `json:"product_id" binding:"required"`
	QtyDusInit int16 `json:"qty_dus_init"`
	QtyBalInit int16 `json:"qty_bal_init"`
	QtySlfInit int16 `json:"qty_slf_init"`
	QtyBksInit int16 `json:"qty_bks_init"`
}

type createStockReq struct {
	SalesID  uint           `json:"sales_id" binding:"required"`
	DateUsed string         `json:"date_used" binding:"required"`
	Items    []stockItemReq `json:"items" binding:"required,gt=0"`
}

func (h *StockTargetHandler) CreateStock(c *gin.Context) {
	creatorIDVal, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		return
	}
	creatorID, ok := creatorIDVal.(uint)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Invalid user context"})
		return
	}

	var req createStockReq
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Map request items to domain items
	domainItems := make([]domain.StockRokokItem, len(req.Items))
	for i, item := range req.Items {
		domainItems[i] = domain.StockRokokItem{
			ProductID:  item.ProductID,
			QtyDusInit: item.QtyDusInit,
			QtyBalInit: item.QtyBalInit,
			QtySlfInit: item.QtySlfInit,
			QtyBksInit: item.QtyBksInit,
		}
	}

	res, err := h.stockUsecase.CreateStock(req.SalesID, req.DateUsed, creatorID, domainItems)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, res)
}

func (h *StockTargetHandler) ListStocks(c *gin.Context) {
	userIDVal, exists := c.Get("userID")
	positionVal, posExists := c.Get("position")
	if !exists || !posExists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		return
	}

	userID := userIDVal.(uint)
	position := positionVal.(string)

	res, err := h.stockUsecase.ListStocks(userID, position)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

func (h *StockTargetHandler) GetStockDetail(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return
	}

	stock, items, err := h.stockUsecase.GetStockDetail(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Stock not found"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"stock": stock,
		"items": items,
	})
}

func (h *StockTargetHandler) UpdateStockStatus(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return
	}

	var req struct {
		Status string `json:"status" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err = h.stockUsecase.UpdateStockStatus(uint(id), req.Status)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Stock status updated successfully"})
}

// ---- Target Endpoints ----

type createTargetReq struct {
	SalesID   uint   `json:"sales_id" binding:"required"`
	RouteID   uint   `json:"route_id" binding:"required"`
	ProductID uint   `json:"product_id" binding:"required"`
	TargetQty int32  `json:"target_qty" binding:"required"`
	WeekStart string `json:"week_start" binding:"required"`
	WeekEnd   string `json:"week_end" binding:"required"`
}

func (h *StockTargetHandler) CreateTarget(c *gin.Context) {
	creatorIDVal, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		return
	}
	creatorID := creatorIDVal.(uint)

	var req createTargetReq
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	res, err := h.targetUsecase.CreateTarget(req.SalesID, req.RouteID, req.ProductID, req.TargetQty, req.WeekStart, req.WeekEnd, creatorID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, res)
}

func (h *StockTargetHandler) GetTarget(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return
	}

	res, err := h.targetUsecase.GetTarget(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Target not found"})
		return
	}

	c.JSON(http.StatusOK, res)
}

func (h *StockTargetHandler) ListTargets(c *gin.Context) {
	res, err := h.targetUsecase.ListTargets()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

func (h *StockTargetHandler) UpdateTarget(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return
	}

	var req createTargetReq
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	res, err := h.targetUsecase.UpdateTarget(uint(id), req.SalesID, req.RouteID, req.ProductID, req.TargetQty, req.WeekStart, req.WeekEnd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, res)
}

func (h *StockTargetHandler) DeleteTarget(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return
	}

	if err := h.targetUsecase.DeleteTarget(uint(id)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Target deleted successfully"})
}
