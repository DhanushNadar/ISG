export interface PatientDTO {
    id?: number;
    aadhaarNumber: string;
    name: string;
    age: number;
    gender: string;
    bloodGroup: string;
    phone: string;
    email?: string;
    createdAt?: string;
}

export interface HospitalDTO {
    id?: number;
    name: string;
    location: string;
    contactNumber: string;
}

export interface DiseaseDTO {
    id?: number;
    name: string;
    isMajor: boolean;
    description: string;
}

export interface BloodRecordDTO {
    id?: number;
    patientId: number;
    hospitalId: number;
    hemoglobin: number;
    platelets: number;
    rbc: number;
    wbc: number;
    recordDate: string;
}

export interface PatientDiseaseDTO {
    id?: number;
    patientId: number;
    diseaseId: number;
    diagnosedDate: string;
    status: string; // "ACTIVE" | "RECOVERED"
    isCurrent: boolean;
}

export interface BloodDonationDTO {
    id?: number;
    patientId: number;
    donationDate: string;
    quantityMl: number;
}

export interface PatientDiseaseResponse {
    id: number;
    diseaseName: string;
    isMajor: boolean;
    diagnosedDate: string;
    status: string;
    isCurrent: boolean;
}

export interface PatientProfileResponse {
    name: string;
    bloodGroup: string;
    recentHospital: string;
    recentTestDate: string;
    recentDisease: string;
    majorDisease: string;
    eligibility: string; // "ELIGIBLE" or "NOT_ELIGIBLE"
    history: PatientDiseaseResponse[];
}
