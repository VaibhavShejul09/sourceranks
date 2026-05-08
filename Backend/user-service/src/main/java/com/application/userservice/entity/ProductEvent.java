package com.application.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String eventName;

    @Column(nullable = false, length = 40)
    private String eventCategory;

    @Column(length = 40)
    private String source;

    @Column(length = 40)
    private String track;

    private UUID userId;

    @Column(length = 40)
    private String role;

    @Column(length = 40)
    private String contentType;

    @Column(length = 100)
    private String contentId;

    @Column(length = 255)
    private String contentTitle;

    @Column(length = 100)
    private String parentContentId;

    @Column(length = 100)
    private String topic;

    @Column(length = 40)
    private String outcome;

    private Double numericValue;

    @Lob
    private String metadataJson;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}
