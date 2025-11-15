package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
	@FutureOrPresent(message = "Start date must be in the present or future")
	private LocalDateTime start;

	@NotNull(message = "End date must be specified")
	@Future(message = "End date must be in the future")
	private LocalDateTime end;
}