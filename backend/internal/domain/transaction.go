package domain

import (
	"time"
)

type TrOutlet struct {
	ID         uint       `gorm:"primaryKey;column:id"`
	BatchID    string     `gorm:"type:uuid;not null;unique;index:idx_tr_outlet_batch;column:batch_id"`
	SalesID    uint       `gorm:"not null;column:sales_id;index:idx_tr_outlet_sales"`
	Sales      Employee   `gorm:"foreignKey:SalesID;constraint:OnDelete:RESTRICT"`
	OutletID   uint       `gorm:"not null;column:outlet_id;index:idx_tr_outlet_outlet"`
	Outlet     Outlet     `gorm:"foreignKey:OutletID;constraint:OnDelete:RESTRICT"`
	VisitNo    int16      `gorm:"not null;default:1;column:visit_no"`
	VisitType  string     `gorm:"type:varchar(20);not null;default:'NORMAL';column:visit_type;check:visit_type IN ('NORMAL', 'OUT_OF_ROUTE')"`
	Status     string     `gorm:"type:varchar(10);not null;default:'CLOSED';column:status"`
	StartTime  time.Time  `gorm:"column:start_time;index:idx_tr_outlet_start_time"`
	EndTime    *time.Time `gorm:"column:end_time"`
	Lat        *float64   `gorm:"type:numeric(10,7);column:lat"`
	Lng        *float64   `gorm:"type:numeric(10,7);column:lng"`
	UploadedAt time.Time  `gorm:"column:uploaded_at;default:now()"`
}

func (TrOutlet) TableName() string {
	return "tr_outlet"
}

type TrCheckStock struct {
	ID         uint      `gorm:"primaryKey;column:id"`
	BatchID    string    `gorm:"type:uuid;not null;column:batch_id"`
	TrOutletID uint      `gorm:"not null;column:tr_outlet_id;uniqueIndex:idx_tr_check_stock_unique;index:idx_tr_check_outlet"`
	TrOutlet   TrOutlet  `gorm:"foreignKey:TrOutletID;constraint:OnDelete:CASCADE"`
	ProductID  uint      `gorm:"not null;column:product_id;uniqueIndex:idx_tr_check_stock_unique;index:idx_tr_check_product"`
	Product    Product   `gorm:"foreignKey:ProductID;constraint:OnDelete:RESTRICT"`
	StockQty   int32     `gorm:"not null;default:0;column:stock_qty"`
	UploadedAt time.Time `gorm:"column:uploaded_at;default:now()"`
}

func (TrCheckStock) TableName() string {
	return "tr_check_stock"
}

type TrSales struct {
	ID         uint      `gorm:"primaryKey;column:id"`
	BatchID    string    `gorm:"type:uuid;not null;uniqueIndex:idx_tr_sales_unique"`
	TrOutletID uint      `gorm:"not null;column:tr_outlet_id;index:idx_tr_sales_outlet"`
	TrOutlet   TrOutlet  `gorm:"foreignKey:TrOutletID;constraint:OnDelete:CASCADE"`
	SalesOrder string    `gorm:"type:varchar(50);not null;uniqueIndex:idx_tr_sales_unique;column:sales_order"`
	UploadedAt time.Time `gorm:"column:uploaded_at;default:now()"`
}

func (TrSales) TableName() string {
	return "tr_sales"
}

type TrSalesDetail struct {
	ID         uint      `gorm:"primaryKey;column:id"`
	BatchID    string    `gorm:"type:uuid;not null;column:batch_id"`
	TrSalesID  uint      `gorm:"not null;column:tr_sales_id;index:idx_tr_sales_detail_sales"`
	TrSales    TrSales   `gorm:"foreignKey:TrSalesID;constraint:OnDelete:CASCADE"`
	ProductID  uint      `gorm:"not null;column:product_id;index:idx_tr_sales_detail_product"`
	Product    Product   `gorm:"foreignKey:ProductID;constraint:OnDelete:RESTRICT"`
	Qty        int32     `gorm:"not null;column:qty"`
	Price      float64   `gorm:"type:numeric(12,2);not null;column:price"`
	Total      float64   `gorm:"type:numeric(14,2);not null;column:total"`
	UploadedAt time.Time `gorm:"column:uploaded_at;default:now()"`
}

func (TrSalesDetail) TableName() string {
	return "tr_sales_detail"
}

// Request and Payload structures for visit upload
type UploadCheckStockRequest struct {
	ProductID uint  `json:"product_id" binding:"required"`
	StockQty  int32 `json:"stock_qty" binding:"required"`
}

type UploadSalesDetailRequest struct {
	ProductID uint    `json:"product_id" binding:"required"`
	Qty       int32   `json:"qty" binding:"required"`
	Price     float64 `json:"price" binding:"required"`
	Total     float64 `json:"total" binding:"required"`
}

type UploadSalesRequest struct {
	SalesOrder string                     `json:"sales_order" binding:"required"`
	Details    []UploadSalesDetailRequest `json:"details" binding:"required,gt=0"`
}

type UploadVisitRequest struct {
	OutletID    uint                      `json:"outlet_id" binding:"required"`
	VisitNo     int16                     `json:"visit_no"`
	VisitType   string                    `json:"visit_type" binding:"required"`
	Status      string                    `json:"status" binding:"required"`
	StartTime   time.Time                 `json:"start_time" binding:"required"`
	EndTime     *time.Time                `json:"end_time"`
	Lat         *float64                  `json:"lat"`
	Lng         *float64                  `json:"lng"`
	CheckStocks []UploadCheckStockRequest `json:"check_stocks"`
	Sales       []UploadSalesRequest      `json:"sales"`
}

type UploadPayload struct {
	BatchID string             `json:"batch_id" binding:"required"`
	Visit   UploadVisitRequest `json:"visit" binding:"required"`
}

// TransactionRepository defines the data access contract for visit uploads.
type TransactionRepository interface {
	HasBatchID(batchID string) (bool, error)
	CreateVisitTransaction(outlet *TrOutlet, checkStocks []TrCheckStock, sales []TrSales, salesDetails [][]TrSalesDetail) error
}

// UploadUsecase defines the business logic contract for visit uploads.
type UploadUsecase interface {
	UploadVisit(salesID uint, batchID string, visit *UploadVisitRequest) error
}

