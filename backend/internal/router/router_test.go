package router_test

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"strconv"
	"testing"
	"time"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/domain"
	"my-salesmanship/internal/router"
	"my-salesmanship/internal/utils"

	"golang.org/x/crypto/bcrypt"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func TestAuthFlow(t *testing.T) {
	// 1. Connect to local Postgres database (exposed on port 5433 by Docker)
	dsn := "host=localhost user=postgres password=secretpassword dbname=salesmanship port=5433 sslmode=disable TimeZone=Asia/Jakarta"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		t.Fatalf("failed to connect database: %v", err)
	}

	// Run within a transaction so it rolls back changes
	tx := db.Begin()
	defer tx.Rollback()

	// Insert test user (use unique username and NIK to avoid unique key conflicts in transaction)
	hashedPassword, _ := bcrypt.GenerateFromPassword([]byte("password123"), bcrypt.DefaultCost)
	testUser := domain.Employee{
		Name:     "Test Sales",
		NIK:      "TS-TEST-001",
		Username: "test.sales.unique",
		Password: string(hashedPassword),
		Position: "SALES",
		IsActive: true,
	}
	if err := tx.Create(&testUser).Error; err != nil {
		t.Fatalf("failed to create test user: %v", err)
	}

	// Setup Config
	cfg := &config.Config{
		JWTSecret: "test_secret_key_123_test_secret_key_123",
		Port:      "8080",
	}

	// Setup Router with the transaction db
	r := router.SetupRouter(cfg, tx)

	// 2. Test Login (Success)
	loginBody, _ := json.Marshal(map[string]string{
		"username": "test.sales.unique",
		"password": "password123",
	})
	req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(loginBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("expected login status 200, got %d. Body: %s", w.Code, w.Body.String())
	}

	var loginResp domain.LoginResponse
	json.Unmarshal(w.Body.Bytes(), &loginResp)

	if loginResp.AccessToken == "" || loginResp.RefreshToken == "" {
		t.Fatal("expected access and refresh tokens, got empty")
	}

	// 3. Test Login (Failed)
	loginBodyFail, _ := json.Marshal(map[string]string{
		"username": "test.sales.unique",
		"password": "wrongpassword",
	})
	reqFail, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(loginBodyFail))
	reqFail.Header.Set("Content-Type", "application/json")
	wFail := httptest.NewRecorder()
	r.ServeHTTP(wFail, reqFail)

	if wFail.Code != http.StatusUnauthorized {
		t.Fatalf("expected unauthorized 401, got %d", wFail.Code)
	}

	// 4. Test Refresh Token
	refreshBody, _ := json.Marshal(map[string]string{
		"refresh_token": loginResp.RefreshToken,
	})
	reqRefresh, _ := http.NewRequest("POST", "/api/v1/auth/refresh", bytes.NewBuffer(refreshBody))
	reqRefresh.Header.Set("Content-Type", "application/json")
	wRefresh := httptest.NewRecorder()
	r.ServeHTTP(wRefresh, reqRefresh)

	if wRefresh.Code != http.StatusOK {
		t.Fatalf("expected refresh status 200, got %d. Body: %s", wRefresh.Code, wRefresh.Body.String())
	}

	var refreshResp map[string]string
	json.Unmarshal(wRefresh.Body.Bytes(), &refreshResp)
	if refreshResp["access_token"] == "" {
		t.Fatal("expected new access token, got empty")
	}

	// 5. Test Change Password (Protected)
	// Without Authorization Header
	pwBody, _ := json.Marshal(map[string]string{
		"old_password": "password123",
		"new_password": "newpassword123",
	})
	reqPWNoAuth, _ := http.NewRequest("PUT", "/api/v1/auth/password", bytes.NewBuffer(pwBody))
	reqPWNoAuth.Header.Set("Content-Type", "application/json")
	wPWNoAuth := httptest.NewRecorder()
	r.ServeHTTP(wPWNoAuth, reqPWNoAuth)

	if wPWNoAuth.Code != http.StatusUnauthorized {
		t.Fatalf("expected 401 Unauthorized for password change without token, got %d", wPWNoAuth.Code)
	}

	// With Authorization Header
	reqPW, _ := http.NewRequest("PUT", "/api/v1/auth/password", bytes.NewBuffer(pwBody))
	reqPW.Header.Set("Content-Type", "application/json")
	reqPW.Header.Set("Authorization", "Bearer "+loginResp.AccessToken)
	wPW := httptest.NewRecorder()
	r.ServeHTTP(wPW, reqPW)

	if wPW.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for password change, got %d. Body: %s", wPW.Code, wPW.Body.String())
	}

	// 6. Verify password change
	// Login with old password should fail
	reqOldLogin, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(loginBody))
	reqOldLogin.Header.Set("Content-Type", "application/json")
	wOldLogin := httptest.NewRecorder()
	r.ServeHTTP(wOldLogin, reqOldLogin)
	if wOldLogin.Code != http.StatusUnauthorized {
		t.Fatalf("expected old password login to fail with 401, got %d", wOldLogin.Code)
	}

	// Login with new password should succeed
	newLoginBody, _ := json.Marshal(map[string]string{
		"username": "test.sales.unique",
		"password": "newpassword123",
	})
	reqNewLogin, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(newLoginBody))
	reqNewLogin.Header.Set("Content-Type", "application/json")
	wNewLogin := httptest.NewRecorder()
	r.ServeHTTP(wNewLogin, reqNewLogin)
	if wNewLogin.Code != http.StatusOK {
		t.Fatalf("expected new password login to succeed with 200, got %d", wNewLogin.Code)
	}
}

func TestMasterAndTransactionFlow(t *testing.T) {
	// 1. Connect to local Postgres database (exposed on port 5433 by Docker)
	dsn := "host=localhost user=postgres password=secretpassword dbname=salesmanship port=5433 sslmode=disable TimeZone=Asia/Jakarta"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		t.Fatalf("failed to connect database: %v", err)
	}

	tx := db.Begin()
	defer tx.Rollback()

	// Setup Config
	cfg := &config.Config{
		JWTSecret: "test_secret_key_123_test_secret_key_123",
		Port:      "8080",
	}

	r := router.SetupRouter(cfg, tx)

	// Create test Spv (Supervisor)
	hashedPassword, _ := bcrypt.GenerateFromPassword([]byte("password123"), bcrypt.DefaultCost)
	spvUser := domain.Employee{
		Name:     "Test Spv",
		NIK:      "SPV-TEST-001",
		Username: "test.spv.unique",
		Password: string(hashedPassword),
		Position: "SPV",
		IsActive: true,
	}
	if err := tx.Create(&spvUser).Error; err != nil {
		t.Fatalf("failed to create test spv: %v", err)
	}

	// Create test Salesman
	salesUser := domain.Employee{
		Name:     "Test Sales",
		NIK:      "SLS-TEST-001",
		Username: "test.sales.unique2",
		Password: string(hashedPassword),
		Position: "SALES",
		IsActive: true,
	}
	if err := tx.Create(&salesUser).Error; err != nil {
		t.Fatalf("failed to create test sales: %v", err)
	}

	// Generate access tokens
	spvToken, _ := utils.GenerateAccessToken(spvUser.ID, spvUser.Username, spvUser.Position, cfg.JWTSecret)
	salesToken, _ := utils.GenerateAccessToken(salesUser.ID, salesUser.Username, salesUser.Position, cfg.JWTSecret)

	// 2. Test Role Guard (Cari Area - Sales should get 403, Spv should get 200)
	req1, _ := http.NewRequest("GET", "/api/v1/master/areas", nil)
	req1.Header.Set("Authorization", "Bearer "+salesToken)
	w1 := httptest.NewRecorder()
	r.ServeHTTP(w1, req1)
	if w1.Code != http.StatusForbidden {
		t.Errorf("expected 403 Forbidden for Sales accessing Area list, got %d", w1.Code)
	}

	req2, _ := http.NewRequest("GET", "/api/v1/master/areas", nil)
	req2.Header.Set("Authorization", "Bearer "+spvToken)
	w2 := httptest.NewRecorder()
	r.ServeHTTP(w2, req2)
	if w2.Code != http.StatusOK {
		t.Errorf("expected 200 OK for Spv accessing Area list, got %d", w2.Code)
	}

	// 3. CRUD Area (Spv)
	areaBody, _ := json.Marshal(map[string]string{"name": "Sumatra"})
	reqArea, _ := http.NewRequest("POST", "/api/v1/master/areas", bytes.NewBuffer(areaBody))
	reqArea.Header.Set("Content-Type", "application/json")
	reqArea.Header.Set("Authorization", "Bearer "+spvToken)
	wArea := httptest.NewRecorder()
	r.ServeHTTP(wArea, reqArea)
	if wArea.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Area creation, got %d. Body: %s", wArea.Code, wArea.Body.String())
	}
	var createdArea domain.Area
	json.Unmarshal(wArea.Body.Bytes(), &createdArea)
	if createdArea.ID == 0 || createdArea.Name != "Sumatra" {
		t.Fatalf("invalid created area payload: %v", createdArea)
	}

	// 4. CRUD Territory (Spv)
	territoryBody, _ := json.Marshal(map[string]interface{}{"area_id": createdArea.ID, "name": "Medan"})
	reqTerr, _ := http.NewRequest("POST", "/api/v1/master/territories", bytes.NewBuffer(territoryBody))
	reqTerr.Header.Set("Content-Type", "application/json")
	reqTerr.Header.Set("Authorization", "Bearer "+spvToken)
	wTerr := httptest.NewRecorder()
	r.ServeHTTP(wTerr, reqTerr)
	if wTerr.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Territory creation, got %d. Body: %s", wTerr.Code, wTerr.Body.String())
	}
	var createdTerritory domain.Territory
	json.Unmarshal(wTerr.Body.Bytes(), &createdTerritory)

	// 5. CRUD District (Spv)
	districtBody, _ := json.Marshal(map[string]interface{}{"territory_id": createdTerritory.ID, "name": "Medan Deli"})
	reqDist, _ := http.NewRequest("POST", "/api/v1/master/districts", bytes.NewBuffer(districtBody))
	reqDist.Header.Set("Content-Type", "application/json")
	reqDist.Header.Set("Authorization", "Bearer "+spvToken)
	wDist := httptest.NewRecorder()
	r.ServeHTTP(wDist, reqDist)
	if wDist.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for District creation, got %d. Body: %s", wDist.Code, wDist.Body.String())
	}
	var createdDistrict domain.District
	json.Unmarshal(wDist.Body.Bytes(), &createdDistrict)

	// 6. CRUD Route (Spv)
	routeBody, _ := json.Marshal(map[string]interface{}{"district_id": createdDistrict.ID, "name": "Deli Route 1", "day_of_week": 1})
	reqRoute, _ := http.NewRequest("POST", "/api/v1/master/routes", bytes.NewBuffer(routeBody))
	reqRoute.Header.Set("Content-Type", "application/json")
	reqRoute.Header.Set("Authorization", "Bearer "+spvToken)
	wRoute := httptest.NewRecorder()
	r.ServeHTTP(wRoute, reqRoute)
	if wRoute.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Route creation, got %d. Body: %s", wRoute.Code, wRoute.Body.String())
	}
	var createdRoute domain.Route
	json.Unmarshal(wRoute.Body.Bytes(), &createdRoute)

	// 7. CRUD Outlet (Spv)
	outletBody, _ := json.Marshal(map[string]interface{}{"name": "Toko Sumatra", "barcode": "OTL-SUM-001", "outlet_status": "ACTIVE"})
	reqOutlet, _ := http.NewRequest("POST", "/api/v1/master/outlets", bytes.NewBuffer(outletBody))
	reqOutlet.Header.Set("Content-Type", "application/json")
	reqOutlet.Header.Set("Authorization", "Bearer "+spvToken)
	wOutlet := httptest.NewRecorder()
	r.ServeHTTP(wOutlet, reqOutlet)
	if wOutlet.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Outlet creation, got %d. Body: %s", wOutlet.Code, wOutlet.Body.String())
	}
	var createdOutlet domain.Outlet
	json.Unmarshal(wOutlet.Body.Bytes(), &createdOutlet)

	// 8. Weekly Mapping
	// Spv mapping
	mappingSpvBody, _ := json.Marshal(map[string]interface{}{
		"spv_id":       spvUser.ID,
		"territory_id": createdTerritory.ID,
		"week_start":   "2026-06-29",
		"week_end":     "2026-07-05",
	})
	reqMapSpv, _ := http.NewRequest("POST", "/api/v1/mapping/spv", bytes.NewBuffer(mappingSpvBody))
	reqMapSpv.Header.Set("Content-Type", "application/json")
	reqMapSpv.Header.Set("Authorization", "Bearer "+spvToken)
	wMapSpv := httptest.NewRecorder()
	r.ServeHTTP(wMapSpv, reqMapSpv)
	if wMapSpv.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for MappingSpv, got %d. Body: %s", wMapSpv.Code, wMapSpv.Body.String())
	}

	// Sales mapping
	mappingSalesBody, _ := json.Marshal(map[string]interface{}{
		"spv_id":      spvUser.ID,
		"sales_id":    salesUser.ID,
		"district_id": createdDistrict.ID,
		"week_start":  "2026-06-29",
		"week_end":    "2026-07-05",
	})
	reqMapSales, _ := http.NewRequest("POST", "/api/v1/mapping/sales", bytes.NewBuffer(mappingSalesBody))
	reqMapSales.Header.Set("Content-Type", "application/json")
	reqMapSales.Header.Set("Authorization", "Bearer "+spvToken)
	wMapSales := httptest.NewRecorder()
	r.ServeHTTP(wMapSales, reqMapSales)
	if wMapSales.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for MappingSales, got %d. Body: %s", wMapSales.Code, wMapSales.Body.String())
	}

	// Outlet mapping
	mappingOutletBody, _ := json.Marshal(map[string]interface{}{
		"outlet_id":  createdOutlet.ID,
		"route_id":   createdRoute.ID,
		"week_start": "2026-06-29",
		"week_end":   "2026-07-05",
	})
	reqMapOutlet, _ := http.NewRequest("POST", "/api/v1/mapping/outlet", bytes.NewBuffer(mappingOutletBody))
	reqMapOutlet.Header.Set("Content-Type", "application/json")
	reqMapOutlet.Header.Set("Authorization", "Bearer "+spvToken)
	wMapOutlet := httptest.NewRecorder()
	r.ServeHTTP(wMapOutlet, reqMapOutlet)
	if wMapOutlet.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for MappingOutlet, got %d. Body: %s", wMapOutlet.Code, wMapOutlet.Body.String())
	}

	// 9. Stock Allocation
	// Seed one product first
	prod := domain.Product{
		Name:     "Sampoerna Mild 16",
		SKU:      "SAM-MLD-16-UNIQUE",
		Price:    3200,
		UOMBal:   20,
		UOMSlf:   10,
		UOMBks:   16,
		IsActive: true,
	}
	tx.Create(&prod)

	stockBody, _ := json.Marshal(map[string]interface{}{
		"sales_id":  salesUser.ID,
		"date_used": "2026-06-30",
		"items": []map[string]interface{}{
			{
				"product_id":   prod.ID,
				"qty_dus_init": 1,
				"qty_bal_init": 0,
				"qty_slf_init": 5,
				"qty_bks_init": 0,
			},
		},
	})
	reqStock, _ := http.NewRequest("POST", "/api/v1/stock/rokok", bytes.NewBuffer(stockBody))
	reqStock.Header.Set("Content-Type", "application/json")
	reqStock.Header.Set("Authorization", "Bearer "+spvToken)
	wStock := httptest.NewRecorder()
	r.ServeHTTP(wStock, reqStock)
	if wStock.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Stock allocation, got %d. Body: %s", wStock.Code, wStock.Body.String())
	}
	var createdStock domain.StockRokok
	json.Unmarshal(wStock.Body.Bytes(), &createdStock)

	// Update Stock Status to READY
	statusBody, _ := json.Marshal(map[string]string{"status": "READY"})
	reqStatus, _ := http.NewRequest("PUT", "/api/v1/stock/rokok/"+strconv.Itoa(int(createdStock.ID))+"/status", bytes.NewBuffer(statusBody))
	reqStatus.Header.Set("Content-Type", "application/json")
	reqStatus.Header.Set("Authorization", "Bearer "+spvToken)
	wStatus := httptest.NewRecorder()
	r.ServeHTTP(wStatus, reqStatus)
	if wStatus.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for Stock status update, got %d. Body: %s", wStatus.Code, wStatus.Body.String())
	}

	// 10. Sales Target CRUD
	targetBody, _ := json.Marshal(map[string]interface{}{
		"sales_id":   salesUser.ID,
		"route_id":   createdRoute.ID,
		"product_id": prod.ID,
		"target_qty": 150,
		"week_start": "2026-06-29",
		"week_end":   "2026-07-05",
	})
	reqTarget, _ := http.NewRequest("POST", "/api/v1/target", bytes.NewBuffer(targetBody))
	reqTarget.Header.Set("Content-Type", "application/json")
	reqTarget.Header.Set("Authorization", "Bearer "+spvToken)
	wTarget := httptest.NewRecorder()
	r.ServeHTTP(wTarget, reqTarget)
	if wTarget.Code != http.StatusCreated {
		t.Fatalf("expected 201 Created for Target creation, got %d. Body: %s", wTarget.Code, wTarget.Body.String())
	}

	// 11. Test Pull Data (Sales)
	reqPull, _ := http.NewRequest("GET", "/api/v1/pull", nil)
	reqPull.Header.Set("Authorization", "Bearer "+salesToken)
	wPull := httptest.NewRecorder()
	r.ServeHTTP(wPull, reqPull)
	if wPull.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for Pull Data, got %d. Body: %s", wPull.Code, wPull.Body.String())
	}

	var pullResp domain.PullResponse
	json.Unmarshal(wPull.Body.Bytes(), &pullResp)

	// Validate Pull Response
	if pullResp.Salesman.ID != salesUser.ID {
		t.Errorf("expected sales ID %d, got %d", salesUser.ID, pullResp.Salesman.ID)
	}
	if pullResp.Salesman.SpvName != spvUser.Name {
		t.Errorf("expected spv name %s, got %s", spvUser.Name, pullResp.Salesman.SpvName)
	}
	if len(pullResp.Outlets) != 1 || pullResp.Outlets[0].ID != createdOutlet.ID {
		t.Errorf("expected 1 outlet with ID %d, got %d", createdOutlet.ID, len(pullResp.Outlets))
	}
	if pullResp.StockRokok == nil || pullResp.StockRokok.Status != "PULLED" {
		t.Errorf("expected stock status PULLED, got %v", pullResp.StockRokok)
	}
	if len(pullResp.Targets) != 1 || pullResp.Targets[0].TargetQty != 150 {
		t.Errorf("expected target quantity 150, got %d", len(pullResp.Targets))
	}

	// 12. Test Upload Visit (Sales)
	uploadPayload := domain.UploadPayload{
		BatchID: "550e8400-e29b-41d4-a716-446655440000",
		Visit: domain.UploadVisitRequest{
			OutletID:  createdOutlet.ID,
			VisitNo:   1,
			VisitType: "NORMAL",
			Status:    "CLOSED",
			StartTime: time.Now(),
			CheckStocks: []domain.UploadCheckStockRequest{
				{
					ProductID: prod.ID,
					StockQty:  3,
				},
			},
			Sales: []domain.UploadSalesRequest{
				{
					SalesOrder: "SO-TEST-001",
					Details: []domain.UploadSalesDetailRequest{
						{
							ProductID: prod.ID,
							Qty:       2,
							Price:     25000,
							Total:     50000,
						},
					},
				},
			},
		},
	}
	uploadBody, _ := json.Marshal(uploadPayload)
	reqUpload, _ := http.NewRequest("POST", "/api/v1/upload/visit", bytes.NewBuffer(uploadBody))
	reqUpload.Header.Set("Content-Type", "application/json")
	reqUpload.Header.Set("Authorization", "Bearer "+salesToken)
	wUpload := httptest.NewRecorder()
	r.ServeHTTP(wUpload, reqUpload)
	if wUpload.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for Upload, got %d. Body: %s", wUpload.Code, wUpload.Body.String())
	}

	// Test Upload Duplicate (Conflict 409)
	wUploadDup := httptest.NewRecorder()
	reqUploadDup, _ := http.NewRequest("POST", "/api/v1/upload/visit", bytes.NewBuffer(uploadBody))
	reqUploadDup.Header.Set("Content-Type", "application/json")
	reqUploadDup.Header.Set("Authorization", "Bearer "+salesToken)
	r.ServeHTTP(wUploadDup, reqUploadDup)
	if wUploadDup.Code != http.StatusConflict {
		t.Fatalf("expected 409 Conflict for duplicate upload, got %d. Body: %s", wUploadDup.Code, wUploadDup.Body.String())
	}

	// 13. Test Reports (Supervisor only)
	// Test Role Guard: Salesman accessing reports must get 403 Forbidden
	reqRepSales, _ := http.NewRequest("GET", "/api/v1/report/visits", nil)
	reqRepSales.Header.Set("Authorization", "Bearer "+salesToken)
	wRepSales := httptest.NewRecorder()
	r.ServeHTTP(wRepSales, reqRepSales)
	if wRepSales.Code != http.StatusForbidden {
		t.Errorf("expected 403 Forbidden for Sales accessing report visits, got %d", wRepSales.Code)
	}

	// GET /report/visits (Spv)
	reqRepVisits, _ := http.NewRequest("GET", "/api/v1/report/visits?sales_id="+strconv.Itoa(int(salesUser.ID))+"&date=2026-06-30", nil)
	reqRepVisits.Header.Set("Authorization", "Bearer "+spvToken)
	wRepVisits := httptest.NewRecorder()
	r.ServeHTTP(wRepVisits, reqRepVisits)
	if wRepVisits.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for report visits, got %d. Body: %s", wRepVisits.Code, wRepVisits.Body.String())
	}
	var visitRep []domain.VisitReportItem
	json.Unmarshal(wRepVisits.Body.Bytes(), &visitRep)
	if len(visitRep) != 1 || visitRep[0].BatchID != "550e8400-e29b-41d4-a716-446655440000" {
		t.Errorf("invalid visit report data: %v", visitRep)
	}

	// GET /report/sales (Spv)
	reqRepSalesRec, _ := http.NewRequest("GET", "/api/v1/report/sales?sales_id="+strconv.Itoa(int(salesUser.ID))+"&start_date=2026-06-29&end_date=2026-07-05", nil)
	reqRepSalesRec.Header.Set("Authorization", "Bearer "+spvToken)
	wRepSalesRec := httptest.NewRecorder()
	r.ServeHTTP(wRepSalesRec, reqRepSalesRec)
	if wRepSalesRec.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for report sales, got %d. Body: %s", wRepSalesRec.Code, wRepSalesRec.Body.String())
	}
	var salesRep []domain.SalesReportItem
	json.Unmarshal(wRepSalesRec.Body.Bytes(), &salesRep)
	if len(salesRep) != 1 || salesRep[0].TotalQty != 2 || salesRep[0].TotalAmount != 50000 {
		t.Errorf("invalid sales report data: %v", salesRep)
	}

	// GET /report/stock (Spv)
	reqRepStock, _ := http.NewRequest("GET", "/api/v1/report/stock?sales_id="+strconv.Itoa(int(salesUser.ID))+"&date=2026-06-30", nil)
	reqRepStock.Header.Set("Authorization", "Bearer "+spvToken)
	wRepStock := httptest.NewRecorder()
	r.ServeHTTP(wRepStock, reqRepStock)
	if wRepStock.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for report stock, got %d. Body: %s", wRepStock.Code, wRepStock.Body.String())
	}
	var stockRep []domain.StockReportItem
	json.Unmarshal(wRepStock.Body.Bytes(), &stockRep)
	// Initial stock was: 1 DUS (20 BAL * 10 SLOP * 16 BKS) + 5 SLOP (5 * 16 BKS) = 3200 + 80 = 3280 bungkus.
	// Sold: 2 bungkus.
	// Current: 3278 bungkus.
	if len(stockRep) != 1 || stockRep[0].InitialBungkus != 3280 || stockRep[0].SoldBungkus != 2 || stockRep[0].CurrentBungkus != 3278 {
		t.Errorf("invalid stock report data: %v", stockRep)
	}

	// GET /report/achievement (Spv)
	reqRepAch, _ := http.NewRequest("GET", "/api/v1/report/achievement?sales_id="+strconv.Itoa(int(salesUser.ID))+"&date=2026-06-30", nil)
	reqRepAch.Header.Set("Authorization", "Bearer "+spvToken)
	wRepAch := httptest.NewRecorder()
	r.ServeHTTP(wRepAch, reqRepAch)
	if wRepAch.Code != http.StatusOK {
		t.Fatalf("expected 200 OK for report achievement, got %d. Body: %s", wRepAch.Code, wRepAch.Body.String())
	}
	var achRep []domain.AchievementReportItem
	json.Unmarshal(wRepAch.Body.Bytes(), &achRep)
	if len(achRep) != 1 || achRep[0].TargetQty != 150 || achRep[0].ActualQty != 2 || achRep[0].Percentage < 1.33 || achRep[0].Percentage > 1.34 {
		t.Errorf("invalid achievement report data: %v", achRep)
	}
}
