package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity.Source;
import com.pocket_poker_pal_app.PocketPokerPalApp.exception.EmptyRulebookException;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.AdminUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.RulebookVectorUploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/rulebook")
@RequiredArgsConstructor
public class RulebookController {

    private final RulebookVectorUploadService rulebookVectorUploadService;
    private final AdminUserRepository adminUserRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadRulebook(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("version") String version,
            @RequestParam("source") Source source,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            Optional<AdminUser> adminOpt = adminUserRepository.findByEmail(userDetails.getUsername());

            if (adminOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not found.");
            }

            RulebookEntity rulebook = new RulebookEntity();
            rulebook.setId(UUID.randomUUID());
            rulebook.setTitle(title);
            rulebook.setVersion(version);
            rulebook.setSource(source);
            rulebook.setUploadedBy(adminOpt.get());

            rulebookVectorUploadService.processAndUpload(file, rulebook);

            return ResponseEntity.ok("Rulebook uploaded and processed successfully.");
        } catch (EmptyRulebookException e) {
            return ResponseEntity.badRequest().body("Rulebook was empty or could not be processed.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process PDF file.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
