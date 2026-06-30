package usecase

import (
	"errors"
	"time"

	"my-salesmanship/internal/domain"
)

type stockTargetUsecase struct {
	stockRepo  domain.StockRepository
	targetRepo domain.TargetRepository
}

// NewStockTargetUsecase creates a new instance of StockUsecase and TargetUsecase combined.
func NewStockTargetUsecase(stockRepo domain.StockRepository, targetRepo domain.TargetRepository) *stockTargetUsecase {
	return &stockTargetUsecase{
		stockRepo:  stockRepo,
		targetRepo: targetRepo,
	}
}

// ---- StockUsecase Implementation ----

func (u *stockTargetUsecase) CreateStock(salesID uint, dateUsed string, creatorID uint, items []domain.StockRokokItem) (*domain.StockRokok, error) {
	du, err := time.Parse("2006-01-02", dateUsed)
	if err != nil {
		return nil, errors.New("invalid date_used date format, must be YYYY-MM-DD")
	}

	stock := &domain.StockRokok{
		SalesID:   salesID,
		DateUsed:  du,
		Status:    "DRAFT",
		CreatedBy: creatorID,
	}

	if err := u.stockRepo.CreateStock(stock, items); err != nil {
		return nil, err
	}
	return stock, nil
}

func (u *stockTargetUsecase) ListStocks(userID uint, position string) ([]domain.StockRokok, error) {
	if position == "SPV" {
		return u.stockRepo.ListAllStocks()
	}
	return u.stockRepo.ListStocksBySalesID(userID)
}

func (u *stockTargetUsecase) GetStockDetail(id uint) (*domain.StockRokok, []domain.StockRokokItem, error) {
	stock, err := u.stockRepo.GetStockByID(id)
	if err != nil {
		return nil, nil, err
	}
	items, err := u.stockRepo.GetStockItems(id)
	if err != nil {
		return nil, nil, err
	}
	return stock, items, nil
}

func (u *stockTargetUsecase) UpdateStockStatus(id uint, status string) error {
	if status != "DRAFT" && status != "READY" && status != "PULLED" && status != "CLOSED" {
		return errors.New("invalid status value")
	}
	return u.stockRepo.UpdateStockStatus(id, status)
}

// ---- TargetUsecase Implementation ----

func (u *stockTargetUsecase) CreateTarget(salesID uint, routeID uint, productID uint, targetQty int32, weekStart string, weekEnd string, createdBy uint) (*domain.SalesTarget, error) {
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

	target := &domain.SalesTarget{
		SalesID:   salesID,
		RouteID:   routeID,
		ProductID: productID,
		TargetQty: targetQty,
		WeekStart: ws,
		WeekEnd:   we,
		CreatedBy: createdBy,
	}

	if err := u.targetRepo.CreateTarget(target); err != nil {
		return nil, err
	}
	return target, nil
}

func (u *stockTargetUsecase) GetTarget(id uint) (*domain.SalesTarget, error) {
	return u.targetRepo.GetTargetByID(id)
}

func (u *stockTargetUsecase) ListTargets() ([]domain.SalesTarget, error) {
	return u.targetRepo.ListTargets()
}

func (u *stockTargetUsecase) UpdateTarget(id uint, salesID uint, routeID uint, productID uint, targetQty int32, weekStart string, weekEnd string) (*domain.SalesTarget, error) {
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

	target, err := u.targetRepo.GetTargetByID(id)
	if err != nil {
		return nil, err
	}

	target.SalesID = salesID
	target.RouteID = routeID
	target.ProductID = productID
	target.TargetQty = targetQty
	target.WeekStart = ws
	target.WeekEnd = we

	if err := u.targetRepo.UpdateTarget(target); err != nil {
		return nil, err
	}
	return target, nil
}

func (u *stockTargetUsecase) DeleteTarget(id uint) error {
	return u.targetRepo.DeleteTarget(id)
}
