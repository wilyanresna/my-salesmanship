package usecase

import (
	"errors"
	"time"

	"my-salesmanship/internal/domain"
)

type pullUsecase struct {
	repo domain.PullRepository
}

// NewPullUsecase creates a new instance of PullUsecase.
func NewPullUsecase(repo domain.PullRepository) domain.PullUsecase {
	return &pullUsecase{
		repo: repo,
	}
}

// PullData fetches all required sync data for the active week/day.
func (u *pullUsecase) PullData(salesID uint, date time.Time) (*domain.PullResponse, error) {
	// 1. Fetch MappingSales for active week
	mapping, err := u.repo.GetMappingSales(salesID, date)
	if err != nil {
		return nil, errors.New("salesman mapping for this week not found")
	}

	// 2. Fetch Outlets in district
	outlets, err := u.repo.GetOutletsByDistrict(mapping.DistrictID, date)
	if err != nil {
		return nil, err
	}

	// 3. Fetch Products
	products, err := u.repo.GetActiveProducts()
	if err != nil {
		return nil, err
	}

	// 4. Fetch Params
	params, err := u.repo.GetActiveParams()
	if err != nil {
		return nil, err
	}

	// 5. Fetch StockRokok
	stock, items, err := u.repo.GetStockRokok(salesID, date)
	if err != nil {
		return nil, errors.New("stok rokok untuk hari ini belum disiapkan (READY)")
	}

	// Validate stock status is READY, PULLED, or CLOSED
	if stock.Status == "DRAFT" {
		return nil, errors.New("stok rokok untuk hari ini belum siap (READY)")
	}

	// If stock status is READY, update to PULLED after a successful pull
	if stock.Status == "READY" {
		if err := u.repo.UpdateStockStatus(stock.ID, "PULLED"); err != nil {
			return nil, err
		}
		stock.Status = "PULLED"
	}

	// 6. Fetch Sales Targets
	targets, err := u.repo.GetSalesTargets(salesID, date)
	if err != nil {
		return nil, err
	}

	// Build response payload
	res := &domain.PullResponse{
		Salesman: domain.PullSalesmanResponse{
			ID:         mapping.Sales.ID,
			Name:       mapping.Sales.Name,
			DistrictID: mapping.DistrictID,
			SpvName:    mapping.Spv.Name,
		},
		Outlets:  outlets,
		Products: products,
		Params:   params,
		StockRokok: &domain.PullStockResponse{
			ID:       stock.ID,
			DateUsed: stock.DateUsed,
			Status:   stock.Status,
			Items:    items,
		},
		Targets: targets,
	}

	return res, nil
}
