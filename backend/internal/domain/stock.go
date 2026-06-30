package domain

import (
	"time"
)

type StockRokok struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	SalesID   uint      `gorm:"not null;column:sales_id;uniqueIndex:idx_stock_rokok_sales_date;index:idx_stock_rokok_sales_date"`
	Sales     Employee  `gorm:"foreignKey:SalesID;constraint:OnDelete:RESTRICT"`
	DateUsed  time.Time `gorm:"type:date;not null;column:date_used;uniqueIndex:idx_stock_rokok_sales_date;index:idx_stock_rokok_sales_date"`
	Status    string    `gorm:"type:varchar(10);not null;default:'DRAFT';index:idx_stock_rokok_status;column:status;check:status IN ('DRAFT', 'READY', 'PULLED', 'CLOSED')"`
	CreatedBy uint      `gorm:"not null;column:created_by"`
	Creator   Employee  `gorm:"foreignKey:CreatedBy;constraint:OnDelete:RESTRICT"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt time.Time `gorm:"column:updated_at;default:now()"`
}

func (StockRokok) TableName() string {
	return "stock_rokok"
}

type StockRokokItem struct {
	ID           uint       `gorm:"primaryKey;column:id"`
	StockRokokID uint       `gorm:"not null;column:stock_rokok_id;uniqueIndex:idx_stock_rokok_item_unique;index:idx_stock_item_rokok"`
	StockRokok   StockRokok `gorm:"foreignKey:StockRokokID;constraint:OnDelete:CASCADE"`
	ProductID    uint       `gorm:"not null;column:product_id;uniqueIndex:idx_stock_rokok_item_unique"`
	Product      Product    `gorm:"foreignKey:ProductID;constraint:OnDelete:RESTRICT"`
	QtyDusInit   int16      `gorm:"not null;default:0;column:qty_dus_init"`
	QtyBalInit   int16      `gorm:"not null;default:0;column:qty_bal_init"`
	QtySlfInit   int16      `gorm:"not null;default:0;column:qty_slf_init"`
	QtyBksInit   int16      `gorm:"not null;default:0;column:qty_bks_init"`
	CreatedAt    time.Time  `gorm:"column:created_at;default:now()"`
}

func (StockRokokItem) TableName() string {
	return "stock_rokok_item"
}

// StockRepository defines the data access contract for stock allocation.
type StockRepository interface {
	CreateStock(stock *StockRokok, items []StockRokokItem) error
	GetStockByID(id uint) (*StockRokok, error)
	GetStockItems(stockID uint) ([]StockRokokItem, error)
	ListAllStocks() ([]StockRokok, error)
	ListStocksBySalesID(salesID uint) ([]StockRokok, error)
	UpdateStockStatus(id uint, status string) error
}

// StockUsecase defines the business logic contract for stock allocation.
type StockUsecase interface {
	CreateStock(salesID uint, dateUsed string, creatorID uint, items []StockRokokItem) (*StockRokok, error)
	ListStocks(userID uint, position string) ([]StockRokok, error)
	GetStockDetail(id uint) (*StockRokok, []StockRokokItem, error)
	UpdateStockStatus(id uint, status string) error
}

