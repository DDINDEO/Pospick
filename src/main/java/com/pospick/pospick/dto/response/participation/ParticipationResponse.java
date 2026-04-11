package com.pospick.pospick.dto.response.participation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationResponse {
    private Long partId;
    private Long eventId;
    private String sellerName;
    private String status; // PENDING, APPROVED, REJECTED
}
