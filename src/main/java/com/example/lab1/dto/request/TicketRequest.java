package com.example.lab1.dto.request;

import com.example.lab1.entity.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record TicketRequest(
        @NotBlank(message = "Поле name не может быть пустым")
        String name,

        @NotNull(message = "Поле coordinates не может быть пустым")
        @Valid
        CoordinatesReferenceRequest coordinates,

        @Valid
        PersonReferenceRequest person,

        @NotNull(message = "Поле event не может быть пустым")
        @Valid
        EventReferenceRequest event,

        @NotNull(message = "Поле price не может быть пустым")
        @Positive(message = "Поле price должно быть положительным числом")
        Integer price,

        TicketType ticketType,

        @NotNull(message = "Поле discount не может быть пустым")
        @Min(value = 1, message = "Discount может быть в пределах от 1 до 100")
        @Max(value = 100, message = "Discount может быть в пределах от 1 до 100")
        Integer discount,

        @Positive(message = "Поле number должно быть положительным числом")
        Float number,

        @Valid
        VenueReferenceRequest venue
) {
}
