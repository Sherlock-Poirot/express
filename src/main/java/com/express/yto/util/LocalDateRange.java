package com.express.yto.util;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
public class LocalDateRange {

    private LocalDate start;
    private LocalDate end;

    public LocalDateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    // 重写equals和hashCode，避免Map匹配问题
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalDateRange that = (LocalDateRange) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    // getter
    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
