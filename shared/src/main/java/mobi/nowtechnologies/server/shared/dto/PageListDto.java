package mobi.nowtechnologies.server.shared.dto;

import java.util.List;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class PageListDto<T> {
	public static final String PAGE_LIST_DTO = "PAGE_LIST_DTO";
	
	private List<T> list;
	private int total;
	private int page;
	private int size;
	
	public PageListDto(){
	}
	
	public PageListDto(List<T> list, int total, int page, int size){
		this.list = list;
		this.total = total;
		this.page = page;
		this.size = size;
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + page;
		result = prime * result + size;
		result = prime * result + total;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageListDto<T> other = (PageListDto<T>) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (page != other.page)
			return false;
		if (size != other.size)
			return false;
		if (total != other.total)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageListDto [list=" + list + ", total=" + total + ", page=" + page + ", size=" + size + "]";
	}
}