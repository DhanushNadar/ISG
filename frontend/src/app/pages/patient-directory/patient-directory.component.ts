import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { PatientDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-patient-directory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-directory.component.html',
  styleUrl: './patient-directory.component.css'
})
export class PatientDirectoryComponent implements OnInit {

  patients: PatientDTO[] = [];
  isLoading = true;
  
  selectedBloodGroup = 'O-';
  isBroadcasting = false;
  broadcastMessage = '';

  constructor(private apiService: ApiService, private router: Router, public authService: AuthService) {}

  ngOnInit(): void {
    this.apiService.getPatients().subscribe(data => {
      this.patients = data;
      this.isLoading = false;
    });
  }

  viewProfile(aadhaar: string) {
    this.router.navigate(['/patients', aadhaar]);
  }

  goToAddPatient() {
    this.router.navigate(['/patients/add']);
  }

  triggerBroadcast() {
    this.isBroadcasting = true;
    this.broadcastMessage = '';
    // The backend now securely reads the hospital ID from the JWT token!
    this.apiService.broadcastEmergency(this.selectedBloodGroup).subscribe({
      next: (res) => {
        this.isBroadcasting = false;
        this.broadcastMessage = res.message;
        setTimeout(() => this.broadcastMessage = '', 5000);
      },
      error: (err) => {
        this.isBroadcasting = false;
        this.broadcastMessage = 'Error sending broadcast. Check backend logs.';
        setTimeout(() => this.broadcastMessage = '', 5000);
      }
    });
  }
}
