import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutletFormComponent } from './outlet-form.component';

describe('OutletFormComponent', () => {
  let component: OutletFormComponent;
  let fixture: ComponentFixture<OutletFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OutletFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutletFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
