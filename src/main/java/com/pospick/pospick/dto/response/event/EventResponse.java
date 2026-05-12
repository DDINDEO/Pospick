package com.pospick.pospick.dto.response.event;

import com.pospick.pospick.domain.Event;
import lombok.Getter;
import java.time.LocalDate;

/**
 * 행사 응답 DTO
 * 서버가 클라이언트에게 행사 정보를 반환할 때 사용하는 데이터 형식
 * Event 엔티티를 직접 노출하지 않고 필요한 필드만 담아서 반환
 */
@Getter
public class EventResponse {
    private Long eventId;
    private String organizerName;
    private String title;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String posterUrl;
    private boolean collectSales;
    private boolean collectStock;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.organizerName = event.getOrganizer().getName();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.posterUrl = event.getPosterUrl();
        this.collectSales = event.isCollectSales();
        this.collectStock = event.isCollectStock();
    }
}
