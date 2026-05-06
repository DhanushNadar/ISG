import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('jwt_token');

  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role;
      const path = route.routeConfig?.path;
      const subject = payload.sub || '';
      
      const isAadhaar = /^\d{12}$/.test(subject);

      // Individual Patient (Aadhaar login) goes to /patient-portal
      if (role === 'PATIENT' && isAadhaar && path !== 'patient-portal') {
        router.navigate(['/patient-portal']);
        return false;
      }
      
      // Hospital Patient Viewer (Email login) goes to /dashboard
      if (role === 'PATIENT' && !isAadhaar && path === 'patient-portal') {
        router.navigate(['/dashboard']);
        return false;
      }
      
      if (role === 'HOSPITAL' && path === 'patient-portal') {
        router.navigate(['/dashboard']);
        return false;
      }
      
      return true;
    } catch (e) {
      router.navigate(['/login']);
      return false;
    }
  } else {
    router.navigate(['/login']);
    return false;
  }
};
