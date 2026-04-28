import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';
import { HospitalDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  email = '';
  password = '';
  role = 'HOSPITAL';
  hospitalId: number | null = null;
  hospitals: HospitalDTO[] = [];
  
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService, 
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit() {
    this.apiService.getHospitals().subscribe(data => this.hospitals = data);
  }

  onSubmit() {
    if (!this.email || !this.password) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }
    if (this.role === 'HOSPITAL' && !this.hospitalId) {
      this.errorMessage = 'Please select your associated Hospital Facility.';
      return;
    }
    
    this.isLoading = true;
    
    const payload: any = { email: this.email, password: this.password, role: this.role };
    if (this.role === 'HOSPITAL') {
      payload.hospitalId = this.hospitalId;
    }
    
    this.authService.register(payload).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration failed. Email might be taken.';
        this.isLoading = false;
      }
    });
  }
}
