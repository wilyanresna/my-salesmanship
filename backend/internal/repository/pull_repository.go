package repository

import (
	"time"

	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type pullRepository struct {
	db *gorm.DB
}

// NewPullRepository creates a new instance of PullRepository.
func NewPullRepository(db *gorm.DB) domain.PullRepository {
	return &pullRepository{
		db: db,
	}
}

// GetMappingSales finds the weekly sales mapping for the salesman.
func (r *pullRepository) GetMappingSales(salesID uint, date time.Time) (*domain.MappingSales, error) {
	var m domain.MappingSales
	err := r.db.Preload("Sales").Preload("Spv").
		Where("sales_id = ? AND week_start <= ? AND week_end >= ?", salesID, date.Format("2006-01-02"), date.Format("2006-01-02")).
		First(&m).Error
	if err != nil {
		return nil, err
	}
	return &m, nil
}

// GetOutletsByDistrict finds all mapped outlets inside the district's routes for the week.
func (r *pullRepository) GetOutletsByDistrict(districtID uint, date time.Time) ([]domain.PullOutletResponse, error) {
	var routes []domain.Route
	if err := r.db.Where("district_id = ?", districtID).Find(&routes).Error; err != nil {
		return nil, err
	}
	if len(routes) == 0 {
		return []domain.PullOutletResponse{}, nil
	}

	routeIDs := make([]uint, len(routes))
	for i, rt := range routes {
		routeIDs[i] = rt.ID
	}

	var mappings []domain.MappingOutlet
	err := r.db.Preload("Outlet").
		Where("route_id IN (?) AND week_start <= ? AND week_end >= ?", routeIDs, date.Format("2006-01-02"), date.Format("2006-01-02")).
		Find(&mappings).Error
	if err != nil {
		return nil, err
	}

	outlets := make([]domain.PullOutletResponse, len(mappings))
	for i, m := range mappings {
		outlets[i] = domain.PullOutletResponse{
			ID:           m.Outlet.ID,
			Name:         m.Outlet.Name,
			OwnerName:    m.Outlet.OwnerName,
			Phone:        m.Outlet.Phone,
			Address:      m.Outlet.Address,
			Lat:          m.Outlet.Lat,
			Lng:          m.Outlet.Lng,
			Barcode:      m.Outlet.Barcode,
			OutletStatus: m.Outlet.OutletStatus,
			RouteID:      m.RouteID,
		}
	}

	return outlets, nil
}

// GetActiveProducts fetches all active products.
func (r *pullRepository) GetActiveProducts() ([]domain.Product, error) {
	var list []domain.Product
	if err := r.db.Where("is_active = ?", true).Find(&list).Error; err != nil {
		return nil, err
	}
	return list, nil
}

// GetActiveParams fetches all active parameters.
func (r *pullRepository) GetActiveParams() ([]domain.Param, error) {
	var list []domain.Param
	if err := r.db.Where("is_active = ?", true).Find(&list).Error; err != nil {
		return nil, err
	}
	return list, nil
}

// GetStockRokok fetches today's stock header and items for the salesman.
func (r *pullRepository) GetStockRokok(salesID uint, date time.Time) (*domain.StockRokok, []domain.StockRokokItem, error) {
	var stock domain.StockRokok
	err := r.db.Where("sales_id = ? AND date_used = ?", salesID, date.Format("2006-01-02")).First(&stock).Error
	if err != nil {
		return nil, nil, err
	}

	var items []domain.StockRokokItem
	err = r.db.Preload("Product").Where("stock_rokok_id = ?", stock.ID).Find(&items).Error
	if err != nil {
		return nil, nil, err
	}

	return &stock, items, nil
}

// UpdateStockStatus updates the stock status.
func (r *pullRepository) UpdateStockStatus(stockID uint, status string) error {
	return r.db.Model(&domain.StockRokok{}).Where("id = ?", stockID).Update("status", status).Error
}

// GetSalesTargets fetches weekly targets for the salesman.
func (r *pullRepository) GetSalesTargets(salesID uint, date time.Time) ([]domain.SalesTarget, error) {
	var list []domain.SalesTarget
	err := r.db.Preload("Product").
		Where("sales_id = ? AND week_start <= ? AND week_end >= ?", salesID, date.Format("2006-01-02"), date.Format("2006-01-02")).
		Find(&list).Error
	if err != nil {
		return nil, err
	}
	return list, nil
}
