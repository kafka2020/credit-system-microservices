package ru.netology.credit.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditDecisionEvent {
    private Long applicationId;
    private boolean approved;
    private String reason;
}
