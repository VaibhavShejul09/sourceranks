package com.application.quizservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BulkDeleteRequest {
    private List<UUID> quizIds;
}
