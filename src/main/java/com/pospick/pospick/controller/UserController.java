package com.pospick.pospick.controller;

import com.pospick.pospick.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/me/account → 내 계좌번호 조회
    @GetMapping("/me/account")
    public ResponseEntity<Map<String, String>> getAccount(@AuthenticationPrincipal String loginId) {
        return ResponseEntity.ok(Map.of("accountNumber",
                userService.getAccountNumber(loginId) != null ? userService.getAccountNumber(loginId) : ""));
    }

    // PUT /api/users/me/account → 내 계좌번호 저장/수정
    @PutMapping("/me/account")
    public ResponseEntity<Void> updateAccount(
            @AuthenticationPrincipal String loginId,
            @RequestBody Map<String, String> body) {
        userService.updateAccountNumber(loginId, body.get("accountNumber"));
        return ResponseEntity.ok().build();
    }
}
