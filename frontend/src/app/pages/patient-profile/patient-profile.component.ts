import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { PatientProfileResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-patient-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './patient-profile.component.html',
  styleUrl: './patient-profile.component.css'
})
export class PatientProfileComponent implements OnInit {

  aadhaar: string = '';
  profile: PatientProfileResponse | null = null;
  isLoading = true;
  error = '';
  newPassword = '';

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.aadhaar = this.route.snapshot.paramMap.get('aadhaar') || '';
    if (this.aadhaar) {
      this.fetchProfile();
    }
  }

  fetchProfile(): void {
    this.apiService.getPatientProfile(this.aadhaar).subscribe({
      next: (data) => {
        this.profile = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Profile not found.';
        this.isLoading = false;
      }
    });
  }

  markRecovered(patientDiseaseId: number) {
    if (!confirm('Are you sure you want to mark this disease as recovered?')) return;
    
    this.apiService.updateDiseaseStatus(patientDiseaseId, 'RECOVERED').subscribe({
      next: () => {
        // Reload profile to recalculate eligibility and update UI
        this.isLoading = true;
        this.fetchProfile();
      },
      error: (err) => {
        alert('Failed to update disease status.');
      }
    });
  }

  setPortalPassword() {
    if (!this.newPassword || this.newPassword.length < 6) {
      alert('Password must be at least 6 characters.');
      return;
    }
    this.apiService.setPortalPassword(this.aadhaar, this.newPassword).subscribe({
      next: () => {
        alert('Patient Portal password set successfully!');
        this.newPassword = '';
      },
      error: (err) => {
        alert('Failed to set password.');
        console.error(err);
      }
    });
  }

}
