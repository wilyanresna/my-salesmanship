package usecase

import (
	"errors"
	"time"

	"my-salesmanship/internal/domain"
)

type mappingUsecase struct {
	repo domain.MappingRepository
}

// NewMappingUsecase creates a new instance of MappingUsecase.
func NewMappingUsecase(repo domain.MappingRepository) domain.MappingUsecase {
	return &mappingUsecase{
		repo: repo,
	}
}

// CreateMappingSpv handles Spv to Territory weekly mapping creation.
func (u *mappingUsecase) CreateMappingSpv(spvID uint, territoryID uint, weekStart string, weekEnd string) (*domain.MappingSpv, error) {
	ws, err := time.Parse("2006-01-02", weekStart)
	if err != nil {
		return nil, errors.New("invalid week_start date format, must be YYYY-MM-DD")
	}
	we, err := time.Parse("2006-01-02", weekEnd)
	if err != nil {
		return nil, errors.New("invalid week_end date format, must be YYYY-MM-DD")
	}

	if ws.After(we) {
		return nil, errors.New("week_start cannot be after week_end")
	}

	m := &domain.MappingSpv{
		SpvID:       spvID,
		TerritoryID: territoryID,
		WeekStart:   ws,
		WeekEnd:     we,
	}

	if err := u.repo.CreateMappingSpv(m); err != nil {
		return nil, err
	}
	return m, nil
}

// ListMappingSpv fetches all MappingSpv records.
func (u *mappingUsecase) ListMappingSpv() ([]domain.MappingSpv, error) {
	return u.repo.ListMappingSpv()
}

// CreateMappingSales handles Sales to Spv & District weekly mapping creation.
func (u *mappingUsecase) CreateMappingSales(spvID uint, salesID uint, districtID uint, weekStart string, weekEnd string) (*domain.MappingSales, error) {
	ws, err := time.Parse("2006-01-02", weekStart)
	if err != nil {
		return nil, errors.New("invalid week_start date format, must be YYYY-MM-DD")
	}
	we, err := time.Parse("2006-01-02", weekEnd)
	if err != nil {
		return nil, errors.New("invalid week_end date format, must be YYYY-MM-DD")
	}

	if ws.After(we) {
		return nil, errors.New("week_start cannot be after week_end")
	}

	m := &domain.MappingSales{
		SpvID:      spvID,
		SalesID:    salesID,
		DistrictID: districtID,
		WeekStart:  ws,
		WeekEnd:    we,
	}

	if err := u.repo.CreateMappingSales(m); err != nil {
		return nil, err
	}
	return m, nil
}

// ListMappingSales fetches all MappingSales records.
func (u *mappingUsecase) ListMappingSales() ([]domain.MappingSales, error) {
	return u.repo.ListMappingSales()
}

// CreateMappingOutlet handles Outlet to Route weekly mapping creation.
func (u *mappingUsecase) CreateMappingOutlet(outletID uint, routeID uint, weekStart string, weekEnd string) (*domain.MappingOutlet, error) {
	ws, err := time.Parse("2006-01-02", weekStart)
	if err != nil {
		return nil, errors.New("invalid week_start date format, must be YYYY-MM-DD")
	}
	we, err := time.Parse("2006-01-02", weekEnd)
	if err != nil {
		return nil, errors.New("invalid week_end date format, must be YYYY-MM-DD")
	}

	if ws.After(we) {
		return nil, errors.New("week_start cannot be after week_end")
	}

	m := &domain.MappingOutlet{
		OutletID:  outletID,
		RouteID:   routeID,
		WeekStart: ws,
		WeekEnd:   we,
	}

	if err := u.repo.CreateMappingOutlet(m); err != nil {
		return nil, err
	}
	return m, nil
}

// ListMappingOutlet fetches all MappingOutlet records.
func (u *mappingUsecase) ListMappingOutlet() ([]domain.MappingOutlet, error) {
	return u.repo.ListMappingOutlet()
}
