package repository

import (
	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type employeeRepository struct {
	db *gorm.DB
}

// NewEmployeeRepository creates a new instance of EmployeeRepository.
func NewEmployeeRepository(db *gorm.DB) domain.EmployeeRepository {
	return &employeeRepository{
		db: db,
	}
}

// GetByUsername fetches an active employee by username.
func (r *employeeRepository) GetByUsername(username string) (*domain.Employee, error) {
	var emp domain.Employee
	err := r.db.Where("username = ? AND is_active = ?", username, true).First(&emp).Error
	if err != nil {
		return nil, err
	}
	return &emp, nil
}

// GetByID fetches an active employee by ID.
func (r *employeeRepository) GetByID(id uint) (*domain.Employee, error) {
	var emp domain.Employee
	err := r.db.Where("id = ? AND is_active = ?", id, true).First(&emp).Error
	if err != nil {
		return nil, err
	}
	return &emp, nil
}

// UpdatePassword updates an employee's password hash.
func (r *employeeRepository) UpdatePassword(id uint, newPasswordHash string) error {
	return r.db.Model(&domain.Employee{}).Where("id = ?", id).Update("password", newPasswordHash).Error
}
