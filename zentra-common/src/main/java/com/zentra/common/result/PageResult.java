package com.zentra.common.result;

import java.util.List;

public class PageResult<T> {

    private Long total;
    private List<T> records;

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public List<T> getRecords() {
        return records;
    }
}
