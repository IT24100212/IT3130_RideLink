package com.ridelink.accountservice.dto;

import com.ridelink.accountservice.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Update account role payload")
public class UpdateRoleRequest {

    @Schema(description = "Target account role (PASSENGER, DRIVER, ADMIN)", example = "DRIVER")
    @NotNull(message = "Role is required and must be PASSENGER, DRIVER, or ADMIN")
    private Role role;

    public UpdateRoleRequest() {
    }

    public UpdateRoleRequest(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
