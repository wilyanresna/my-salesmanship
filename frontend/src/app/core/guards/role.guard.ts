import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;
  if (currentUser && currentUser.position === 'SPV') {
    return true;
  }

  // Redirect to unauthorized or login
  router.navigate(['/login']);
  return false;
};
