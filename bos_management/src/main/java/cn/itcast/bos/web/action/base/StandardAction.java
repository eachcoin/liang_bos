package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.StandardService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class StandardAction extends BaseAction<Standard> {

	// 注入Service对象
	@Autowired
	private StandardService standardService;

	// 添加操作
	@Action(value = "standard_save", results = { @Result(type = "redirect", location = "./pages/base/standard.html") })
	public String save() {
		standardService.save(model);
		return SUCCESS;
	}

	// 分页列表查询
	@Action(value = "standard_pageQuery", results = { @Result(type = "json") })
	public String pageQuery() {
		// 调用业务层 ，查询数据结果
		Pageable pageable = new PageRequest(page - 1, rows);
		Page<Standard> pageData = standardService.findPageData(pageable);

		// json数据返回
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

    // 查询所有收派标准方法
    @Action(value = "standard_findAll", results = { @Result(name = "success", type = "json") })
    public String findAll() {
        List<Standard> standards = standardService.findAll();
        ActionContext.getContext().getValueStack().push(standards);
        return SUCCESS;
    }

}
