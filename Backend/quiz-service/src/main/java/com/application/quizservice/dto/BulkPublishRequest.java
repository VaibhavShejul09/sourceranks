package com.application.quizservice.dto;

import lombok.Data;

import java.util.*;
import java.util.UUID;

@Data
public class BulkPublishRequest {
    private List<UUID> quizIds;
    private boolean published;
}
