package handler

import (
	"net/http"
	"strconv"

	"my-salesmanship/internal/domain"

	"github.com/gin-gonic/gin"
)

// MasterHandler handles HTTP requests for master data.
type MasterHandler struct {
	usecase domain.MasterUsecase
}

// NewMasterHandler creates a new instance of MasterHandler.
func NewMasterHandler(u domain.MasterUsecase) *MasterHandler {
	return &MasterHandler{
		usecase: u,
	}
}

func parseID(c *gin.Context) (uint, bool) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid ID parameter"})
		return 0, false
	}
	return uint(id), true
}

// Area
func (h *MasterHandler) CreateArea(c *gin.Context) {
	var req struct {
		Name string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateArea(req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetArea(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetArea(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Area not found"})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListAreas(c *gin.Context) {
	res, err := h.usecase.ListAreas()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateArea(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req struct {
		Name string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateArea(id, req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteArea(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteArea(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Area deleted successfully"})
}

// Territory
func (h *MasterHandler) CreateTerritory(c *gin.Context) {
	var req struct {
		AreaID uint   `json:"area_id" binding:"required"`
		Name   string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateTerritory(req.AreaID, req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetTerritory(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetTerritory(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Territory not found"})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListTerritories(c *gin.Context) {
	res, err := h.usecase.ListTerritories()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateTerritory(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req struct {
		AreaID uint   `json:"area_id" binding:"required"`
		Name   string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateTerritory(id, req.AreaID, req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteTerritory(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteTerritory(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Territory deleted successfully"})
}

// District
func (h *MasterHandler) CreateDistrict(c *gin.Context) {
	var req struct {
		TerritoryID uint   `json:"territory_id" binding:"required"`
		Name        string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateDistrict(req.TerritoryID, req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetDistrict(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetDistrict(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "District not found"})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListDistricts(c *gin.Context) {
	res, err := h.usecase.ListDistricts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateDistrict(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req struct {
		TerritoryID uint   `json:"territory_id" binding:"required"`
		Name        string `json:"name" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateDistrict(id, req.TerritoryID, req.Name)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteDistrict(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteDistrict(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "District deleted successfully"})
}

// Route
func (h *MasterHandler) CreateRoute(c *gin.Context) {
	var req struct {
		DistrictID uint   `json:"district_id" binding:"required"`
		Name       string `json:"name" binding:"required"`
		DayOfWeek  int16  `json:"day_of_week" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateRoute(req.DistrictID, req.Name, req.DayOfWeek)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetRoute(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetRoute(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Route not found"})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListRoutes(c *gin.Context) {
	res, err := h.usecase.ListRoutes()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateRoute(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req struct {
		DistrictID uint   `json:"district_id" binding:"required"`
		Name       string `json:"name" binding:"required"`
		DayOfWeek  int16  `json:"day_of_week" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateRoute(id, req.DistrictID, req.Name, req.DayOfWeek)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteRoute(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteRoute(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Route deleted successfully"})
}

// Outlet
func (h *MasterHandler) CreateOutlet(c *gin.Context) {
	var req domain.Outlet
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateOutlet(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetOutlet(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetOutlet(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Outlet not found"})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListOutlets(c *gin.Context) {
	res, err := h.usecase.ListOutlets()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateOutlet(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req domain.Outlet
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateOutlet(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteOutlet(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteOutlet(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Outlet deleted successfully"})
}

// Employee
func (h *MasterHandler) CreateEmployee(c *gin.Context) {
	var req domain.Employee
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.CreateEmployee(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	// Hide password hash in API response for security
	res.Password = ""
	c.JSON(http.StatusCreated, res)
}

func (h *MasterHandler) GetEmployee(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	res, err := h.usecase.GetEmployee(id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Employee not found"})
		return
	}
	res.Password = ""
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) ListEmployees(c *gin.Context) {
	res, err := h.usecase.ListEmployees()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	for i := range res {
		res[i].Password = ""
	}
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) UpdateEmployee(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	var req domain.Employee
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	res, err := h.usecase.UpdateEmployee(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	res.Password = ""
	c.JSON(http.StatusOK, res)
}

func (h *MasterHandler) DeleteEmployee(c *gin.Context) {
	id, ok := parseID(c)
	if !ok {
		return
	}
	if err := h.usecase.DeleteEmployee(id); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Employee deleted successfully"})
}

// Product (Read-only)
func (h *MasterHandler) ListProducts(c *gin.Context) {
	res, err := h.usecase.ListProducts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}

// Param (Read-only)
func (h *MasterHandler) ListParams(c *gin.Context) {
	res, err := h.usecase.ListParams()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, res)
}
