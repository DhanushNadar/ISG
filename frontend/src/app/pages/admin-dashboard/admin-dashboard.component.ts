import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { HospitalDTO } from '../../core/models/api.models';
import * as L from 'leaflet';

const iconRetinaUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png';
const iconUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png';
const shadowUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png';
const iconDefault = L.icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});
L.Marker.prototype.options.icon = iconDefault;

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  hospitals: HospitalDTO[] = [];
  
  newHospital: HospitalDTO = {
    name: '',
    location: '',
    contactNumber: '',
    latitude: 19.0760, // Default to Mumbai
    longitude: 72.8777
  };

  private pickerMap!: L.Map;
  private pickerMarker!: L.Marker;
  private networkMap!: L.Map;

  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadHospitals();
  }

  loadHospitals() {
    this.apiService.getHospitals().subscribe(data => {
      this.hospitals = data;
      this.refreshNetworkMap();
    });
  }

  ngAfterViewInit() {
    this.initPickerMap();
    this.initNetworkMap();
  }

  private initPickerMap() {
    this.pickerMap = L.map('picker-map').setView([this.newHospital.latitude!, this.newHospital.longitude!], 10);
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.pickerMap);

    this.pickerMarker = L.marker([this.newHospital.latitude!, this.newHospital.longitude!], { draggable: true }).addTo(this.pickerMap);
    
    this.pickerMarker.on('dragend', (event) => {
      const position = event.target.getLatLng();
      this.newHospital.latitude = position.lat;
      this.newHospital.longitude = position.lng;
    });

    this.pickerMap.on('click', (event: L.LeafletMouseEvent) => {
      const latlng = event.latlng;
      this.pickerMarker.setLatLng(latlng);
      this.newHospital.latitude = latlng.lat;
      this.newHospital.longitude = latlng.lng;
    });
  }

  private initNetworkMap() {
    this.networkMap = L.map('network-map').setView([20.5937, 78.9629], 5); // India center
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.networkMap);
  }

  private refreshNetworkMap() {
    if (!this.networkMap) return;
    
    // Clear existing markers (excluding the tile layer)
    this.networkMap.eachLayer((layer) => {
      if (layer instanceof L.Marker) {
        this.networkMap.removeLayer(layer);
      }
    });

    // Add markers for all hospitals
    this.hospitals.forEach(h => {
      if (h.latitude && h.longitude) {
        L.marker([h.latitude, h.longitude])
          .bindPopup(`<b>${h.name}</b><br>${h.location}<br>📞 ${h.contactNumber}`)
          .addTo(this.networkMap);
      }
    });
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
        this.newHospital = { name: '', location: '', contactNumber: '', latitude: 19.0760, longitude: 72.8777 };
        this.pickerMarker.setLatLng([19.0760, 72.8777]);
        this.pickerMap.setView([19.0760, 72.8777], 10);
        this.loadHospitals(); // Refresh list
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = "Failed to add hospital.";
      }
    });
  }
}
