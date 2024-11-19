package com.example.demo.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AleAntragMetadataDto(UUID antragId,
                                   String status,
                                   LocalDateTime updatedAt,
                                   int nodeCount) {
}
