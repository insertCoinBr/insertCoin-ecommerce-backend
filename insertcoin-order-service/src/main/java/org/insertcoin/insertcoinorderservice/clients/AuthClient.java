package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.response.AuthMeResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "insertcoin-auth-service",
        path = "/auth"
)
public interface AuthClient {

    @GetMapping("/me")
    AuthMeResponseDTO getAuthenticatedUser(
            @RequestHeader("Authorization") String token
    );
}
