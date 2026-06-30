package router

import (
	"net/http"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/handler"
	"my-salesmanship/internal/middleware"
	"my-salesmanship/internal/repository"
	"my-salesmanship/internal/usecase"

	"github.com/gin-gonic/gin"
	"golang.org/x/time/rate"
	"gorm.io/gorm"
)

// SetupRouter initializes the Gin engine with all global middlewares and routes.
func SetupRouter(cfg *config.Config, db *gorm.DB) *gin.Engine {
	gin.SetMode(gin.ReleaseMode)

	r := gin.New()

	// Global Middlewares
	r.Use(gin.Recovery())
	r.Use(middleware.Logger())
	r.Use(middleware.CORS())

	// IP-based Rate Limiter (20 requests per second, burst of 50)
	limiter := middleware.NewIPRateLimiter(rate.Limit(20), 50)
	r.Use(middleware.RateLimit(limiter))

	// Initialize Dependencies
	empRepo := repository.NewEmployeeRepository(db)
	authUseCase := usecase.NewAuthUsecase(empRepo, cfg)
	authHandler := handler.NewAuthHandler(authUseCase)

	masterRepo := repository.NewMasterRepository(db)
	masterUseCase := usecase.NewMasterUsecase(masterRepo)
	masterHandler := handler.NewMasterHandler(masterUseCase)

	mappingRepo := repository.NewMappingRepository(db)
	mappingUseCase := usecase.NewMappingUsecase(mappingRepo)
	mappingHandler := handler.NewMappingHandler(mappingUseCase)

	stockTargetRepo := repository.NewStockTargetRepository(db)
	stockTargetUseCase := usecase.NewStockTargetUsecase(stockTargetRepo, stockTargetRepo)
	stockTargetHandler := handler.NewStockTargetHandler(stockTargetUseCase, stockTargetUseCase)

	pullRepo := repository.NewPullRepository(db)
	pullUseCase := usecase.NewPullUsecase(pullRepo)
	pullHandler := handler.NewPullHandler(pullUseCase)

	uploadRepo := repository.NewTransactionRepository(db)
	uploadUseCase := usecase.NewUploadUsecase(uploadRepo)
	uploadHandler := handler.NewUploadHandler(uploadUseCase)

	reportRepo := repository.NewReportRepository(db)
	reportUseCase := usecase.NewReportUsecase(reportRepo)
	reportHandler := handler.NewReportHandler(reportUseCase)

	// Base API Group
	api := r.Group("/api")
	{
		api.GET("/health", func(c *gin.Context) {
			sqlDB, err := db.DB()
			dbStatus := "connected"
			if err != nil || sqlDB.Ping() != nil {
				dbStatus = "disconnected"
			}

			c.JSON(http.StatusOK, gin.H{
				"status":   "healthy",
				"database": dbStatus,
			})
		})

		v1 := api.Group("/v1")
		{
			// Public Auth Routes
			auth := v1.Group("/auth")
			{
				auth.POST("/login", authHandler.Login)
				auth.POST("/refresh", authHandler.Refresh)

				// Protected Auth Routes
				auth.Use(middleware.JWTAuth(cfg))
				{
					auth.PUT("/password", authHandler.ChangePassword)
				}
			}

			// Protected Group
			protected := v1.Group("")
			protected.Use(middleware.JWTAuth(cfg))
			{
				// Master Data (Spv Only)
				masterSpv := protected.Group("/master")
				masterSpv.Use(middleware.RoleSpv())
				{
					// Employees CRUD
					masterSpv.GET("/employees", masterHandler.ListEmployees)
					masterSpv.POST("/employees", masterHandler.CreateEmployee)
					masterSpv.GET("/employees/:id", masterHandler.GetEmployee)
					masterSpv.PUT("/employees/:id", masterHandler.UpdateEmployee)
					masterSpv.DELETE("/employees/:id", masterHandler.DeleteEmployee)

					// Areas CRUD
					masterSpv.GET("/areas", masterHandler.ListAreas)
					masterSpv.POST("/areas", masterHandler.CreateArea)
					masterSpv.GET("/areas/:id", masterHandler.GetArea)
					masterSpv.PUT("/areas/:id", masterHandler.UpdateArea)
					masterSpv.DELETE("/areas/:id", masterHandler.DeleteArea)

					// Territories CRUD
					masterSpv.GET("/territories", masterHandler.ListTerritories)
					masterSpv.POST("/territories", masterHandler.CreateTerritory)
					masterSpv.GET("/territories/:id", masterHandler.GetTerritory)
					masterSpv.PUT("/territories/:id", masterHandler.UpdateTerritory)
					masterSpv.DELETE("/territories/:id", masterHandler.DeleteTerritory)

					// Districts CRUD
					masterSpv.GET("/districts", masterHandler.ListDistricts)
					masterSpv.POST("/districts", masterHandler.CreateDistrict)
					masterSpv.GET("/districts/:id", masterHandler.GetDistrict)
					masterSpv.PUT("/districts/:id", masterHandler.UpdateDistrict)
					masterSpv.DELETE("/districts/:id", masterHandler.DeleteDistrict)

					// Routes CRUD
					masterSpv.GET("/routes", masterHandler.ListRoutes)
					masterSpv.POST("/routes", masterHandler.CreateRoute)
					masterSpv.GET("/routes/:id", masterHandler.GetRoute)
					masterSpv.PUT("/routes/:id", masterHandler.UpdateRoute)
					masterSpv.DELETE("/routes/:id", masterHandler.DeleteRoute)

					// Outlets CRUD
					masterSpv.GET("/outlets", masterHandler.ListOutlets)
					masterSpv.POST("/outlets", masterHandler.CreateOutlet)
					masterSpv.GET("/outlets/:id", masterHandler.GetOutlet)
					masterSpv.PUT("/outlets/:id", masterHandler.UpdateOutlet)
					masterSpv.DELETE("/outlets/:id", masterHandler.DeleteOutlet)
				}

				// Master Data (Read-only for all roles)
				protected.GET("/master/products", masterHandler.ListProducts)
				protected.GET("/master/params", masterHandler.ListParams)

				// Weekly Mapping (Spv Only)
				mapping := protected.Group("/mapping")
				mapping.Use(middleware.RoleSpv())
				{
					mapping.GET("/spv", mappingHandler.ListMappingSpv)
					mapping.POST("/spv", mappingHandler.CreateMappingSpv)

					mapping.GET("/sales", mappingHandler.ListMappingSales)
					mapping.POST("/sales", mappingHandler.CreateMappingSales)

					mapping.GET("/outlet", mappingHandler.ListMappingOutlet)
					mapping.POST("/outlet", mappingHandler.CreateMappingOutlet)
				}

				// Stock allocation
				// GET is allowed for both, POST and PUT status are Spv only
				protected.GET("/stock/rokok", stockTargetHandler.ListStocks)
				protected.GET("/stock/rokok/:id", stockTargetHandler.GetStockDetail)

				stockSpv := protected.Group("/stock/rokok")
				stockSpv.Use(middleware.RoleSpv())
				{
					stockSpv.POST("", stockTargetHandler.CreateStock)
					stockSpv.PUT("/:id/status", stockTargetHandler.UpdateStockStatus)
				}

				// Sales Target (Spv Only)
				targetSpv := protected.Group("/target")
				targetSpv.Use(middleware.RoleSpv())
				{
					targetSpv.GET("", stockTargetHandler.ListTargets)
					targetSpv.POST("", stockTargetHandler.CreateTarget)
					targetSpv.GET("/:id", stockTargetHandler.GetTarget)
					targetSpv.PUT("/:id", stockTargetHandler.UpdateTarget)
					targetSpv.DELETE("/:id", stockTargetHandler.DeleteTarget)
				}

				// Pull Endpoint (Sales Only)
				protected.GET("/pull", middleware.RoleSales(), pullHandler.PullData)

				// Upload Endpoint (Sales Only)
				protected.POST("/upload/visit", middleware.RoleSales(), uploadHandler.UploadVisit)

				// Report (Spv Only)
				reportSpv := protected.Group("/report")
				reportSpv.Use(middleware.RoleSpv())
				{
					reportSpv.GET("/visits", reportHandler.GetVisitsReport)
					reportSpv.GET("/sales", reportHandler.GetSalesReport)
					reportSpv.GET("/stock", reportHandler.GetStockReport)
					reportSpv.GET("/achievement", reportHandler.GetAchievementReport)
				}
			}
		}
	}

	return r
}


