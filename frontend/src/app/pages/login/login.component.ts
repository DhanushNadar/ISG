import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginType: 'HOSPITAL' | 'PATIENT' = 'HOSPITAL';
  email = '';
  aadhaar = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';
    
    if (this.loginType === 'HOSPITAL') {
      if (!this.email || !this.password) {
        this.errorMessage = 'Please enter both email and password.';
        return;
      }
      this.isLoading = true;
      this.authService.login({ email: this.email, password: this.password }).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: () => {
          this.errorMessage = 'Invalid hospital credentials. Please try again.';
          this.isLoading = false;
        }
      });
    } else {
      if (!this.aadhaar || !this.password) {
        this.errorMessage = 'Please enter both Aadhaar number and password.';
        return;
      }
      this.isLoading = true;
      this.authService.patientLogin({ aadhaarNumber: this.aadhaar, password: this.password }).subscribe({
        next: () => this.router.navigate(['/patient-portal']),
        error: () => {
          this.errorMessage = 'Invalid Aadhaar or password. Please try again.';
          this.isLoading = false;
        }
      });
    }
  }
}
