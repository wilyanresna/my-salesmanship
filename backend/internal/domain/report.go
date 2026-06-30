package domain

import "time"

// VisitReportItem defines a visit record for Supervisor reports.
type VisitReportItem struct {
	ID           uint       `json:"id"`
	BatchID      string     `json:"batch_id"`
	SalesID      uint       `json:"sales_id"`
	SalesName    string     `json:"sales_name"`
	OutletID     uint       `json:"outlet_id"`
	OutletName   string     `json:"outlet_name"`
	VisitNo      int16      `json:"visit_no"`
	VisitType    string     `json:"visit_type"`
	Status       string     `json:"status"`
	StartTime    time.Time  `json:"start_time"`
	EndTime      *time.Time `json:"end_time"`
	Lat          *float64   `json:"lat"`
	Lng          *float64   `json:"lng"`
	UploadedAt   time.Time  `json:"uploaded_at"`
}

// SalesReportItem defines a aggregated sales item for Supervisor reports.
type SalesReportItem struct {
	ProductID   uint    `json:"product_id"`
	ProductName string  `json:"product_name"`
	ProductSKU  string  `json:"product_sku"`
	TotalQty    int64   `json:"total_qty"`
	TotalAmount float64 `json:"total_amount"`
}

// StockReportItem defines the stock balance details of a salesman.
type StockReportItem struct {
	SalesID        uint   `json:"sales_id"`
	SalesName      string `json:"sales_name"`
	ProductID      uint   `json:"product_id"`
	ProductName    string `json:"product_name"`
	ProductSKU     string `json:"product_sku"`
	InitialBungkus int64  `json:"initial_bungkus"`
	SoldBungkus    int64  `json:"sold_bungkus"`
	CurrentBungkus int64  `json:"current_bungkus"`
}

// AchievementReportItem defines the achievement percentage vs target for weekly plans.
type AchievementReportItem struct {
	SalesID     uint    `json:"sales_id"`
	SalesName   string  `json:"sales_name"`
	RouteID     uint    `json:"route_id"`
	RouteName   string  `json:"route_name"`
	ProductID   uint    `json:"product_id"`
	ProductName string  `json:"product_name"`
	ProductSKU  string  `json:"product_sku"`
	TargetQty   int64   `json:"target_qty"`
	ActualQty   int64   `json:"actual_qty"`
	Percentage  float64 `json:"percentage"`
}

// ReportRepository defines the database access layer for supervisor reports.
type ReportRepository interface {
	GetVisitsReport(salesID uint, date time.Time) ([]VisitReportItem, error)
	GetSalesReport(salesID uint, startDate, endDate time.Time) ([]SalesReportItem, error)
	GetStockReport(salesID uint, date time.Time) ([]StockReportItem, error)
	GetAchievementReport(salesID uint, date time.Time) ([]AchievementReportItem, error)
}

// ReportUsecase defines the business logic layer for supervisor reports.
type ReportUsecase interface {
	GetVisitsReport(salesID uint, dateStr string) ([]VisitReportItem, error)
	GetSalesReport(salesID uint, startDateStr, endDateStr string) ([]SalesReportItem, error)
	GetStockReport(salesID uint, dateStr string) ([]StockReportItem, error)
	GetAchievementReport(salesID uint, dateStr string) ([]AchievementReportItem, error)
}
