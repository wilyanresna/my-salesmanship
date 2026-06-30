package usecase

import (
	"errors"

	"my-salesmanship/internal/config"
	"my-salesmanship/internal/domain"
	"my-salesmanship/internal/utils"

	"golang.org/x/crypto/bcrypt"
)

type authUsecase struct {
	empRepo domain.EmployeeRepository
	cfg     *config.Config
}

// NewAuthUsecase creates a new instance of AuthUsecase.
func NewAuthUsecase(empRepo domain.EmployeeRepository, cfg *config.Config) domain.AuthUsecase {
	return &authUsecase{
		empRepo: empRepo,
		cfg:     cfg,
	}
}

// Login validates user credentials and returns access & refresh tokens.
func (u *authUsecase) Login(username, password string) (*domain.LoginResponse, error) {
	emp, err := u.empRepo.GetByUsername(username)
	if err != nil {
		return nil, errors.New("invalid username or password")
	}

	// Compare bcrypt password hash
	err = bcrypt.CompareHashAndPassword([]byte(emp.Password), []byte(password))
	if err != nil {
		return nil, errors.New("invalid username or password")
	}

	// Generate access and refresh tokens
	accessToken, err := utils.GenerateAccessToken(emp.ID, emp.Username, emp.Position, u.cfg.JWTSecret)
	if err != nil {
		return nil, err
	}

	refreshToken, err := utils.GenerateRefreshToken(emp.ID, emp.Username, emp.Position, u.cfg.JWTSecret)
	if err != nil {
		return nil, err
	}

	return &domain.LoginResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
		Position:     emp.Position,
		Name:         emp.Name,
	}, nil
}

// RefreshToken validates refresh token and generates a new access token.
func (u *authUsecase) RefreshToken(tokenStr string) (string, error) {
	claims, err := utils.ParseToken(tokenStr, u.cfg.JWTSecret)
	if err != nil {
		return "", errors.New("invalid refresh token")
	}

	// Ensure the employee still exists and is active
	emp, err := u.empRepo.GetByID(claims.UserID)
	if err != nil {
		return "", errors.New("user not found or inactive")
	}

	// Generate new access token
	newAccessToken, err := utils.GenerateAccessToken(emp.ID, emp.Username, emp.Position, u.cfg.JWTSecret)
	if err != nil {
		return "", err
	}

	return newAccessToken, nil
}

// ChangePassword verifies the old password and sets the new hashed password.
func (u *authUsecase) ChangePassword(userID uint, oldPassword, newPassword string) error {
	if newPassword == "" {
		return errors.New("new password cannot be empty")
	}

	emp, err := u.empRepo.GetByID(userID)
	if err != nil {
		return errors.New("user not found")
	}

	// Verify old password
	err = bcrypt.CompareHashAndPassword([]byte(emp.Password), []byte(oldPassword))
	if err != nil {
		return errors.New("invalid old password")
	}

	// Hash the new password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(newPassword), bcrypt.DefaultCost)
	if err != nil {
		return err
	}

	return u.empRepo.UpdatePassword(userID, string(hashedPassword))
}
