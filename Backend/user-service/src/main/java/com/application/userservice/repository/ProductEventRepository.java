package com.application.userservice.repository;

import com.application.userservice.entity.ProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductEventRepository extends JpaRepository<ProductEvent, Long> {

    List<ProductEvent> findByOccurredAtAfter(LocalDateTime occurredAt);

    List<ProductEvent> findByEventCategory(String eventCategory);

    List<ProductEvent> findByContentType(String contentType);
}
