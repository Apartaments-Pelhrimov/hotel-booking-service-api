/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.adapter.in.web.controller.user.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;
import ua.mibal.booking.adapter.in.web.advice.model.ApiError;
import ua.mibal.booking.adapter.in.web.model.ChangePasswordDto;
import ua.mibal.booking.adapter.in.web.model.DeleteMeDto;
import ua.mibal.booking.adapter.in.web.model.UserAccountDto;
import ua.mibal.booking.adapter.in.web.model.UserDto;
import ua.mibal.booking.application.model.ChangeNotificationSettingsForm;
import ua.mibal.booking.application.model.ChangeUserForm;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface UserControllerDocs {

    @Operation(summary = "Get me as user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User received"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    UserDto getOne(Authentication authentication);

    @Operation(summary = "Get me as user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User received"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    UserAccountDto getAccount(Authentication authentication);

    @Operation(summary = "Delete my account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description =
                    "User is not authenticated or supplied password is incorrect",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    void deleteAccount(DeleteMeDto dto,
                       Authentication authentication);

    @Operation(summary = "Update my account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Passed data to update is incorrect",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    void changeAccount(ChangeUserForm form,
                       Authentication authentication);

    @Operation(summary = "Put new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated"),
            @ApiResponse(responseCode = "400", description =
                    "New password is not valid or supplied old password is incorrect",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    void putPassword(ChangePasswordDto dto,
                     Authentication authentication);

    @Operation(summary = "Update notification settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification settings updated"),
            @ApiResponse(responseCode = "400", description = "Passed data to update is incorrect",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    void changeNotificationSettings(ChangeNotificationSettingsForm form,
                                    Authentication authentication);
}
