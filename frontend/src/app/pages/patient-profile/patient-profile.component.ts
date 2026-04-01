import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { PatientProfileResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-patient-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './patient-profile.component.html',
  styleUrl: './patient-profile.component.css'
})
export class PatientProfileComponent implements OnInit {

  aadhaar: string = '';
  profile: PatientProfileResponse | null = null;
  isLoading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService
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

}
