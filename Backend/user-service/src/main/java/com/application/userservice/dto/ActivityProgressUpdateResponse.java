package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityProgressUpdateResponse {

    private boolean progressChanged;
    private int affectedStudyPlans;
    private int completedItems;
}
