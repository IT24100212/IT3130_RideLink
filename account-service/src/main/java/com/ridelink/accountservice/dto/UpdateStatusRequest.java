package com.ridelink.accountservice.dto;

import com.ridelink.accountservice.model.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Update account status payload")
public class UpdateStatusRequest {

    @Schema(description = "Target account status (ACTIVE, INACTIVE, SUSPENDED)", example = "ACTIVE")
    @NotNull(message = "Status is required and must be ACTIVE, INACTIVE, or SUSPENDED")
    private AccountStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
