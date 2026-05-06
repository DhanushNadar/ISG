import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { PatientProfileResponse, BloodCampDTO, BloodCampBookingDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-patient-portal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-portal.component.html',
  styleUrl: './patient-portal.component.css'
})
export class PatientPortalComponent implements OnInit {

  profile: PatientProfileResponse | null = null;
  isLoading = true;
  aadhaarNumber = '';

  // Report Submission State
  nextDisease: any = null;
  selectedFile: File | null = null;
  isSubmitting = false;
  mySubmissions: any[] = [];

  // Blood Camps State
  globalCamps: BloodCampDTO[] = [];
  myCampBookings: BloodCampBookingDTO[] = [];

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.aadhaarNumber = payload.sub || '';
      } catch (e) {
        console.error('Error decoding JWT', e);
      }
    }

    this.apiService.getPatientPortalProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching profile', err);
        this.isLoading = false;
      }
    });

    this.apiService.getNextDiseaseToClaim().subscribe(data => {
      this.nextDisease = data;
    });
    this.fetchMySubmissions();
    this.fetchGlobalCamps();
    this.fetchMyCampBookings();
  }

  fetchGlobalCamps(): void {
    this.apiService.getAllUpcomingCamps().subscribe({
      next: (data) => this.globalCamps = data,
      error: (err) => console.error('Error fetching global camps', err)
    });
  }

  fetchMyCampBookings(): void {
    this.apiService.getMyCampBookings().subscribe({
      next: (data) => this.myCampBookings = data,
      error: (err) => console.error('Error fetching my camp bookings', err)
    });
  }

  hasBooked(campId: number): boolean {
    return this.myCampBookings.some(b => b.campId === campId);
  }

  bookCamp(campId: number): void {
    if (this.profile?.eligibility !== 'ELIGIBLE') {
      alert('You are not eligible to donate blood at this time.');
      return;
    }
    
    this.apiService.bookCampSlot(campId).subscribe({
      next: (res) => {
        alert('Slot booked successfully! You can view your entry slip below.');
        this.fetchMyCampBookings();
      },
      error: (err) => {
        alert('Failed to book slot. You might have already booked it.');
      }
    });
  }

  fetchMySubmissions(): void {
    this.apiService.getMyMedicalReports().subscribe({
      next: (data) => this.mySubmissions = data,
      error: (err) => console.error('Error fetching submissions', err)
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        alert('File size exceeds 5MB limit.');
        event.target.value = null;
        this.selectedFile = null;
        return;
      }
      this.selectedFile = file;
    }
  }

  submitReport(): void {
    if (!this.nextDisease || !this.selectedFile) {
      alert('Please upload a file.');
      return;
    }

    this.isSubmitting = true;
    this.apiService.submitMedicalReport(this.selectedFile, this.nextDisease.id).subscribe({
      next: () => {
        alert('Report submitted successfully! Waiting for hospital approval.');
        this.selectedFile = null;
        this.isSubmitting = false;
        
        // Fetch the next available disease after successful submission
        this.apiService.getNextDiseaseToClaim().subscribe(data => {
          this.nextDisease = data;
        });
        
        this.fetchMySubmissions();
      },
      error: (err) => {
        alert('Failed to submit report. Please try again.');
        console.error(err);
        this.isSubmitting = false;
      }
    });
  }

  get maskedAadhaar(): string {
    if (!this.aadhaarNumber || this.aadhaarNumber.length !== 12) return 'XXXX XXXX XXXX';
    return `XXXX XXXX ${this.aadhaarNumber.substring(8)}`;
  }
}
