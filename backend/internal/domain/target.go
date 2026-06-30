package domain

import (
	"time"
)

type SalesTarget struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	SalesID   uint      `gorm:"not null;column:sales_id;uniqueIndex:idx_sales_target_unique;index:idx_target_sales_week"`
	Sales     Employee  `gorm:"foreignKey:SalesID;constraint:OnDelete:RESTRICT"`
	RouteID   uint      `gorm:"not null;column:route_id;uniqueIndex:idx_sales_target_unique;index:idx_target_route"`
	Route     Route     `gorm:"foreignKey:RouteID;constraint:OnDelete:RESTRICT"`
	ProductID uint      `gorm:"not null;column:product_id;uniqueIndex:idx_sales_target_unique"`
	Product   Product   `gorm:"foreignKey:ProductID;constraint:OnDelete:RESTRICT"`
	TargetQty int32     `gorm:"not null;column:target_qty"` // dalam satuan BUNGKUS
	WeekStart time.Time `gorm:"type:date;not null;column:week_start;uniqueIndex:idx_sales_target_unique;index:idx_target_sales_week"`
	WeekEnd   time.Time `gorm:"type:date;not null;column:week_end"`
	CreatedBy uint      `gorm:"not null;column:created_by"`
	Creator   Employee  `gorm:"foreignKey:CreatedBy;constraint:OnDelete:RESTRICT"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
}

func (SalesTarget) TableName() string {
	return "sales_target"
}

// TargetRepository defines the data access contract for sales target entities.
type TargetRepository interface {
	CreateTarget(target *SalesTarget) error
	GetTargetByID(id uint) (*SalesTarget, error)
	ListTargets() ([]SalesTarget, error)
	UpdateTarget(target *SalesTarget) error
	DeleteTarget(id uint) error
}

// TargetUsecase defines the business logic contract for sales target entities.
type TargetUsecase interface {
	CreateTarget(salesID uint, routeID uint, productID uint, targetQty int32, weekStart string, weekEnd string, createdBy uint) (*SalesTarget, error)
	GetTarget(id uint) (*SalesTarget, error)
	ListTargets() ([]SalesTarget, error)
	UpdateTarget(id uint, salesID uint, routeID uint, productID uint, targetQty int32, weekStart string, weekEnd string) (*SalesTarget, error)
	DeleteTarget(id uint) error
}

