package database

import (
	"log"
	"time"

	"my-salesmanship/internal/domain"

	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

func SeedDB(db *gorm.DB) error {
	log.Println("Seeding database...")

	// 1. Seed Params
	var paramCount int64
	if err := db.Model(&domain.Param{}).Count(&paramCount).Error; err != nil {
		return err
	}
	if paramCount == 0 {
		params := []domain.Param{
			{GroupName: "VISIT_TYPE", Key: "NORMAL", Value: "Normal", Description: "Kunjungan dalam route", IsActive: true},
			{GroupName: "VISIT_TYPE", Key: "OUT_OF_ROUTE", Value: "Luar Route", Description: "Kunjungan di luar route", IsActive: true},
			{GroupName: "NOTATION", Key: "NORMAL", Value: "Normal", Description: "Distribus normal", IsActive: true},
			{GroupName: "NOTATION", Key: "OOS", Value: "Out of Stock", Description: "Outlet kehabisan stok", IsActive: true},
			{GroupName: "NOTATION", Key: "WHOLESALE", Value: "Grosir", Description: "Outlet melayani grosir", IsActive: true},
			{GroupName: "STOCK_STATUS", Key: "DRAFT", Value: "Draft", Description: "Belum siap", IsActive: true},
			{GroupName: "STOCK_STATUS", Key: "READY", Value: "Ready", Description: "Siap ditarik Sales", IsActive: true},
			{GroupName: "STOCK_STATUS", Key: "PULLED", Value: "Pulled", Description: "Sudah ditarik Sales", IsActive: true},
			{GroupName: "STOCK_STATUS", Key: "CLOSED", Value: "Closed", Description: "Selesai", IsActive: true},
		}
		if err := db.Create(&params).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d params", len(params))
	}

	// 2. Seed Products (Minimal 5 SKU)
	var productCount int64
	if err := db.Model(&domain.Product{}).Count(&productCount).Error; err != nil {
		return err
	}
	if productCount == 0 {
		products := []domain.Product{
			{Name: "Djarum Super 12", SKU: "DJ-SUP-12", Price: 2500, UOMBal: 20, UOMSlf: 10, UOMBks: 12, IsActive: true},
			{Name: "LA Bold 16", SKU: "LA-BLD-16", Price: 2800, UOMBal: 20, UOMSlf: 10, UOMBks: 16, IsActive: true},
			{Name: "Gudang Garam Merah", SKU: "GG-MERAH", Price: 2200, UOMBal: 25, UOMSlf: 10, UOMBks: 12, IsActive: true},
			{Name: "Sampoerna Mild 16", SKU: "SAM-MLD-16", Price: 3200, UOMBal: 20, UOMSlf: 10, UOMBks: 16, IsActive: true},
			{Name: "Djarum Black 16", SKU: "DJ-BLK-16", Price: 3000, UOMBal: 20, UOMSlf: 10, UOMBks: 16, IsActive: true},
		}
		if err := db.Create(&products).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d products", len(products))
	}

	// 3. Seed Areas
	var areaCount int64
	if err := db.Model(&domain.Area{}).Count(&areaCount).Error; err != nil {
		return err
	}
	if areaCount == 0 {
		areas := []domain.Area{
			{Name: "Jabodetabek"},
			{Name: "Jawa Barat"},
		}
		if err := db.Create(&areas).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d areas", len(areas))
	}

	// 4. Seed Territories
	var territoryCount int64
	if err := db.Model(&domain.Territory{}).Count(&territoryCount).Error; err != nil {
		return err
	}
	if territoryCount == 0 {
		territories := []domain.Territory{
			{AreaID: 1, Name: "Jakarta Selatan"},
			{AreaID: 1, Name: "Jakarta Timur"},
		}
		if err := db.Create(&territories).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d territories", len(territories))
	}

	// 5. Seed Districts
	var districtCount int64
	if err := db.Model(&domain.District{}).Count(&districtCount).Error; err != nil {
		return err
	}
	if districtCount == 0 {
		districts := []domain.District{
			{TerritoryID: 1, Name: "District Kebayoran"},
			{TerritoryID: 1, Name: "District Cilandak"},
		}
		if err := db.Create(&districts).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d districts", len(districts))
	}

	// 6. Seed Routes
	var routeCount int64
	if err := db.Model(&domain.Route{}).Count(&routeCount).Error; err != nil {
		return err
	}
	if routeCount == 0 {
		routes := []domain.Route{
			{DistrictID: 1, Name: "Route 1 - Senin", DayOfWeek: 1},
			{DistrictID: 1, Name: "Route 2 - Selasa", DayOfWeek: 2},
			{DistrictID: 1, Name: "Route 3 - Rabu", DayOfWeek: 3},
			{DistrictID: 1, Name: "Route 4 - Kamis", DayOfWeek: 4},
			{DistrictID: 1, Name: "Route 5 - Jumat", DayOfWeek: 5},
		}
		if err := db.Create(&routes).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d routes", len(routes))
	}

	// 7. Seed Employees
	var employeeCount int64
	if err := db.Model(&domain.Employee{}).Count(&employeeCount).Error; err != nil {
		return err
	}
	if employeeCount == 0 {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte("password123"), bcrypt.DefaultCost)
		if err != nil {
			return err
		}
		passwordHash := string(hashedPassword)

		employees := []domain.Employee{
			{Name: "Budi Santoso", NIK: "1001", Username: "budi.spv", Password: passwordHash, Position: "SPV", Phone: "08123456789", Address: "Jl. Sudirman No. 1, Jakarta", IsActive: true},
			{Name: "Andi Wijaya", NIK: "2001", Username: "andi.sales", Password: passwordHash, Position: "SALES", Phone: "08129876543", Address: "Jl. Gatot Subroto No. 2, Jakarta", IsActive: true},
			{Name: "Rini Susanti", NIK: "2002", Username: "rini.sales", Password: passwordHash, Position: "SALES", Phone: "08131122334", Address: "Jl. Rasuna Said No. 3, Jakarta", IsActive: true},
		}
		if err := db.Create(&employees).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d employees", len(employees))
	}

	// 8. Seed Outlets (Minimal 10 Outlets)
	var outletCount int64
	if err := db.Model(&domain.Outlet{}).Count(&outletCount).Error; err != nil {
		return err
	}
	if outletCount == 0 {
		outlets := []domain.Outlet{
			{Name: "Toko Mawar", OwnerName: "Pak Joko", Phone: "08122334455", Address: "Kebayoran Lama, Jakarta Selatan", Lat: floatPtr(-6.2614), Lng: floatPtr(106.8106), Barcode: "OTL-0001", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Warung Melati", OwnerName: "Bu Sari", Phone: "08122334456", Address: "Kebayoran Baru, Jakarta Selatan", Lat: floatPtr(-6.2650), Lng: floatPtr(106.8120), Barcode: "OTL-0002", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Toko Sejahtera", OwnerName: "Pak Bowo", Phone: "08122334457", Address: "Cilandak, Jakarta Selatan", Lat: floatPtr(-6.2590), Lng: floatPtr(106.8090), Barcode: "OTL-0003", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Toko Jaya", OwnerName: "Pak Rudy", Phone: "08122334458", Address: "Blok M, Jakarta Selatan", Lat: floatPtr(-6.2440), Lng: floatPtr(106.8020), Barcode: "OTL-0004", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Warung Berkah", OwnerName: "Bu Aminah", Phone: "08122334459", Address: "Fatmawati, Jakarta Selatan", Lat: floatPtr(-6.2730), Lng: floatPtr(106.7970), Barcode: "OTL-0005", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Toko Makmur", OwnerName: "Pak Hendi", Phone: "08122334460", Address: "Pondok Indah, Jakarta Selatan", Lat: floatPtr(-6.2690), Lng: floatPtr(106.7820), Barcode: "OTL-0006", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Warung Sederhana", OwnerName: "Bu Marni", Phone: "08122334461", Address: "Gandaria, Jakarta Selatan", Lat: floatPtr(-6.2530), Lng: floatPtr(106.7910), Barcode: "OTL-0007", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Toko Utama", OwnerName: "Pak Andi", Phone: "08122334462", Address: "Kemang, Jakarta Selatan", Lat: floatPtr(-6.2720), Lng: floatPtr(106.8150), Barcode: "OTL-0008", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Warung Kita", OwnerName: "Bu Endang", Phone: "08122334463", Address: "Cipete, Jakarta Selatan", Lat: floatPtr(-6.2780), Lng: floatPtr(106.8050), Barcode: "OTL-0009", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
			{Name: "Toko Sentosa", OwnerName: "Pak Budi", Phone: "08122334464", Address: "Radio Dalam, Jakarta Selatan", Lat: floatPtr(-6.2510), Lng: floatPtr(106.7880), Barcode: "OTL-0010", OutletStatus: "ACTIVE", CallCycle: "WEEKLY", CreatedAt: time.Now(), UpdatedAt: time.Now()},
		}
		if err := db.Create(&outlets).Error; err != nil {
			return err
		}
		log.Printf("Seeded %d outlets", len(outlets))
	}

	log.Println("Seeding completed successfully!")
	return nil
}

func floatPtr(f float64) *float64 {
	return &f
}
