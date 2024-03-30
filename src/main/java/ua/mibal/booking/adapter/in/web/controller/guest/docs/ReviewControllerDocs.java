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

package ua.mibal.booking.adapter.in.web.controller.guest.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.adapter.in.web.advice.model.ApiError;
import ua.mibal.booking.adapter.in.web.model.ReviewDto;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface ReviewControllerDocs {

    @Operation(summary = "Get latest reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews received"),
    })
    List<ReviewDto> getAllLatest(Pageable pageable);

    @Operation(summary = "Get reviews for apartment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews received"),
            @ApiResponse(responseCode = "400", description = "Invalid apartment id format",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Apartment not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    List<ReviewDto> getAllByApartment(String apartmentId, Pageable pageable);
}
