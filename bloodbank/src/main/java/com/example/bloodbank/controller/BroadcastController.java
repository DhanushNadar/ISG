package com.example.bloodbank.controller;

import com.example.bloodbank.service.BroadcastService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.bloodbank.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class BroadcastController {

    private final BroadcastService broadcastService;

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<Map<String, Object>> broadcastEmergency(
            @AuthenticationPrincipal User user,
            @RequestBody BroadcastRequest request) {
            
        Long hospitalId = user.getHospitalId();
        if (hospitalId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No hospital associated with this account."));
        }
        
        int donorsAlerted = broadcastService.broadcastEmergencyNeed(request.getBloodGroup(), hospitalId);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Broadcast sent to " + donorsAlerted + " eligible donors.",
                "donorsAlerted", donorsAlerted
        ));
    }

    @Data
    static class BroadcastRequest {
        private String bloodGroup;
    }
}
