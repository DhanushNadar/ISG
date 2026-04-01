import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { HospitalDTO, DiseaseDTO, PatientDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  hospitals: HospitalDTO[] = [];
  diseases: DiseaseDTO[] = [];
  patients: PatientDTO[] = [];
  isLoading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    // Fetch overview stats
    this.apiService.getHospitals().subscribe(data => this.hospitals = data);
    this.apiService.getDiseases().subscribe(data => this.diseases = data);
    this.apiService.getPatients().subscribe(data => {
      this.patients = data;
      this.isLoading = false;
    });
  }

}
