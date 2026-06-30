import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AchievementReportComponent } from './achievement-report.component';

describe('AchievementReportComponent', () => {
  let component: AchievementReportComponent;
  let fixture: ComponentFixture<AchievementReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AchievementReportComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AchievementReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
