package repository

import (
	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type stockTargetRepository struct {
	db *gorm.DB
}

// NewStockTargetRepository creates a new instance of StockRepository and TargetRepository combined.
func NewStockTargetRepository(db *gorm.DB) *stockTargetRepository {
	return &stockTargetRepository{
		db: db,
	}
}

// ---- StockRepository Implementation ----

func (r *stockTargetRepository) CreateStock(stock *domain.StockRokok, items []domain.StockRokokItem) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		if err := tx.Create(stock).Error; err != nil {
			return err
		}
		for i := range items {
			items[i].StockRokokID = stock.ID
			if err := tx.Create(&items[i]).Error; err != nil {
				return err
			}
		}
		return nil
	})
}

func (r *stockTargetRepository) GetStockByID(id uint) (*domain.StockRokok, error) {
	var stock domain.StockRokok
	if err := r.db.Preload("Sales").Preload("Creator").First(&stock, id).Error; err != nil {
		return nil, err
	}
	return &stock, nil
}

func (r *stockTargetRepository) GetStockItems(stockID uint) ([]domain.StockRokokItem, error) {
	var items []domain.StockRokokItem
	if err := r.db.Preload("Product").Where("stock_rokok_id = ?", stockID).Find(&items).Error; err != nil {
		return nil, err
	}
	return items, nil
}

func (r *stockTargetRepository) ListAllStocks() ([]domain.StockRokok, error) {
	var stocks []domain.StockRokok
	if err := r.db.Preload("Sales").Preload("Creator").Find(&stocks).Error; err != nil {
		return nil, err
	}
	return stocks, nil
}

func (r *stockTargetRepository) ListStocksBySalesID(salesID uint) ([]domain.StockRokok, error) {
	var stocks []domain.StockRokok
	if err := r.db.Preload("Sales").Preload("Creator").Where("sales_id = ?", salesID).Find(&stocks).Error; err != nil {
		return nil, err
	}
	return stocks, nil
}

func (r *stockTargetRepository) UpdateStockStatus(id uint, status string) error {
	return r.db.Model(&domain.StockRokok{}).Where("id = ?", id).Update("status", status).Error
}

// ---- TargetRepository Implementation ----

func (r *stockTargetRepository) CreateTarget(target *domain.SalesTarget) error {
	return r.db.Create(target).Error
}

func (r *stockTargetRepository) GetTargetByID(id uint) (*domain.SalesTarget, error) {
	var target domain.SalesTarget
	if err := r.db.Preload("Sales").Preload("Route.District.Territory.Area").Preload("Product").Preload("Creator").First(&target, id).Error; err != nil {
		return nil, err
	}
	return &target, nil
}

func (r *stockTargetRepository) ListTargets() ([]domain.SalesTarget, error) {
	var targets []domain.SalesTarget
	if err := r.db.Preload("Sales").Preload("Route.District.Territory.Area").Preload("Product").Preload("Creator").Find(&targets).Error; err != nil {
		return nil, err
	}
	return targets, nil
}

func (r *stockTargetRepository) UpdateTarget(target *domain.SalesTarget) error {
	return r.db.Save(target).Error
}

func (r *stockTargetRepository) DeleteTarget(id uint) error {
	return r.db.Delete(&domain.SalesTarget{}, id).Error
}
