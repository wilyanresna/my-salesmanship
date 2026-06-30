package usecase

import (
	"errors"

	"my-salesmanship/internal/domain"
)

type uploadUsecase struct {
	repo domain.TransactionRepository
}

// NewUploadUsecase creates a new instance of UploadUsecase.
func NewUploadUsecase(repo domain.TransactionRepository) domain.UploadUsecase {
	return &uploadUsecase{
		repo: repo,
	}
}

// UploadVisit validates the batch_id, maps request data to GORM entities, and stores them atomically.
func (u *uploadUsecase) UploadVisit(salesID uint, batchID string, visit *domain.UploadVisitRequest) error {
	// 1. Check for duplicate batch_id (idempotency constraint)
	exists, err := u.repo.HasBatchID(batchID)
	if err != nil {
		return err
	}
	if exists {
		return errors.New("conflict: batch_id already exists")
	}

	// 2. Map TrOutlet
	outlet := &domain.TrOutlet{
		BatchID:   batchID,
		SalesID:   salesID,
		OutletID:  visit.OutletID,
		VisitNo:   visit.VisitNo,
		VisitType: visit.VisitType,
		Status:    visit.Status,
		StartTime: visit.StartTime,
		EndTime:   visit.EndTime,
		Lat:       visit.Lat,
		Lng:       visit.Lng,
	}

	// 3. Map TrCheckStock items
	checkStocks := make([]domain.TrCheckStock, len(visit.CheckStocks))
	for i, cs := range visit.CheckStocks {
		checkStocks[i] = domain.TrCheckStock{
			BatchID:   batchID,
			ProductID: cs.ProductID,
			StockQty:  cs.StockQty,
		}
	}

	// 4. Map TrSales & TrSalesDetail items
	salesList := make([]domain.TrSales, len(visit.Sales))
	salesDetails := make([][]domain.TrSalesDetail, len(visit.Sales))

	for i, s := range visit.Sales {
		salesList[i] = domain.TrSales{
			BatchID:    batchID,
			SalesOrder: s.SalesOrder,
		}

		details := make([]domain.TrSalesDetail, len(s.Details))
		for j, d := range s.Details {
			details[j] = domain.TrSalesDetail{
				BatchID:   batchID,
				ProductID: d.ProductID,
				Qty:       d.Qty,
				Price:     d.Price,
				Total:     d.Total,
			}
		}
		salesDetails[i] = details
	}

	// 5. Store atomically in database
	return u.repo.CreateVisitTransaction(outlet, checkStocks, salesList, salesDetails)
}
