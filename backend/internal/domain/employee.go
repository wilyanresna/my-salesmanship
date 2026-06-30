package domain

import (
	"time"

	"gorm.io/gorm"
)

type Employee struct {
	ID        uint           `gorm:"primaryKey;column:id"`
	Name      string         `gorm:"type:varchar(100);not null;column:name"`
	NIK       string         `gorm:"type:varchar(30);not null;unique;column:nik"`
	Username  string         `gorm:"type:varchar(50);not null;unique;index:idx_employees_username;column:username"`
	Password  string         `gorm:"type:varchar(255);not null;column:password"`
	Position  string         `gorm:"type:varchar(10);not null;index:idx_employees_position;column:position;check:position IN ('SPV', 'SALES')"`
	Phone     string         `gorm:"type:varchar(20);column:phone"`
	Address   string         `gorm:"type:text;column:address"`
	IsActive  bool           `gorm:"type:boolean;not null;default:true;column:is_active"`
	CreatedAt time.Time      `gorm:"column:created_at;default:now()"`
	UpdatedAt time.Time      `gorm:"column:updated_at;default:now()"`
	DeletedAt gorm.DeletedAt `gorm:"column:deleted_at;index"`
}

func (Employee) TableName() string {
	return "employees"
}

// EmployeeRepository defines the data access contract for Employee entity.
type EmployeeRepository interface {
	GetByUsername(username string) (*Employee, error)
	GetByID(id uint) (*Employee, error)
	UpdatePassword(id uint, newPasswordHash string) error
}

