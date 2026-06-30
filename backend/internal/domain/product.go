package domain

import (
	"time"
)

type Product struct {
	ID        uint      `gorm:"primaryKey;column:id"`
	Name      string    `gorm:"type:varchar(150);not null;column:name"`
	SKU       string    `gorm:"type:varchar(50);not null;unique;index:idx_products_sku;column:sku"`
	Price     float64   `gorm:"type:numeric(12,2);not null;column:price"`
	UOMBal    int16     `gorm:"not null;column:uom_bal"` // BAL per DUS
	UOMSlf    int16     `gorm:"not null;column:uom_slf"` // SLOP per BAL
	UOMBks    int16     `gorm:"not null;column:uom_bks"` // BUNGKUS per SLOP
	IsActive  bool      `gorm:"type:boolean;not null;default:true;column:is_active"`
	CreatedAt time.Time `gorm:"column:created_at;default:now()"`
	UpdatedAt time.Time `gorm:"column:updated_at;default:now()"`
}

func (Product) TableName() string {
	return "products"
}
