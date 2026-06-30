package domain

// MasterRepository defines the data access contract for all master data entities.
type MasterRepository interface {
	// Area
	CreateArea(area *Area) error
	GetAreaByID(id uint) (*Area, error)
	ListAreas() ([]Area, error)
	UpdateArea(area *Area) error
	DeleteArea(id uint) error

	// Territory
	CreateTerritory(t *Territory) error
	GetTerritoryByID(id uint) (*Territory, error)
	ListTerritories() ([]Territory, error)
	UpdateTerritory(t *Territory) error
	DeleteTerritory(id uint) error

	// District
	CreateDistrict(d *District) error
	GetDistrictByID(id uint) (*District, error)
	ListDistricts() ([]District, error)
	UpdateDistrict(d *District) error
	DeleteDistrict(id uint) error

	// Route
	CreateRoute(r *Route) error
	GetRouteByID(id uint) (*Route, error)
	ListRoutes() ([]Route, error)
	UpdateRoute(r *Route) error
	DeleteRoute(id uint) error

	// Outlet
	CreateOutlet(o *Outlet) error
	GetOutletByID(id uint) (*Outlet, error)
	ListOutlets() ([]Outlet, error)
	UpdateOutlet(o *Outlet) error
	DeleteOutlet(id uint) error

	// Employee
	CreateEmployee(e *Employee) error
	GetEmployeeByID(id uint) (*Employee, error)
	ListEmployees() ([]Employee, error)
	UpdateEmployee(e *Employee) error
	DeleteEmployee(id uint) error

	// Product (Read-only)
	ListProducts() ([]Product, error)

	// Param (Read-only)
	ListParams() ([]Param, error)
}

// MasterUsecase defines the business logic contract for all master data entities.
type MasterUsecase interface {
	// Area
	CreateArea(name string) (*Area, error)
	GetArea(id uint) (*Area, error)
	ListAreas() ([]Area, error)
	UpdateArea(id uint, name string) (*Area, error)
	DeleteArea(id uint) error

	// Territory
	CreateTerritory(areaID uint, name string) (*Territory, error)
	GetTerritory(id uint) (*Territory, error)
	ListTerritories() ([]Territory, error)
	UpdateTerritory(id uint, areaID uint, name string) (*Territory, error)
	DeleteTerritory(id uint) error

	// District
	CreateDistrict(territoryID uint, name string) (*District, error)
	GetDistrict(id uint) (*District, error)
	ListDistricts() ([]District, error)
	UpdateDistrict(id uint, territoryID uint, name string) (*District, error)
	DeleteDistrict(id uint) error

	// Route
	CreateRoute(districtID uint, name string, dayOfWeek int16) (*Route, error)
	GetRoute(id uint) (*Route, error)
	ListRoutes() ([]Route, error)
	UpdateRoute(id uint, districtID uint, name string, dayOfWeek int16) (*Route, error)
	DeleteRoute(id uint) error

	// Outlet
	CreateOutlet(o *Outlet) (*Outlet, error)
	GetOutlet(id uint) (*Outlet, error)
	ListOutlets() ([]Outlet, error)
	UpdateOutlet(id uint, o *Outlet) (*Outlet, error)
	DeleteOutlet(id uint) error

	// Employee
	CreateEmployee(e *Employee) (*Employee, error)
	GetEmployee(id uint) (*Employee, error)
	ListEmployees() ([]Employee, error)
	UpdateEmployee(id uint, e *Employee) (*Employee, error)
	DeleteEmployee(id uint) error

	// Product
	ListProducts() ([]Product, error)

	// Param
	ListParams() ([]Param, error)
}
