import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { HospitalDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  hospitals: HospitalDTO[] = [];
  
  newHospital: HospitalDTO = {
    name: '',
    location: '',
    contactNumber: ''
  };

  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadHospitals();
  }

  loadHospitals() {
    this.apiService.getHospitals().subscribe(data => this.hospitals = data);
  }

  onSubmit() {
    if (!this.newHospital.name || !this.newHospital.location || !this.newHospital.contactNumber) {
      this.errorMessage = "All fields are required.";
      return;
    }
    
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.apiService.createHospital(this.newHospital).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        this.successMessage = `Hospital ${res.name} added successfully!`;
        this.newHospital = { name: '', location: '', contactNumber: '' };
        this.loadHospitals(); // Refresh list
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = "Failed to add hospital.";
      }
    });
  }
}
