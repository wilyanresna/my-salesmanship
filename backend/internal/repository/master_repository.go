package repository

import (
	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type masterRepository struct {
	db *gorm.DB
}

// NewMasterRepository creates a new instance of MasterRepository.
func NewMasterRepository(db *gorm.DB) domain.MasterRepository {
	return &masterRepository{
		db: db,
	}
}

// Area
func (r *masterRepository) CreateArea(area *domain.Area) error {
	return r.db.Create(area).Error
}
func (r *masterRepository) GetAreaByID(id uint) (*domain.Area, error) {
	var area domain.Area
	if err := r.db.First(&area, id).Error; err != nil {
		return nil, err
	}
	return &area, nil
}
func (r *masterRepository) ListAreas() ([]domain.Area, error) {
	var areas []domain.Area
	if err := r.db.Find(&areas).Error; err != nil {
		return nil, err
	}
	return areas, nil
}
func (r *masterRepository) UpdateArea(area *domain.Area) error {
	return r.db.Save(area).Error
}
func (r *masterRepository) DeleteArea(id uint) error {
	return r.db.Delete(&domain.Area{}, id).Error
}

// Territory
func (r *masterRepository) CreateTerritory(t *domain.Territory) error {
	return r.db.Create(t).Error
}
func (r *masterRepository) GetTerritoryByID(id uint) (*domain.Territory, error) {
	var t domain.Territory
	if err := r.db.Preload("Area").First(&t, id).Error; err != nil {
		return nil, err
	}
	return &t, nil
}
func (r *masterRepository) ListTerritories() ([]domain.Territory, error) {
	var ts []domain.Territory
	if err := r.db.Preload("Area").Find(&ts).Error; err != nil {
		return nil, err
	}
	return ts, nil
}
func (r *masterRepository) UpdateTerritory(t *domain.Territory) error {
	return r.db.Save(t).Error
}
func (r *masterRepository) DeleteTerritory(id uint) error {
	return r.db.Delete(&domain.Territory{}, id).Error
}

// District
func (r *masterRepository) CreateDistrict(d *domain.District) error {
	return r.db.Create(d).Error
}
func (r *masterRepository) GetDistrictByID(id uint) (*domain.District, error) {
	var d domain.District
	if err := r.db.Preload("Territory.Area").First(&d, id).Error; err != nil {
		return nil, err
	}
	return &d, nil
}
func (r *masterRepository) ListDistricts() ([]domain.District, error) {
	var ds []domain.District
	if err := r.db.Preload("Territory.Area").Find(&ds).Error; err != nil {
		return nil, err
	}
	return ds, nil
}
func (r *masterRepository) UpdateDistrict(d *domain.District) error {
	return r.db.Save(d).Error
}
func (r *masterRepository) DeleteDistrict(id uint) error {
	return r.db.Delete(&domain.District{}, id).Error
}

// Route
func (r *masterRepository) CreateRoute(rt *domain.Route) error {
	return r.db.Create(rt).Error
}
func (r *masterRepository) GetRouteByID(id uint) (*domain.Route, error) {
	var rt domain.Route
	if err := r.db.Preload("District.Territory.Area").First(&rt, id).Error; err != nil {
		return nil, err
	}
	return &rt, nil
}
func (r *masterRepository) ListRoutes() ([]domain.Route, error) {
	var rts []domain.Route
	if err := r.db.Preload("District.Territory.Area").Find(&rts).Error; err != nil {
		return nil, err
	}
	return rts, nil
}
func (r *masterRepository) UpdateRoute(rt *domain.Route) error {
	return r.db.Save(rt).Error
}
func (r *masterRepository) DeleteRoute(id uint) error {
	return r.db.Delete(&domain.Route{}, id).Error
}

// Outlet
func (r *masterRepository) CreateOutlet(o *domain.Outlet) error {
	return r.db.Create(o).Error
}
func (r *masterRepository) GetOutletByID(id uint) (*domain.Outlet, error) {
	var o domain.Outlet
	if err := r.db.First(&o, id).Error; err != nil {
		return nil, err
	}
	return &o, nil
}
func (r *masterRepository) ListOutlets() ([]domain.Outlet, error) {
	var os []domain.Outlet
	if err := r.db.Find(&os).Error; err != nil {
		return nil, err
	}
	return os, nil
}
func (r *masterRepository) UpdateOutlet(o *domain.Outlet) error {
	return r.db.Save(o).Error
}
func (r *masterRepository) DeleteOutlet(id uint) error {
	return r.db.Delete(&domain.Outlet{}, id).Error
}

// Employee
func (r *masterRepository) CreateEmployee(e *domain.Employee) error {
	return r.db.Create(e).Error
}
func (r *masterRepository) GetEmployeeByID(id uint) (*domain.Employee, error) {
	var e domain.Employee
	if err := r.db.First(&e, id).Error; err != nil {
		return nil, err
	}
	return &e, nil
}
func (r *masterRepository) ListEmployees() ([]domain.Employee, error) {
	var es []domain.Employee
	if err := r.db.Find(&es).Error; err != nil {
		return nil, err
	}
	return es, nil
}
func (r *masterRepository) UpdateEmployee(e *domain.Employee) error {
	return r.db.Save(e).Error
}
func (r *masterRepository) DeleteEmployee(id uint) error {
	return r.db.Delete(&domain.Employee{}, id).Error
}

// Product (Read-only)
func (r *masterRepository) ListProducts() ([]domain.Product, error) {
	var ps []domain.Product
	if err := r.db.Find(&ps).Error; err != nil {
		return nil, err
	}
	return ps, nil
}

// Param (Read-only)
func (r *masterRepository) ListParams() ([]domain.Param, error) {
	var prs []domain.Param
	if err := r.db.Find(&prs).Error; err != nil {
		return nil, err
	}
	return prs, nil
}
