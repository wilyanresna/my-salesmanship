package repository

import (
	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type mappingRepository struct {
	db *gorm.DB
}

// NewMappingRepository creates a new instance of MappingRepository.
func NewMappingRepository(db *gorm.DB) domain.MappingRepository {
	return &mappingRepository{
		db: db,
	}
}

// CreateMappingSpv inserts a MappingSpv record.
func (r *mappingRepository) CreateMappingSpv(m *domain.MappingSpv) error {
	return r.db.Create(m).Error
}

// ListMappingSpv fetches all MappingSpv records with preloads.
func (r *mappingRepository) ListMappingSpv() ([]domain.MappingSpv, error) {
	var list []domain.MappingSpv
	err := r.db.Preload("Spv").Preload("Territory.Area").Find(&list).Error
	if err != nil {
		return nil, err
	}
	return list, nil
}

// CreateMappingSales inserts a MappingSales record.
func (r *mappingRepository) CreateMappingSales(m *domain.MappingSales) error {
	return r.db.Create(m).Error
}

// ListMappingSales fetches all MappingSales records with preloads.
func (r *mappingRepository) ListMappingSales() ([]domain.MappingSales, error) {
	var list []domain.MappingSales
	err := r.db.Preload("Spv").Preload("Sales").Preload("District.Territory.Area").Find(&list).Error
	if err != nil {
		return nil, err
	}
	return list, nil
}

// CreateMappingOutlet inserts a MappingOutlet record.
func (r *mappingRepository) CreateMappingOutlet(m *domain.MappingOutlet) error {
	return r.db.Create(m).Error
}

// ListMappingOutlet fetches all MappingOutlet records with preloads.
func (r *mappingRepository) ListMappingOutlet() ([]domain.MappingOutlet, error) {
	var list []domain.MappingOutlet
	err := r.db.Preload("Outlet").Preload("Route.District.Territory.Area").Find(&list).Error
	if err != nil {
		return nil, err
	}
	return list, nil
}
