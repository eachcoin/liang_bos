package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.CourierService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CourierAction extends BaseAction<Courier> {

	// 注入Service
	@Autowired
	private CourierService courierService;

	// 添加快递员方法
	@Action(value = "courier_save", results = { @Result(location = "./pages/base/courier.html", type = "redirect") })
	public String save() {
		courierService.save(model);
		return SUCCESS;
	}

	// 分页查询方法
	@Action(value = "courier_pageQuery", results = { @Result(type = "json") })
	public String pageQuery() {
		// 封装Pageable对象
		Pageable pageable = new PageRequest(page - 1, rows);
		// 封装条件查询对象 Specification
		Specification<Courier> specification = new Specification<Courier>() {
			@Override
			// Root 用于获取属性字段，CriteriaQuery可以用于简单条件查询，CriteriaBuilder 用于构造复杂条件查询
			public Predicate toPredicate(Root<Courier> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();

				// 简单单表查询
				if (StringUtils.isNotBlank(model.getCourierNum())) {
					Predicate p1 = cb.equal(
							root.get("courierNum").as(String.class),
							model.getCourierNum());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + model.getCompany() + "%");
					list.add(p2);
				}
				if (StringUtils.isNotBlank(model.getType())) {
					Predicate p3 = cb.equal(root.get("type").as(String.class),
							model.getType());
					list.add(p3);
				}
				// 多表查询
				Join<Courier, Standard> standardJoin = root.join("standard",
						JoinType.INNER);
				if (model.getStandard() != null
						&& StringUtils.isNotBlank(model.getStandard()
								.getName())) {
					Predicate p4 = cb.like(
							standardJoin.get("name").as(String.class), "%"
									+ model.getStandard().getName() + "%");
					list.add(p4);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		// 调用业务层 ，返回 Page
		Page<Courier> pageData = courierService.findPageData(specification,
				pageable);
		//
		pushPageDataToValueStack(pageData);

		return SUCCESS;
	}

	// 属性驱动
	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 作废快递员
	@Action(value = "courier_delBatch", results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String delBatch() {
		// 按,分隔ids
		String[] idArray = ids.split(",");
		// 调用业务层，批量作废
		courierService.delBatch(idArray);

		return SUCCESS;
	}

	//查询未关联的快递员
    @Action(value = "courier_findnoassociation",results = { @Result(type = "json") })
    public String findnoassociation() {
        List<Courier> couriers = courierService.findNoAssociation();
        // 将查询快递员列表 压入值栈
        ActionContext.getContext().getValueStack().push(couriers);
        return SUCCESS;
    }

}
