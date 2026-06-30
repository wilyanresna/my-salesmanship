package domain

import (
	"time"

	"gorm.io/gorm"
)

type Area struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	Name      string    `gorm:"type:varchar(100);not null;column:name"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt time.Time `gorm:"column:updated_at;default:now()"`
}

func (Area) TableName() string {
	return "areas"
}

type Territory struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	AreaID    uint      `gorm:"not null;column:area_id;index:idx_territories_area"`
	Area      Area      `gorm:"foreignKey:AreaID;constraint:OnDelete:RESTRICT"`
	Name      string    `gorm:"type:varchar(100);not null;column:name"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt time.Time `gorm:"column:updated_at;default:now()"`
}

func (Territory) TableName() string {
	return "territories"
}

type District struct {
	ID          uint      `gorm:"primaryKey;column:id"`
	TerritoryID uint      `gorm:"not null;column:territory_id;index:idx_districts_territory"`
	Territory   Territory `gorm:"foreignKey:TerritoryID;constraint:OnDelete:RESTRICT"`
	Name        string    `gorm:"type:varchar(100);not null;column:name"`
	CreatedAt   time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt   time.Time `gorm:"column:updated_at;default:now()"`
}

func (District) TableName() string {
	return "districts"
}

type Route struct {
	ID         uint      `gorm:"primaryKey;column:id"`
	DistrictID uint      `gorm:"not null;column:district_id;index:idx_routes_district"`
	District   District  `gorm:"foreignKey:DistrictID;constraint:OnDelete:RESTRICT"`
	Name       string    `gorm:"type:varchar(100);not null;column:name"`
	DayOfWeek  int16     `gorm:"not null;column:day_of_week;check:day_of_week BETWEEN 1 AND 7"`
	CreatedAt  time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt  time.Time `gorm:"column:updated_at;default:now()"`
}

func (Route) TableName() string {
	return "routes"
}

type Outlet struct {
	ID           uint           `gorm:"primaryKey;column:id"`
	Name         string         `gorm:"type:varchar(150);not null;column:name"`
	OwnerName    string         `gorm:"type:varchar(100);column:owner_name"`
	Phone        string         `gorm:"type:varchar(20);column:phone"`
	Address      string         `gorm:"type:text;column:address"`
	Lat          *float64       `gorm:"type:numeric(10,7);column:lat"`
	Lng          *float64       `gorm:"type:numeric(10,7);column:lng"`
	Barcode      string         `gorm:"type:varchar(100);unique;index:idx_outlets_barcode;column:barcode"`
	OutletStatus string         `gorm:"type:varchar(20);not null;default:'ACTIVE';index:idx_outlets_status;column:outlet_status;check:outlet_status IN ('ACTIVE', 'INACTIVE', 'POTENTIAL')"`
	CallCycle    string         `gorm:"type:varchar(10);not null;default:'WEEKLY';column:call_cycle;check:call_cycle IN ('DAILY', 'WEEKLY', 'BIWEEKLY')"`
	CreatedAt    time.Time      `gorm:"column:created_at;default:now()"`
	UpdatedAt    time.Time      `gorm:"column:updated_at;default:now()"`
	DeletedAt    gorm.DeletedAt `gorm:"column:deleted_at;index"`
}

func (Outlet) TableName() string {
	return "outlets"
}
