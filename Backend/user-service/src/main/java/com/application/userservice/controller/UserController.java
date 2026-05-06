package com.application.userservice.controller;

import com.application.userservice.dto.DashboardSummaryResponse;
import com.application.userservice.dto.UserPreferenceRequest;
import com.application.userservice.dto.UserPreferenceResponse;
import com.application.userservice.dto.UserProfileResponse;
import com.application.userservice.service.UserPreferenceService;
import com.application.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserProfileService userProfileService;
    private final UserPreferenceService userPreferenceService;

    public UserController(
            UserProfileService userProfileService,
            UserPreferenceService userPreferenceService
    ) {
        this.userProfileService = userProfileService;
        this.userPreferenceService = userPreferenceService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        UUID userId = getUserId(authentication);
        String role = getRole(authentication);

        return ResponseEntity.ok(userProfileService.getProfile(userId, role));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me/preferences")
    public ResponseEntity<UserPreferenceResponse> getMyPreferences(Authentication authentication) {
        return ResponseEntity.ok(userPreferenceService.getPreferences(getUserId(authentication)));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/me/preferences")
    public ResponseEntity<UserPreferenceResponse> updateMyPreferences(
            Authentication authentication,
            @Valid @RequestBody UserPreferenceRequest request
    ) {
        return ResponseEntity.ok(userPreferenceService.updatePreferences(getUserId(authentication), request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me/dashboard-summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.getDashboardSummary(
                getUserId(authentication),
                getRole(authentication)
        ));
    }

    private UUID getUserId(Authentication authentication) {
        return (UUID) authentication.getPrincipal();
    }

    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("ROLE_USER");
    }
}
