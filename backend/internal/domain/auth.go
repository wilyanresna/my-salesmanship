package domain

// LoginResponse defines the tokens and metadata returned after successful login.
type LoginResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	Position     string `json:"position"` // 'SPV' | 'SALES'
	Name         string `json:"name"`
}

// AuthUsecase defines the business logic contract for authentication.
type AuthUsecase interface {
	Login(username, password string) (*LoginResponse, error)
	RefreshToken(tokenStr string) (string, error)
	ChangePassword(userID uint, oldPassword, newPassword string) error
}
