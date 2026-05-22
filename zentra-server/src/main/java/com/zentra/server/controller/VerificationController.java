package com.zentra.server.controller;

import com.zentra.common.dto.verification.SendVerificationCodeRequestDTO;
import com.zentra.server.annotation.SuccessApiResponse;
import com.zentra.server.annotation.ValidationErrorApiResponse;
import com.zentra.server.dto.DataResponseDTO;
import com.zentra.server.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification controller
 */
@Tag(
        name = "Verification APIs",
        description = "Verification code related APIs"
)
@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {

    /**
     * Verification code service
     */
    private final VerificationCodeService verificationCodeService;

    /**
     * Send verification code
     */
    @Operation(
            summary = "Send verification code",
            description =
                    "Generate and send verification code" +
                    "Verification code has retry protection adn expiration governance"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @PostMapping("/send")
    public DataResponseDTO<String> sendCode(
            @Valid
            @RequestBody
            SendVerificationCodeRequestDTO request
    ) {

        // Generate verification code
        String code = verificationCodeService.generateCode();

        // Save verification code
        verificationCodeService.saveCode(
                request.getType(),
                request.getTarget(),
                code
        );

        return DataResponseDTO.success(
                "Verification code sent successfully"
        );
    }

}
