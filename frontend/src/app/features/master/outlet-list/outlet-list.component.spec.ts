import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutletListComponent } from './outlet-list.component';

describe('OutletListComponent', () => {
  let component: OutletListComponent;
  let fixture: ComponentFixture<OutletListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OutletListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutletListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
