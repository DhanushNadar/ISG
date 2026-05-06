import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
    PatientDTO, HospitalDTO, DiseaseDTO, 
    BloodRecordDTO, PatientDiseaseDTO, PatientProfileResponse 
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly API_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080/api' 
    : 'https://isg-production.up.railway.app/api';

  constructor(private http: HttpClient) { }

  // Patients
  getPatients(): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(`${this.API_URL}/patients`);
  }

  getPatientProfile(aadhaar: string): Observable<PatientProfileResponse> {
    return this.http.get<PatientProfileResponse>(`${this.API_URL}/patients/${aadhaar}`);
  }

  getPatientPortalProfile(): Observable<PatientProfileResponse> {
    return this.http.get<PatientProfileResponse>(`${this.API_URL}/portal/me`);
  }

  triggerEmergencyAccess(aadhaar: string, reason: string): Observable<PatientProfileResponse> {
    return this.http.post<PatientProfileResponse>(`${this.API_URL}/patients/${aadhaar}/emergency-access`, { reason });
  }

  setPortalPassword(aadhaar: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/patients/${aadhaar}/set-portal-password`, { password });
  }

  createPatient(patient: PatientDTO): Observable<PatientDTO> {
    return this.http.post<PatientDTO>(`${this.API_URL}/patients`, patient);
  }

  deletePatient(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/patients/${id}`);
  }

  // --- Medical Report Endpoints ---
  
  submitMedicalReport(file: File, diseaseId: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('diseaseId', diseaseId.toString());
    
    // Note: Do NOT set Content-Type header manually for FormData, browser sets it with boundaries
    return this.http.post(`${this.API_URL}/reports/submit`, formData);
  }

  getMyMedicalReports(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/reports/me?t=${new Date().getTime()}`);
  }

  getMedicalReportFile(id: number): Observable<Blob> {
    return this.http.get(`${this.API_URL}/reports/${id}/file`, { responseType: 'blob' });
  }

  // --- BLOOD CAMPS ---
  createBloodCamp(camp: any): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/camps`, camp);
  }

  getAllUpcomingCamps(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/camps?t=${new Date().getTime()}`);
  }

  getHospitalCamps(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/camps/hospital?t=${new Date().getTime()}`);
  }

  getHospitalCampBookings(campId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/camps/${campId}/bookings?t=${new Date().getTime()}`);
  }

  bookCampSlot(campId: number): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/camps/${campId}/book`, {});
  }

  getMyCampBookings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/camps/my-bookings?t=${new Date().getTime()}`);
  }

  getNextDiseaseToClaim(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/reports/next-disease?t=${new Date().getTime()}`);
  }

  getPendingMedicalReports(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/reports/pending?t=${new Date().getTime()}`);
  }

  approveMedicalReport(id: number): Observable<any> {
    return this.http.post(`${this.API_URL}/reports/${id}/approve`, {});
  }

  rejectMedicalReport(id: number): Observable<any> {
    return this.http.post(`${this.API_URL}/reports/${id}/reject`, {});
  }

  checkEligibility(patientId: number): Observable<string> {
    return this.http.get<string>(`${this.API_URL}/patients/${patientId}/eligibility`, { responseType: 'text' as 'json' });
  }

  // Hospitals
  getHospitals(): Observable<HospitalDTO[]> {
    return this.http.get<HospitalDTO[]>(`${this.API_URL}/hospitals`);
  }

  createHospital(hospital: HospitalDTO): Observable<HospitalDTO> {
    return this.http.post<HospitalDTO>(`${this.API_URL}/hospitals`, hospital);
  }

  broadcastEmergency(bloodGroup: string): Observable<{success: boolean, message: string, donorsAlerted: number}> {
    return this.http.post<any>(`${this.API_URL}/hospitals/broadcast`, { bloodGroup });
  }

  // Diseases
  getDiseases(): Observable<DiseaseDTO[]> {
    return this.http.get<DiseaseDTO[]>(`${this.API_URL}/diseases`);
  }

  createDisease(disease: DiseaseDTO): Observable<DiseaseDTO> {
    return this.http.post<DiseaseDTO>(`${this.API_URL}/diseases`, disease);
  }

  // Blood Records
  addBloodRecord(record: BloodRecordDTO): Observable<BloodRecordDTO> {
    return this.http.post<BloodRecordDTO>(`${this.API_URL}/blood-records`, record);
  }

  // Patient Disease Mappings
  assignDisease(mapping: PatientDiseaseDTO): Observable<PatientDiseaseDTO> {
    return this.http.post<PatientDiseaseDTO>(`${this.API_URL}/patient-disease`, mapping);
  }

  updateDiseaseStatus(id: number, status: string): Observable<PatientDiseaseDTO> {
    return this.http.put<PatientDiseaseDTO>(`${this.API_URL}/patient-disease/${id}`, { status });
  }
}
