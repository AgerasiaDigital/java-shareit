package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "Item ID must be specified")
	private Long itemId;

	@NotNull(message = "Start date must be specified")
	private LocalDateTime start;

	@NotNull(message = "End date must be specified")
	private LocalDateTime end;
}