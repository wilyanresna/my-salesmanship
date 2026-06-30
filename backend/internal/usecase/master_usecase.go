package usecase

import (
	"errors"

	"my-salesmanship/internal/domain"

	"golang.org/x/crypto/bcrypt"
)

type masterUsecase struct {
	repo domain.MasterRepository
}

// NewMasterUsecase creates a new instance of MasterUsecase.
func NewMasterUsecase(repo domain.MasterRepository) domain.MasterUsecase {
	return &masterUsecase{
		repo: repo,
	}
}

// Area
func (u *masterUsecase) CreateArea(name string) (*domain.Area, error) {
	if name == "" {
		return nil, errors.New("area name is required")
	}
	area := &domain.Area{Name: name}
	if err := u.repo.CreateArea(area); err != nil {
		return nil, err
	}
	return area, nil
}

func (u *masterUsecase) GetArea(id uint) (*domain.Area, error) {
	return u.repo.GetAreaByID(id)
}

func (u *masterUsecase) ListAreas() ([]domain.Area, error) {
	return u.repo.ListAreas()
}

func (u *masterUsecase) UpdateArea(id uint, name string) (*domain.Area, error) {
	if name == "" {
		return nil, errors.New("area name is required")
	}
	area, err := u.repo.GetAreaByID(id)
	if err != nil {
		return nil, err
	}
	area.Name = name
	if err := u.repo.UpdateArea(area); err != nil {
		return nil, err
	}
	return area, nil
}

func (u *masterUsecase) DeleteArea(id uint) error {
	return u.repo.DeleteArea(id)
}

// Territory
func (u *masterUsecase) CreateTerritory(areaID uint, name string) (*domain.Territory, error) {
	if name == "" {
		return nil, errors.New("territory name is required")
	}
	t := &domain.Territory{AreaID: areaID, Name: name}
	if err := u.repo.CreateTerritory(t); err != nil {
		return nil, err
	}
	return t, nil
}

func (u *masterUsecase) GetTerritory(id uint) (*domain.Territory, error) {
	return u.repo.GetTerritoryByID(id)
}

func (u *masterUsecase) ListTerritories() ([]domain.Territory, error) {
	return u.repo.ListTerritories()
}

func (u *masterUsecase) UpdateTerritory(id uint, areaID uint, name string) (*domain.Territory, error) {
	if name == "" {
		return nil, errors.New("territory name is required")
	}
	t, err := u.repo.GetTerritoryByID(id)
	if err != nil {
		return nil, err
	}
	t.AreaID = areaID
	t.Name = name
	if err := u.repo.UpdateTerritory(t); err != nil {
		return nil, err
	}
	return t, nil
}

func (u *masterUsecase) DeleteTerritory(id uint) error {
	return u.repo.DeleteTerritory(id)
}

// District
func (u *masterUsecase) CreateDistrict(territoryID uint, name string) (*domain.District, error) {
	if name == "" {
		return nil, errors.New("district name is required")
	}
	d := &domain.District{TerritoryID: territoryID, Name: name}
	if err := u.repo.CreateDistrict(d); err != nil {
		return nil, err
	}
	return d, nil
}

func (u *masterUsecase) GetDistrict(id uint) (*domain.District, error) {
	return u.repo.GetDistrictByID(id)
}

func (u *masterUsecase) ListDistricts() ([]domain.District, error) {
	return u.repo.ListDistricts()
}

func (u *masterUsecase) UpdateDistrict(id uint, territoryID uint, name string) (*domain.District, error) {
	if name == "" {
		return nil, errors.New("district name is required")
	}
	d, err := u.repo.GetDistrictByID(id)
	if err != nil {
		return nil, err
	}
	d.TerritoryID = territoryID
	d.Name = name
	if err := u.repo.UpdateDistrict(d); err != nil {
		return nil, err
	}
	return d, nil
}

func (u *masterUsecase) DeleteDistrict(id uint) error {
	return u.repo.DeleteDistrict(id)
}

// Route
func (u *masterUsecase) CreateRoute(districtID uint, name string, dayOfWeek int16) (*domain.Route, error) {
	if name == "" {
		return nil, errors.New("route name is required")
	}
	if dayOfWeek < 1 || dayOfWeek > 7 {
		return nil, errors.New("day of week must be between 1 and 7")
	}
	rt := &domain.Route{DistrictID: districtID, Name: name, DayOfWeek: dayOfWeek}
	if err := u.repo.CreateRoute(rt); err != nil {
		return nil, err
	}
	return rt, nil
}

func (u *masterUsecase) GetRoute(id uint) (*domain.Route, error) {
	return u.repo.GetRouteByID(id)
}

func (u *masterUsecase) ListRoutes() ([]domain.Route, error) {
	return u.repo.ListRoutes()
}

func (u *masterUsecase) UpdateRoute(id uint, districtID uint, name string, dayOfWeek int16) (*domain.Route, error) {
	if name == "" {
		return nil, errors.New("route name is required")
	}
	if dayOfWeek < 1 || dayOfWeek > 7 {
		return nil, errors.New("day of week must be between 1 and 7")
	}
	rt, err := u.repo.GetRouteByID(id)
	if err != nil {
		return nil, err
	}
	rt.DistrictID = districtID
	rt.Name = name
	rt.DayOfWeek = dayOfWeek
	if err := u.repo.UpdateRoute(rt); err != nil {
		return nil, err
	}
	return rt, nil
}

func (u *masterUsecase) DeleteRoute(id uint) error {
	return u.repo.DeleteRoute(id)
}

// Outlet
func (u *masterUsecase) CreateOutlet(o *domain.Outlet) (*domain.Outlet, error) {
	if o.Name == "" {
		return nil, errors.New("outlet name is required")
	}
	if err := u.repo.CreateOutlet(o); err != nil {
		return nil, err
	}
	return o, nil
}

func (u *masterUsecase) GetOutlet(id uint) (*domain.Outlet, error) {
	return u.repo.GetOutletByID(id)
}

func (u *masterUsecase) ListOutlets() ([]domain.Outlet, error) {
	return u.repo.ListOutlets()
}

func (u *masterUsecase) UpdateOutlet(id uint, o *domain.Outlet) (*domain.Outlet, error) {
	existing, err := u.repo.GetOutletByID(id)
	if err != nil {
		return nil, err
	}
	existing.Name = o.Name
	existing.OwnerName = o.OwnerName
	existing.Phone = o.Phone
	existing.Address = o.Address
	existing.Lat = o.Lat
	existing.Lng = o.Lng
	existing.Barcode = o.Barcode
	existing.OutletStatus = o.OutletStatus
	existing.CallCycle = o.CallCycle

	if err := u.repo.UpdateOutlet(existing); err != nil {
		return nil, err
	}
	return existing, nil
}

func (u *masterUsecase) DeleteOutlet(id uint) error {
	return u.repo.DeleteOutlet(id)
}

// Employee
func (u *masterUsecase) CreateEmployee(e *domain.Employee) (*domain.Employee, error) {
	if e.Username == "" || e.Password == "" {
		return nil, errors.New("username and password are required")
	}
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(e.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}
	e.Password = string(hashedPassword)
	if err := u.repo.CreateEmployee(e); err != nil {
		return nil, err
	}
	return e, nil
}

func (u *masterUsecase) GetEmployee(id uint) (*domain.Employee, error) {
	return u.repo.GetEmployeeByID(id)
}

func (u *masterUsecase) ListEmployees() ([]domain.Employee, error) {
	return u.repo.ListEmployees()
}

func (u *masterUsecase) UpdateEmployee(id uint, e *domain.Employee) (*domain.Employee, error) {
	existing, err := u.repo.GetEmployeeByID(id)
	if err != nil {
		return nil, err
	}
	existing.Name = e.Name
	existing.NIK = e.NIK
	existing.Username = e.Username
	existing.Position = e.Position
	existing.Phone = e.Phone
	existing.Address = e.Address
	existing.IsActive = e.IsActive

	if e.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(e.Password), bcrypt.DefaultCost)
		if err != nil {
			return nil, err
		}
		existing.Password = string(hashedPassword)
	}

	if err := u.repo.UpdateEmployee(existing); err != nil {
		return nil, err
	}
	return existing, nil
}

func (u *masterUsecase) DeleteEmployee(id uint) error {
	return u.repo.DeleteEmployee(id)
}

// Product
func (u *masterUsecase) ListProducts() ([]domain.Product, error) {
	return u.repo.ListProducts()
}

// Param
func (u *masterUsecase) ListParams() ([]domain.Param, error) {
	return u.repo.ListParams()
}
