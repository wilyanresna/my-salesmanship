import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent {
  @Input() isCollapsed = false;

  // Track submenu states
  expandedMenus: { [key: string]: boolean } = {
    master: false,
    mapping: false,
    reports: false
  };

  toggleSubmenu(menu: string): void {
    if (this.isCollapsed) {
      // If collapsed, we don't expand inside, we might show tooltips/popovers
      // But for simplicity in toggle, we can also expand the sidebar or just toggle the state
      this.expandedMenus[menu] = !this.expandedMenus[menu];
      return;
    }
    this.expandedMenus[menu] = !this.expandedMenus[menu];
  }

  isExpanded(menu: string): boolean {
    return this.expandedMenus[menu] && !this.isCollapsed;
  }
}
