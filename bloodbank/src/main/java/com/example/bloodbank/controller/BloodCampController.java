package com.example.bloodbank.controller;

import com.example.bloodbank.dto.BloodCampBookingDTO;
import com.example.bloodbank.dto.BloodCampDTO;
import com.example.bloodbank.dto.CreateCampRequest;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.service.BloodCampService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
public class BloodCampController {

    private final BloodCampService bloodCampService;

    @PostMapping
    public ResponseEntity<BloodCampDTO> createCamp(@AuthenticationPrincipal User user, @RequestBody CreateCampRequest request) {
        return ResponseEntity.ok(bloodCampService.createCamp(user.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<BloodCampDTO>> getAllUpcomingCamps() {
        return ResponseEntity.ok(bloodCampService.getAllUpcomingCamps());
    }

    @GetMapping("/hospital")
    public ResponseEntity<List<BloodCampDTO>> getHospitalCamps(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bloodCampService.getHospitalCamps(user.getUsername()));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BloodCampBookingDTO>> getHospitalCampBookings(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bloodCampService.getHospitalCampBookings(user.getUsername(), id));
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<BloodCampBookingDTO> bookSlot(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bloodCampService.bookSlot(user.getUsername(), id));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BloodCampBookingDTO>> getMyBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bloodCampService.getPatientBookings(user.getUsername()));
    }
}
