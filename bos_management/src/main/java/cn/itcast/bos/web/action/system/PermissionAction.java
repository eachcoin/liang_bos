package cn.itcast.bos.web.action.system;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.service.system.PermissionService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PermissionAction extends BaseAction<Permission> {

	@Autowired
	private PermissionService permissionService;

	@Action(value = "permission_list", results = { @Result(type = "json") })
	public String list() {
		List<Permission> permissions = permissionService.findAll();
		ActionContext.getContext().getValueStack().push(permissions);
		return SUCCESS;
	}

	@Action(value = "permission_save", results = { 
			@Result(type = "redirect", location = "pages/system/permission.html") })
	public String save() {
		permissionService.save(model);
		return SUCCESS;
	}
}
