import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingSpvComponent } from './mapping-spv.component';

describe('MappingSpvComponent', () => {
  let component: MappingSpvComponent;
  let fixture: ComponentFixture<MappingSpvComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingSpvComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingSpvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
