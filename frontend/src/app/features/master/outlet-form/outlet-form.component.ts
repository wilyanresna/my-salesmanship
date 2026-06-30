import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Outlet } from '../../../core/services/outlet.service';

@Component({
  selector: 'app-outlet-form',
  templateUrl: './outlet-form.component.html',
  styleUrls: ['./outlet-form.component.scss']
})
export class OutletFormComponent implements OnInit {
  outletForm!: FormGroup;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<OutletFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { outlet?: Outlet }
  ) {}

  ngOnInit(): void {
    const outlet = this.data?.outlet;
    this.isEditMode = !!outlet;

    this.outletForm = this.fb.group({
      name: [outlet?.name || '', [Validators.required]],
      owner_name: [outlet?.owner_name || ''],
      phone: [outlet?.phone || ''],
      address: [outlet?.address || ''],
      lat: [outlet?.lat || null, [Validators.pattern(/^-?\d+(\.\d+)?$/)]],
      lng: [outlet?.lng || null, [Validators.pattern(/^-?\d+(\.\d+)?$/)]],
      barcode: [outlet?.barcode || '', [Validators.required]],
      outlet_status: [outlet?.outlet_status || 'ACTIVE', [Validators.required]],
      call_cycle: [outlet?.call_cycle || 'WEEKLY', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.outletForm.invalid) {
      return;
    }

    const val = this.outletForm.value;
    if (val.lat !== null) {
      val.lat = parseFloat(val.lat);
    }
    if (val.lng !== null) {
      val.lng = parseFloat(val.lng);
    }

    this.dialogRef.close(val);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
