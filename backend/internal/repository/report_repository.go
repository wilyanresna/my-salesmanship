package repository

import (
	"time"

	"my-salesmanship/internal/domain"

	"gorm.io/gorm"
)

type reportRepository struct {
	db *gorm.DB
}

// NewReportRepository creates a new instance of ReportRepository.
func NewReportRepository(db *gorm.DB) domain.ReportRepository {
	return &reportRepository{
		db: db,
	}
}

// GetVisitsReport retrieves visit logs with outlet and sales names.
func (r *reportRepository) GetVisitsReport(salesID uint, date time.Time) ([]domain.VisitReportItem, error) {
	var items []domain.VisitReportItem
	q := r.db.Table("tr_outlet t").
		Select("t.id, t.batch_id, t.sales_id, e.name as sales_name, t.outlet_id, o.name as outlet_name, t.visit_no, t.visit_type, t.status, t.start_time, t.end_time, t.lat, t.lng, t.uploaded_at").
		Joins("JOIN employees e ON e.id = t.sales_id").
		Joins("JOIN outlets o ON o.id = t.outlet_id")

	if salesID > 0 {
		q = q.Where("t.sales_id = ?", salesID)
	}
	if !date.IsZero() {
		q = q.Where("DATE(t.start_time) = ?", date.Format("2006-01-02"))
	}

	err := q.Order("t.start_time DESC").Scan(&items).Error
	return items, err
}

// GetSalesReport retrieves sum of items sold per product.
func (r *reportRepository) GetSalesReport(salesID uint, startDate, endDate time.Time) ([]domain.SalesReportItem, error) {
	var items []domain.SalesReportItem
	q := r.db.Table("tr_sales_detail tsd").
		Select("tsd.product_id, p.name as product_name, p.sku as product_sku, SUM(tsd.qty) as total_qty, SUM(tsd.total) as total_amount").
		Joins("JOIN products p ON p.id = tsd.product_id").
		Joins("JOIN tr_sales ts ON ts.id = tsd.tr_sales_id").
		Joins("JOIN tr_outlet t ON t.id = ts.tr_outlet_id")

	if salesID > 0 {
		q = q.Where("t.sales_id = ?", salesID)
	}
	if !startDate.IsZero() {
		q = q.Where("t.start_time >= ?", startDate)
	}
	if !endDate.IsZero() {
		q = q.Where("t.start_time <= ?", endDate)
	}

	err := q.Group("tsd.product_id, p.name, p.sku").Order("total_qty DESC").Scan(&items).Error
	return items, err
}

// GetStockReport retrieves the initial stock vs total sold to calculate current stock.
func (r *reportRepository) GetStockReport(salesID uint, date time.Time) ([]domain.StockReportItem, error) {
	var stocks []domain.StockRokok
	q := r.db.Preload("Sales")
	if salesID > 0 {
		q = q.Where("sales_id = ?", salesID)
	}
	if !date.IsZero() {
		q = q.Where("date_used = ?", date.Format("2006-01-02"))
	}

	if err := q.Find(&stocks).Error; err != nil {
		return nil, err
	}

	var reportItems []domain.StockReportItem
	for _, s := range stocks {
		var items []domain.StockRokokItem
		if err := r.db.Preload("Product").Where("stock_rokok_id = ?", s.ID).Find(&items).Error; err != nil {
			return nil, err
		}

		for _, item := range items {
			// Convert initial quantity DUS, BAL, SLOP, BKS to packs (bungkus)
			initBks := int64(item.QtyDusInit)*int64(item.Product.UOMBal)*int64(item.Product.UOMSlf)*int64(item.Product.UOMBks) +
				int64(item.QtyBalInit)*int64(item.Product.UOMSlf)*int64(item.Product.UOMBks) +
				int64(item.QtySlfInit)*int64(item.Product.UOMBks) +
				int64(item.QtyBksInit)

			// Query sum sold qty for this product, sales, and day
			var soldQty int64
			err := r.db.Table("tr_sales_detail tsd").
				Joins("JOIN tr_sales ts ON ts.id = tsd.tr_sales_id").
				Joins("JOIN tr_outlet t ON t.id = ts.tr_outlet_id").
				Where("t.sales_id = ? AND tsd.product_id = ? AND DATE(t.start_time) = ?", s.SalesID, item.ProductID, s.DateUsed.Format("2006-01-02")).
				Select("COALESCE(SUM(tsd.qty), 0)").
				Row().Scan(&soldQty)
			if err != nil {
				return nil, err
			}

			reportItems = append(reportItems, domain.StockReportItem{
				SalesID:        s.SalesID,
				SalesName:      s.Sales.Name,
				ProductID:      item.ProductID,
				ProductName:    item.Product.Name,
				ProductSKU:     item.Product.SKU,
				InitialBungkus: initBks,
				SoldBungkus:    soldQty,
				CurrentBungkus: initBks - soldQty,
			})
		}
	}
	return reportItems, nil
}

// GetAchievementReport retrieves target vs actual items sold for the week of the specified date.
func (r *reportRepository) GetAchievementReport(salesID uint, date time.Time) ([]domain.AchievementReportItem, error) {
	var targets []domain.SalesTarget
	q := r.db.Preload("Sales").Preload("Route").Preload("Product")
	if salesID > 0 {
		q = q.Where("sales_id = ?", salesID)
	}

	dateStr := date.Format("2006-01-02")
	q = q.Where("week_start <= ? AND week_end >= ?", dateStr, dateStr)

	if err := q.Find(&targets).Error; err != nil {
		return nil, err
	}

	var reportItems []domain.AchievementReportItem
	for _, t := range targets {
		var actualQty int64
		err := r.db.Table("tr_sales_detail tsd").
			Joins("JOIN tr_sales ts ON ts.id = tsd.tr_sales_id").
			Joins("JOIN tr_outlet t ON t.id = ts.tr_outlet_id").
			Joins("JOIN mapping_outlet mo ON mo.outlet_id = t.outlet_id").
			Where("t.sales_id = ? AND tsd.product_id = ? AND mo.route_id = ? AND DATE(t.start_time) >= ? AND DATE(t.start_time) <= ? AND mo.week_start = ?",
				t.SalesID, t.ProductID, t.RouteID, t.WeekStart.Format("2006-01-02"), t.WeekEnd.Format("2006-01-02"), t.WeekStart.Format("2006-01-02")).
			Select("COALESCE(SUM(tsd.qty), 0)").
			Row().Scan(&actualQty)
		if err != nil {
			return nil, err
		}

		percentage := 0.0
		if t.TargetQty > 0 {
			percentage = (float64(actualQty) / float64(t.TargetQty)) * 100.0
		}

		reportItems = append(reportItems, domain.AchievementReportItem{
			SalesID:     t.SalesID,
			SalesName:   t.Sales.Name,
			RouteID:     t.RouteID,
			RouteName:   t.Route.Name,
			ProductID:   t.ProductID,
			ProductName: t.Product.Name,
			ProductSKU:  t.Product.SKU,
			TargetQty:   int64(t.TargetQty),
			ActualQty:   actualQty,
			Percentage:  percentage,
		})
	}
	return reportItems, nil
}
