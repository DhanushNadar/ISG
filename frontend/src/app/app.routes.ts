import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PatientDirectoryComponent } from './pages/patient-directory/patient-directory.component';
import { PatientProfileComponent } from './pages/patient-profile/patient-profile.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard } from './core/guards/auth.guard';

import { AddPatientComponent } from './pages/add-patient/add-patient.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { PatientPortalComponent } from './pages/patient-portal/patient-portal.component';

export const routes: Routes = [
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'admin', component: AdminDashboardComponent, canActivate: [authGuard] },
    { path: 'patients', component: PatientDirectoryComponent, canActivate: [authGuard] },
    { path: 'patients/add', component: AddPatientComponent, canActivate: [authGuard] },
    { path: 'patients/:aadhaar', component: PatientProfileComponent, canActivate: [authGuard] },
    { path: 'patient-portal', component: PatientPortalComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'dashboard' }
];
