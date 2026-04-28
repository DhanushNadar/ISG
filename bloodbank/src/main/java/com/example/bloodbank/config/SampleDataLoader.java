package com.example.bloodbank.config;

import com.example.bloodbank.entity.BloodDonation;
import com.example.bloodbank.entity.BloodRecord;
import com.example.bloodbank.entity.Disease;
import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.entity.Hospital;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.entity.PatientDisease;
import com.example.bloodbank.repository.BloodDonationRepository;
import com.example.bloodbank.repository.BloodRecordRepository;
import com.example.bloodbank.repository.DiseaseRepository;
import com.example.bloodbank.repository.HospitalRepository;
import com.example.bloodbank.repository.PatientDiseaseRepository;
import com.example.bloodbank.repository.PatientRepository;
import com.example.bloodbank.repository.UserRepository;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final DiseaseRepository diseaseRepository;
    private final HospitalRepository hospitalRepository;
    private final PatientDiseaseRepository patientDiseaseRepository;
    private final BloodRecordRepository bloodRecordRepository;
    private final BloodDonationRepository bloodDonationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (diseaseRepository.count() == 0) {
            diseaseRepository.save(Disease.builder().name("Anemia").isMajor(false).description("Lack of healthy red blood cells").build());
            diseaseRepository.save(Disease.builder().name("Leukemia").isMajor(true).description("Cancer of the body's blood-forming tissues").build());
            diseaseRepository.save(Disease.builder().name("HIV").isMajor(true).description("Human immunodeficiency virus").build());
            diseaseRepository.save(Disease.builder().name("Hepatitis B").isMajor(true).description("Serious liver infection").build());
            diseaseRepository.save(Disease.builder().name("Malaria").isMajor(false).description("Mosquito-borne infectious disease").build());
        }
        
        if (userRepository.findByEmail("admin@bloodbank.com").isEmpty()) {
            User admin = User.builder()
                .email("admin@bloodbank.com")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build();
            userRepository.save(admin);
        }

        if (hospitalRepository.count() == 0) {
            hospitalRepository.save(Hospital.builder().name("City Central Hospital").location("Downtown").contactNumber("1234567890").build());
            hospitalRepository.save(Hospital.builder().name("Sunrise Healthcare Clinic").location("Uptown").contactNumber("0987654321").build());
        }

        if (patientRepository.count() == 0) {
            // Fetch relations
            Hospital hospital1 = hospitalRepository.findAll().get(0);
            Hospital hospital2 = hospitalRepository.findAll().get(1);
            Disease anemia = diseaseRepository.findByNameIgnoreCase("Anemia").get();
            Disease leukemia = diseaseRepository.findByNameIgnoreCase("Leukemia").get();
            Disease hiv = diseaseRepository.findByNameIgnoreCase("HIV").get();

            // PATIENT 1: John Doe (NOT_ELIGIBLE due to active Leukemia)
            Patient patient1 = Patient.builder().aadhaarNumber("123456789012").name("John Doe").age(30).gender("Male").bloodGroup("O+").phone("9876543210").email("johndoe@example.com").createdAt(LocalDateTime.now()).hospital(hospital1).build();
            patient1 = patientRepository.save(patient1);

            bloodRecordRepository.save(BloodRecord.builder().patient(patient1).hospital(hospital1).hemoglobin(11.5).platelets(150000.0).rbc(4.5).wbc(8000.0).recordDate(LocalDate.now().minusDays(10)).build());
            patientDiseaseRepository.save(PatientDisease.builder().patient(patient1).disease(anemia).diagnosedDate(LocalDate.now().minusYears(1)).status(DiseaseStatus.RECOVERED).isCurrent(false).build());
            patientDiseaseRepository.save(PatientDisease.builder().patient(patient1).disease(leukemia).diagnosedDate(LocalDate.now().minusMonths(2)).status(DiseaseStatus.ACTIVE).isCurrent(true).build());
            bloodDonationRepository.save(BloodDonation.builder().patient(patient1).donationDate(LocalDate.now().minusYears(2)).quantityMl(450.0).build());

            // PATIENT 2: Jane Smith (ELIGIBLE - Totally healthy, perfect blood levels)
            Patient patient2 = Patient.builder().aadhaarNumber("555566667777").name("Jane Smith").age(28).gender("Female").bloodGroup("A-").phone("8234567891").email("janesmith@example.com").createdAt(LocalDateTime.now()).hospital(hospital2).build();
            patient2 = patientRepository.save(patient2);

            bloodRecordRepository.save(BloodRecord.builder().patient(patient2).hospital(hospital2).hemoglobin(14.0).platelets(250000.0).rbc(5.2).wbc(6500.0).recordDate(LocalDate.now().minusDays(2)).build());
            bloodDonationRepository.save(BloodDonation.builder().patient(patient2).donationDate(LocalDate.now().minusMonths(4)).quantityMl(500.0).build());
            bloodDonationRepository.save(BloodDonation.builder().patient(patient2).donationDate(LocalDate.now().minusYears(1)).quantityMl(450.0).build());

            // PATIENT 3: Bob Brown (NOT_ELIGIBLE due to active HIV)
            Patient patient3 = Patient.builder().aadhaarNumber("999988887777").name("Bob Brown").age(45).gender("Male").bloodGroup("AB+").phone("7654321098").email("bob.brown@example.com").createdAt(LocalDateTime.now()).hospital(hospital1).build();
            patient3 = patientRepository.save(patient3);

            bloodRecordRepository.save(BloodRecord.builder().patient(patient3).hospital(hospital1).hemoglobin(12.1).platelets(200000.0).rbc(4.8).wbc(5000.0).recordDate(LocalDate.now().minusYears(1)).build());
            patientDiseaseRepository.save(PatientDisease.builder().patient(patient3).disease(hiv).diagnosedDate(LocalDate.now().minusYears(3)).status(DiseaseStatus.ACTIVE).isCurrent(true).build());
        }
    }
}
