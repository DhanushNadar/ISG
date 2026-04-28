import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { PatientDTO, DiseaseDTO, PatientDiseaseDTO } from '../../core/models/api.models';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-add-patient',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-patient.component.html',
  styleUrl: './add-patient.component.css'
})
export class AddPatientComponent implements OnInit {
  
  patient: PatientDTO = {
    aadhaarNumber: '',
    name: '',
    age: 18,
    gender: 'Male',
    bloodGroup: 'O+',
    phone: '',
    email: ''
  };

  isSubmitting = false;
  error = '';
  
  diseases: DiseaseDTO[] = [];
  selectedDiseases: string[] = []; // array of disease IDs bound to select-multiple

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit() {
    this.apiService.getDiseases().subscribe(data => this.diseases = data);
  }

  onSubmit() {
    this.isSubmitting = true;
    this.error = '';

    if (this.patient.aadhaarNumber.length !== 12) {
      this.error = "Aadhaar number must be exactly 12 digits.";
      this.isSubmitting = false;
      return;
    }

    this.apiService.createPatient(this.patient).subscribe({
      next: (res) => {
        if (!res.id) {
            this.isSubmitting = false;
            this.router.navigate(['/patients']);
            return;
        }
        
        // If diseases are selected, map them now
        if (this.selectedDiseases && this.selectedDiseases.length > 0) {
           const requests = this.selectedDiseases.map(dId => {
             const mapping: PatientDiseaseDTO = {
               patientId: res.id!,
               diseaseId: parseInt(dId, 10),
               diagnosedDate: new Date().toISOString().split('T')[0],
               status: 'ACTIVE',
               isCurrent: true
             };
             return this.apiService.assignDisease(mapping).pipe(catchError(e => of(null)));
           });
           
           forkJoin(requests).subscribe(() => {
             this.isSubmitting = false;
             this.router.navigate(['/patients']);
           });
        } else {
           this.isSubmitting = false;
           this.router.navigate(['/patients']);
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        if (err.error && err.error.error) {
          this.error = err.error.error;
        } else {
          this.error = "Failed to register patient. Please check your inputs.";
        }
      }
    });
  }

  cancel() {
    this.router.navigate(['/patients']);
  }
}
