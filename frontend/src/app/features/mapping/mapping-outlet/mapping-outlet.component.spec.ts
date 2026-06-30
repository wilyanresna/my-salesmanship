import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingOutletComponent } from './mapping-outlet.component';

describe('MappingOutletComponent', () => {
  let component: MappingOutletComponent;
  let fixture: ComponentFixture<MappingOutletComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingOutletComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingOutletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
