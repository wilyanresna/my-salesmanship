package domain

import "time"

// PullSalesmanResponse represents the salesman metadata in the pull payload.
type PullSalesmanResponse struct {
	ID         uint   `json:"id"`
	Name       string `json:"name"`
	DistrictID uint   `json:"district_id"`
	SpvName    string `json:"spv_name"`
}

// PullOutletResponse represents the outlet metadata along with its route mapping in the pull payload.
type PullOutletResponse struct {
	ID           uint     `json:"id"`
	Name         string   `json:"name"`
	OwnerName    string   `json:"owner_name"`
	Phone        string   `json:"phone"`
	Address      string   `json:"address"`
	Lat          *float64 `json:"lat"`
	Lng          *float64 `json:"lng"`
	Barcode      string   `json:"barcode"`
	OutletStatus string   `json:"outlet_status"`
	RouteID      uint     `json:"route_id"`
}

// PullStockResponse represents stock header and items in the pull payload.
type PullStockResponse struct {
	ID       uint             `json:"id"`
	DateUsed time.Time        `json:"date_used"`
	Status   string           `json:"status"`
	Items    []StockRokokItem `json:"items"`
}

// PullResponse defines the full payload returned to the Android client.
type PullResponse struct {
	Salesman   PullSalesmanResponse `json:"salesman"`
	Outlets    []PullOutletResponse `json:"outlets"`
	Products   []Product            `json:"products"`
	Params     []Param              `json:"params"`
	StockRokok *PullStockResponse   `json:"stock_rokok"`
	Targets    []SalesTarget        `json:"targets"`
}

// PullRepository defines the data access contract for pulling data.
type PullRepository interface {
	GetMappingSales(salesID uint, date time.Time) (*MappingSales, error)
	GetOutletsByDistrict(districtID uint, date time.Time) ([]PullOutletResponse, error)
	GetActiveProducts() ([]Product, error)
	GetActiveParams() ([]Param, error)
	GetStockRokok(salesID uint, date time.Time) (*StockRokok, []StockRokokItem, error)
	UpdateStockStatus(stockID uint, status string) error
	GetSalesTargets(salesID uint, date time.Time) ([]SalesTarget, error)
}

// PullUsecase defines the business logic contract for pulling data.
type PullUsecase interface {
	PullData(salesID uint, date time.Time) (*PullResponse, error)
}
