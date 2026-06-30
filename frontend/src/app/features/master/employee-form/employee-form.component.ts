import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Employee } from '../../../core/services/employee.service';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.scss']
})
export class EmployeeFormComponent implements OnInit {
  employeeForm!: FormGroup;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EmployeeFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { employee?: Employee }
  ) {}

  ngOnInit(): void {
    const employee = this.data?.employee;
    this.isEditMode = !!employee;

    this.employeeForm = this.fb.group({
      name: [employee?.name || '', [Validators.required]],
      nik: [employee?.nik || '', [Validators.required]],
      username: [employee?.username || '', [Validators.required]],
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]],
      position: [employee?.position || 'SALES', [Validators.required]],
      phone: [employee?.phone || ''],
      address: [employee?.address || ''],
      is_active: [employee !== undefined ? employee.is_active : true]
    });
  }

  onSubmit(): void {
    if (this.employeeForm.invalid) {
      return;
    }

    const formVal = this.employeeForm.value;
    // On edit mode, if password is empty, remove it so we don't update it
    if (this.isEditMode && !formVal.password) {
      delete formVal.password;
    }

    this.dialogRef.close(formVal);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
