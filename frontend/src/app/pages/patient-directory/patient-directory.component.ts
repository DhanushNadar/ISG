import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { PatientDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-patient-directory',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-directory.component.html',
  styleUrl: './patient-directory.component.css'
})
export class PatientDirectoryComponent implements OnInit {

  patients: PatientDTO[] = [];
  isLoading = true;

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.apiService.getPatients().subscribe(data => {
      this.patients = data;
      this.isLoading = false;
    });
  }

  viewProfile(aadhaar: string) {
    this.router.navigate(['/patients', aadhaar]);
  }
}
