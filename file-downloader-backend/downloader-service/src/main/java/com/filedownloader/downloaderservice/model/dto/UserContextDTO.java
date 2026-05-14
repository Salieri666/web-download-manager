package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authenticated user context")
public class UserContextDTO {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "User full name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "User email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Set of user roles", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<UserRole> roles = new HashSet<>();
}
