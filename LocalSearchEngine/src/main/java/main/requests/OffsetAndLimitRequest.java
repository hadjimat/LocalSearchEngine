package main.requests;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetAndLimitRequest implements Pageable {

    private final int limit;
    private final int offset;

    public OffsetAndLimitRequest(int limit, int offset) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public @NotNull Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public @NotNull Pageable next() {
        return new OffsetAndLimitRequest(getPageSize(), (int) (getOffset() + getPageSize()));
    }

    public Pageable previous() {
        return hasPrevious() ?
                new OffsetAndLimitRequest(getPageSize(), (int) (getOffset() - getPageSize())) : this;
    }

    @Override
    public @NotNull Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public @NotNull Pageable first() {
        return new OffsetAndLimitRequest(getPageSize(), 0);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}

