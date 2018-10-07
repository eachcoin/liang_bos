package cn.itcast.bos.service.take_delivery;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.*;
import java.util.Date;

public interface PromotionService {

	// 保存宣传任务
	void save(Promotion promotion);

	// 分页查询
	Page<Promotion> findPageData(Pageable pageable);

	// 根据page和rows 返回分页数据
	@Path("/pageQuery")
	@GET
	@Produces({ "application/xml", "application/json" })
	PageBean<Promotion> findPageData(@QueryParam("page") int page,
                                     @QueryParam("rows") int rows);

	// 根据id 查询
	@Path("/promotion/{id}")
	@GET
	@Produces({ "application/xml", "application/json" })
	Promotion findById(@PathParam("id") Integer id);

	// 设置活动过期
	void updateStatus(Date date);
}
