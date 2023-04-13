package com.moa.dto.page;

import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.Objects;

@Getter
public class SortResponse {
    private final boolean sorted;
    private Direction direction;
    private String orderProperty;

    public SortResponse(Sort sort) {
        this.sorted = sort.isSorted();

        Sort.Order order = sort.get().findFirst().orElse(null);
        if (!Objects.isNull(order)) {
            this.direction = order.getDirection();
            this.orderProperty = order.getProperty();
        }
    }
}
