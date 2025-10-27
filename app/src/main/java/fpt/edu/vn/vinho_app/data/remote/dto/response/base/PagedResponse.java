package fpt.edu.vn.vinho_app.data.remote.dto.response.base;

import java.util.List;

public class PagedResponse<T> extends BaseResponse<List<T>> {
    private int totalCount;
    private int page;
    private int pageSize;

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
        if (pageSize <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
