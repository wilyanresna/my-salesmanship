package domain

type Param struct {
	ID          uint   `gorm:"primaryKey;column:id"`
	GroupName   string `gorm:"type:varchar(50);not null;index:idx_params_group;uniqueIndex:idx_params_group_key;column:group_name"`
	Key         string `gorm:"type:varchar(50);not null;uniqueIndex:idx_params_group_key;column:key"`
	Value       string `gorm:"type:varchar(200);not null;column:value"`
	Description string `gorm:"type:text;column:description"`
	IsActive    bool   `gorm:"type:boolean;not null;default:true;column:is_active"`
}

func (Param) TableName() string {
	return "params"
}
