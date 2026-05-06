import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { HospitalDTO, DiseaseDTO, PatientDTO, BloodCampDTO, BloodCampBookingDTO } from '../../core/models/api.models';
import * as L from 'leaflet';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {

  hospitals: HospitalDTO[] = [];
  diseases: DiseaseDTO[] = [];
  majorDiseases: DiseaseDTO[] = [];
  currentHospitalName: string = 'HOSPITAL';
  isPatientViewer: boolean = false;
  
  // Pending Medical Reports
  pendingReports: any[] = [];

  // Map
  private map: L.Map | null = null;

  // In-App Viewer State
  selectedFileUrl: SafeResourceUrl | null = null;
  rawFileUrl: string | null = null;
  selectedFileType: 'image' | 'pdf' | null = null;
  isLoadingFile: boolean = false;

  // Blood Camps State
  myCamps: BloodCampDTO[] = [];
  selectedCampBookings: BloodCampBookingDTO[] = [];
  selectedCamp: BloodCampDTO | null = null;
  isOrganizingCamp: boolean = false;
  newCamp = { title: '', location: '', date: '', time: '', description: '' };

  constructor(
    private apiService: ApiService, 
    private authService: AuthService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    // Get role and hospital name from JWT
    const token = this.authService.getToken();
    this.isPatientViewer = this.authService.getRole() === 'PATIENT';
    
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.currentHospitalName = payload.sub || 'HOSPITAL';
      } catch (e) {
        console.error('Error decoding JWT', e);
      }
      
      this.fetchPendingReports();
      if (!this.isPatientViewer) {
          this.fetchMyCamps();
      }
    }

    this.apiService.getHospitals().subscribe(data => this.hospitals = data);
    this.apiService.getDiseases().subscribe(data => {
      this.diseases = data;
      this.majorDiseases = data.filter(d => d.isMajor);
    });
  }

  fetchPendingReports(): void {
    this.apiService.getPendingMedicalReports().subscribe({
      next: (data) => this.pendingReports = data,
      error: (err) => console.error('Error fetching pending reports', err)
    });
  }

  approveReport(id: number): void {
    this.apiService.approveMedicalReport(id).subscribe({
      next: () => {
        alert('Report approved successfully.');
        this.fetchPendingReports();
      },
      error: (err) => alert('Failed to approve report.')
    });
  }

  rejectReport(id: number): void {
    this.apiService.rejectMedicalReport(id).subscribe({
      next: () => {
        alert('Report rejected.');
        this.fetchPendingReports();
      },
      error: (err) => alert('Failed to reject report.')
    });
  }

  viewReportFile(id: number): void {
    this.isLoadingFile = true;
    this.selectedFileUrl = null; // Open modal immediately showing loading state
    
    // Set raw URL for download button explicitly asking for attachment
    const token = this.authService.getToken();
    const baseUrl = window.location.hostname === 'localhost' ? 'http://localhost:8080/api' : 'https://isg-production.up.railway.app/api';
    this.rawFileUrl = `${baseUrl}/reports/${id}/file?token=${token}&download=true`;

    this.apiService.getMedicalReportFile(id).subscribe({
      next: (blob: Blob) => {
        let finalBlob = blob;
        let isImage = blob.type.startsWith('image');
        
        if (!isImage) {
          // Force PDF MIME type to ensure Chrome's native viewer doesn't reject application/octet-stream
          finalBlob = new Blob([blob], { type: 'application/pdf' });
        }
        
        const objectUrl = URL.createObjectURL(finalBlob);
        this.selectedFileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
        this.selectedFileType = isImage ? 'image' : 'pdf';
        this.isLoadingFile = false;
      },
      error: (err) => {
        console.error('Failed to load file', err);
        alert('Failed to load file.');
        this.closeModal();
      }
    });
  }

  closeModal(): void {
    this.selectedFileUrl = null;
    this.rawFileUrl = null;
    this.selectedFileType = null;
    this.isLoadingFile = false;
  }

  downloadFile(): void {
    if(this.rawFileUrl) {
      window.open(this.rawFileUrl, '_blank');
    }
  }

  // --- Blood Camp Methods ---
  fetchMyCamps(): void {
    this.apiService.getHospitalCamps().subscribe({
      next: (data) => this.myCamps = data,
      error: (err) => console.error('Error fetching hospital camps', err)
    });
  }

  toggleOrganizeCamp(): void {
    this.isOrganizingCamp = !this.isOrganizingCamp;
    if(!this.isOrganizingCamp) {
       this.newCamp = { title: '', location: '', date: '', time: '', description: '' };
    }
  }

  submitCamp(): void {
    if (!this.newCamp.title || !this.newCamp.location || !this.newCamp.date || !this.newCamp.time) {
      alert('Please fill all required fields');
      return;
    }
    this.apiService.createBloodCamp(this.newCamp).subscribe({
      next: (res) => {
        alert('Blood camp organized successfully!');
        this.fetchMyCamps();
        this.toggleOrganizeCamp();
      },
      error: (err) => alert('Failed to organize blood camp')
    });
  }

  viewCampBookings(camp: BloodCampDTO): void {
    if (this.selectedCamp?.id === camp.id) {
       this.selectedCamp = null;
       this.selectedCampBookings = [];
       return;
    }
    this.selectedCamp = camp;
    this.apiService.getHospitalCampBookings(camp.id).subscribe({
      next: (data) => this.selectedCampBookings = data,
      error: (err) => console.error('Error fetching bookings', err)
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.map) this.map.remove();
  }

  initMap() {
    this.map = L.map('radarMap', {
      center: [40.7128, -74.0060], // Mock center (NY)
      zoom: 12,
      zoomControl: false,
      attributionControl: false
    });

    // Dark themed map tiles (CartoDB Dark Matter)
    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      maxZoom: 19
    }).addTo(this.map);

    // Mock Hospitals
    const hospitals = [
      { lat: 40.7128, lng: -74.0060, name: 'Central General', status: 'stable' },
      { lat: 40.7300, lng: -73.9950, name: 'Mercy Clinic', status: 'stable' },
      { lat: 40.7000, lng: -74.0200, name: 'St. Judes', status: 'stable' },
    ];

    hospitals.forEach(h => {
      const color = '#00ff66'; // Stable green
      const circle = L.circleMarker([h.lat, h.lng], {
        radius: 8,
        fillColor: color,
        color: color,
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
      }).addTo(this.map!);
      circle.bindPopup(`<b>${h.name}</b><br>Status: Connected`);
    });
  }

}
