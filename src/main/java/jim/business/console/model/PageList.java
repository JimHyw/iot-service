package jim.business.console.model;

import java.util.List;

/** 
* @ClassName: PageList 
* @Description: 通用列表数据
* @author DanielHyw
* @date Apr 10, 2020 6:34:39 PM 
* 
* @param <T> 
*/
public class PageList<T> {

	/** 
	* @Fields rows : 列表数据
	*/ 
	private List<T> rows;
	
	/** 
	* @Fields total : 总行数 
	*/ 
	private long total;
	
	/** 
	* @Fields pageSize : 每页数量
	*/ 
	private int pageSize;
	
	/** 
	* @Fields pageNum : 当前页码
	*/ 
	private int pageNum;

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
}
