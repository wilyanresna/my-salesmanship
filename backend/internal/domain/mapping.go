package domain

import (
	"time"
)

type MappingSpv struct {
	ID          uint      `gorm:"primaryKey;column:id"`
	SpvID       uint      `gorm:"not null;column:spv_id;uniqueIndex:idx_mapping_spv_unique"`
	Spv         Employee  `gorm:"foreignKey:SpvID;constraint:OnDelete:RESTRICT"`
	TerritoryID uint      `gorm:"not null;column:territory_id;uniqueIndex:idx_mapping_spv_unique"`
	Territory   Territory `gorm:"foreignKey:TerritoryID;constraint:OnDelete:RESTRICT"`
	WeekStart   time.Time `gorm:"type:date;not null;column:week_start;uniqueIndex:idx_mapping_spv_unique;index:idx_mapping_spv_week"`
	WeekEnd     time.Time `gorm:"type:date;not null;column:week_end;index:idx_mapping_spv_week"`
	CreatedAt   time.Time `gorm:"column:created_at;default:now()"`
}

func (MappingSpv) TableName() string {
	return "mapping_spv"
}

type MappingSales struct {
	ID         uint      `gorm:"primaryKey;column:id"`
	SpvID      uint      `gorm:"not null;column:spv_id"`
	Spv        Employee  `gorm:"foreignKey:SpvID;constraint:OnDelete:RESTRICT"`
	SalesID    uint      `gorm:"not null;column:sales_id;uniqueIndex:idx_mapping_sales_unique;index:idx_mapping_sales_sales"`
	Sales      Employee  `gorm:"foreignKey:SalesID;constraint:OnDelete:RESTRICT"`
	DistrictID uint      `gorm:"not null;column:district_id"`
	District   District  `gorm:"foreignKey:DistrictID;constraint:OnDelete:RESTRICT"`
	WeekStart  time.Time `gorm:"type:date;not null;column:week_start;uniqueIndex:idx_mapping_sales_unique;index:idx_mapping_sales_week"`
	WeekEnd    time.Time `gorm:"type:date;not null;column:week_end;index:idx_mapping_sales_week"`
	CreatedAt  time.Time `gorm:"column:created_at;default:now()"`
}

func (MappingSales) TableName() string {
	return "mapping_sales"
}

type MappingOutlet struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	OutletID  uint      `gorm:"not null;column:outlet_id;uniqueIndex:idx_mapping_outlet_unique"`
	Outlet    Outlet    `gorm:"foreignKey:OutletID;constraint:OnDelete:RESTRICT"`
	RouteID   uint      `gorm:"not null;column:route_id;index:idx_mapping_outlet_route"`
	Route     Route     `gorm:"foreignKey:RouteID;constraint:OnDelete:RESTRICT"`
	WeekStart time.Time `gorm:"type:date;not null;column:week_start;uniqueIndex:idx_mapping_outlet_unique;index:idx_mapping_outlet_week"`
	WeekEnd   time.Time `gorm:"type:date;not null;column:week_end"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
}

func (MappingOutlet) TableName() string {
	return "mapping_outlet"
}

// MappingRepository defines the data access contract for weekly mapping entities.
type MappingRepository interface {
	CreateMappingSpv(m *MappingSpv) error
	ListMappingSpv() ([]MappingSpv, error)

	CreateMappingSales(m *MappingSales) error
	ListMappingSales() ([]MappingSales, error)

	CreateMappingOutlet(m *MappingOutlet) error
	ListMappingOutlet() ([]MappingOutlet, error)
}

// MappingUsecase defines the business logic contract for weekly mapping entities.
type MappingUsecase interface {
	CreateMappingSpv(spvID uint, territoryID uint, weekStart string, weekEnd string) (*MappingSpv, error)
	ListMappingSpv() ([]MappingSpv, error)

	CreateMappingSales(spvID uint, salesID uint, districtID uint, weekStart string, weekEnd string) (*MappingSales, error)
	ListMappingSales() ([]MappingSales, error)

	CreateMappingOutlet(outletID uint, routeID uint, weekStart string, weekEnd string) (*MappingOutlet, error)
	ListMappingOutlet() ([]MappingOutlet, error)
}

