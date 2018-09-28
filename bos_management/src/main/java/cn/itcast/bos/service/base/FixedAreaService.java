package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.FixedArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface FixedAreaService {

	void save(FixedArea model);

	Page<FixedArea> findPageData(Specification<FixedArea> specification,
                                 Pageable pageable);

	void associationCourierToFixedArea(FixedArea model, Integer courierId,
                                       Integer takeTimeId);

}
