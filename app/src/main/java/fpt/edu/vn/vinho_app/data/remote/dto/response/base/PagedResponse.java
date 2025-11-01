package fpt.edu.vn.vinho_app.data.remote.dto.response.base;

import java.util.List;

public class PagedResponse<T> extends BaseResponse<List<T>> {
    private int totalCount;
    private int page;
    private int pageSize;
    private int totalPages;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
