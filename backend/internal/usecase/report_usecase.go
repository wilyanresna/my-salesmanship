package usecase

import (
	"time"

	"my-salesmanship/internal/domain"
)

type reportUsecase struct {
	repo domain.ReportRepository
}

// NewReportUsecase creates a new instance of ReportUsecase.
func NewReportUsecase(repo domain.ReportRepository) domain.ReportUsecase {
	return &reportUsecase{
		repo: repo,
	}
}

// GetVisitsReport parses date filter and calls repository.
func (u *reportUsecase) GetVisitsReport(salesID uint, dateStr string) ([]domain.VisitReportItem, error) {
	var date time.Time
	if dateStr != "" {
		var err error
		date, err = time.Parse("2006-01-02", dateStr)
		if err != nil {
			return nil, err
		}
	} else {
		date = time.Now()
	}
	return u.repo.GetVisitsReport(salesID, date)
}

// GetSalesReport parses date range filters and calls repository.
func (u *reportUsecase) GetSalesReport(salesID uint, startDateStr, endDateStr string) ([]domain.SalesReportItem, error) {
	var startDate, endDate time.Time
	var err error
	if startDateStr != "" {
		startDate, err = time.Parse("2006-01-02", startDateStr)
		if err != nil {
			return nil, err
		}
	}
	if endDateStr != "" {
		endDate, err = time.Parse("2006-01-02", endDateStr)
		if err != nil {
			return nil, err
		}
		// Adjust to end of the day
		endDate = endDate.Add(23*time.Hour + 59*time.Minute + 59*time.Second)
	}
	return u.repo.GetSalesReport(salesID, startDate, endDate)
}

// GetStockReport parses date filter and calls repository.
func (u *reportUsecase) GetStockReport(salesID uint, dateStr string) ([]domain.StockReportItem, error) {
	var date time.Time
	if dateStr != "" {
		var err error
		date, err = time.Parse("2006-01-02", dateStr)
		if err != nil {
			return nil, err
		}
	} else {
		date = time.Now()
	}
	return u.repo.GetStockReport(salesID, date)
}

// GetAchievementReport parses date filter and calls repository.
func (u *reportUsecase) GetAchievementReport(salesID uint, dateStr string) ([]domain.AchievementReportItem, error) {
	var date time.Time
	if dateStr != "" {
		var err error
		date, err = time.Parse("2006-01-02", dateStr)
		if err != nil {
			return nil, err
		}
	} else {
		date = time.Now()
	}
	return u.repo.GetAchievementReport(salesID, date)
}
