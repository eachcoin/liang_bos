package cn.itcast.bos.dao.base;

import cn.itcast.bos.domain.base.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, String>,
		JpaSpecificationExecutor<Area> {

    List<Area> findByProvinceAndCityAndDistrict(String province, String city, String district);
}
