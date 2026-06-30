import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';

@Component({
  selector: 'app-week-selector',
  templateUrl: './week-selector.component.html',
  styleUrls: ['./week-selector.component.scss']
})
export class WeekSelectorComponent implements OnInit {
  @Output() weekSelected = new EventEmitter<{ weekStart: string; weekEnd: string }>();

  selectedDate: Date = new Date();
  displayRange = '';

  ngOnInit(): void {
    this.selectWeek(this.selectedDate);
  }

  onDateChange(event: MatDatepickerInputEvent<Date>): void {
    if (event.value) {
      this.selectedDate = event.value;
      this.selectWeek(this.selectedDate);
    }
  }

  private selectWeek(date: Date): void {
    // Snap to Monday
    const monday = new Date(date);
    const day = monday.getDay();
    const diff = monday.getDate() - day + (day === 0 ? -6 : 1);
    monday.setDate(diff);
    monday.setHours(0, 0, 0, 0);

    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    sunday.setHours(23, 59, 59, 999);

    const weekStartStr = this.formatDate(monday);
    const weekEndStr = this.formatDate(sunday);

    this.displayRange = `${this.formatDisplayDate(monday)} - ${this.formatDisplayDate(sunday)}`;
    this.weekSelected.emit({ weekStart: weekStartStr, weekEnd: weekEndStr });
  }

  private formatDate(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private formatDisplayDate(d: Date): string {
    const day = String(d.getDate()).padStart(2, '0');
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
    const month = months[d.getMonth()];
    const year = d.getFullYear();
    return `${day} ${month} ${year}`;
  }
}
