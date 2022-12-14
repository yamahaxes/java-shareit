package ru.practicum.shareit.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

public class CustomRequestPage implements Pageable {

    private final Pageable pageable;

    public CustomRequestPage(int from, int size) {
        checkRangePageable(from, size);
        pageable = PageRequest.of(from / size, size);
    }

    public CustomRequestPage(int from, int size, Sort sort) {
        checkRangePageable(from, size);
        pageable = PageRequest.of(from / size, size, sort);
    }

    private void checkRangePageable(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException();
        }
    }

    @Override
    public int getPageNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return pageable.getPageSize();
    }

    @Override
    public long getOffset() {
        return pageable.getOffset();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable next() {
        return pageable.next();
    }

    @Override
    public Pageable previousOrFirst() {
        return pageable.previousOrFirst();
    }

    @Override
    public Pageable first() {
        return pageable.first();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return pageable.withPage(pageNumber);
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }
}
