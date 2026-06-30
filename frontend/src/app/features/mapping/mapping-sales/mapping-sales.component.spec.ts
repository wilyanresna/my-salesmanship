import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingSalesComponent } from './mapping-sales.component';

describe('MappingSalesComponent', () => {
  let component: MappingSalesComponent;
  let fixture: ComponentFixture<MappingSalesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingSalesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingSalesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
