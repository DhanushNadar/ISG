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

  createPatient(patient: PatientDTO): Observable<PatientDTO> {
    return this.http.post<PatientDTO>(`${this.API_URL}/patients`, patient);
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
