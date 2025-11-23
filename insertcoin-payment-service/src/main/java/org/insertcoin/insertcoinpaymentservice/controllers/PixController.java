package org.insertcoin.insertcoinpaymentservice.controllers;

import org.insertcoin.insertcoinpaymentservice.service.PixService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/pix")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @PostMapping("/{pixKey}/confirm")
    @PreAuthorize("hasAuthority('PAYMENTS_ADMIN')")
    public ResponseEntity<Object> confirmPayment(@PathVariable String pixKey) {
        pixService.confirmPix(pixKey);
        return ResponseEntity.noContent().build();
    }
}
