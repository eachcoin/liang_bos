package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Standard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 收派标准管理 
 * @author itcast
 * 
 */
public interface StandardService {
	void save(Standard standard);

	// 分页查询 
	Page<Standard> findPageData(Pageable pageable);

	List<Standard> findAll();

}
