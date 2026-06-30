package repository

import (
	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type transactionRepository struct {
	db *gorm.DB
}

// NewTransactionRepository creates a new instance of TransactionRepository.
func NewTransactionRepository(db *gorm.DB) domain.TransactionRepository {
	return &transactionRepository{
		db: db,
	}
}

// HasBatchID checks if the batch_id has already been uploaded (idempotency check).
func (r *transactionRepository) HasBatchID(batchID string) (bool, error) {
	var count int64
	err := r.db.Model(&domain.TrOutlet{}).Where("batch_id = ?", batchID).Count(&count).Error
	if err != nil {
		return false, err
	}
	return count > 0, nil
}

// CreateVisitTransaction handles saving a whole visit's transaction models atomically.
func (r *transactionRepository) CreateVisitTransaction(
	outlet *domain.TrOutlet,
	checkStocks []domain.TrCheckStock,
	sales []domain.TrSales,
	salesDetails [][]domain.TrSalesDetail,
) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		// 1. Insert tr_outlet
		if err := tx.Create(outlet).Error; err != nil {
			return err
		}

		// 2. Insert tr_check_stock
		for i := range checkStocks {
			checkStocks[i].TrOutletID = outlet.ID
			if err := tx.Create(&checkStocks[i]).Error; err != nil {
				return err
			}
		}

		// 3. Insert tr_sales and tr_sales_detail
		for i := range sales {
			sales[i].TrOutletID = outlet.ID
			if err := tx.Create(&sales[i]).Error; err != nil {
				return err
			}
			for j := range salesDetails[i] {
				salesDetails[i][j].TrSalesID = sales[i].ID
				if err := tx.Create(&salesDetails[i][j]).Error; err != nil {
					return err
				}
			}
		}

		return nil
	})
}
