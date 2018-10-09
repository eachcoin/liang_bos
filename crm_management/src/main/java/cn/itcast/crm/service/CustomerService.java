package cn.itcast.crm.service;

import cn.itcast.crm.domain.Customer;

import javax.ws.rs.*;
import java.util.List;

/**
 * 客户操作
 * 
 * @author itcast
 *
 */
public interface CustomerService {

	// 查询所有未关联客户列表
	@Path("/noassociationcustomers")
	@GET
	@Produces({ "application/xml", "application/json" })
	List<Customer> findNoAssociationCustomers();

	// 已经关联到指定定区的客户列表
	@Path("/associationfixedareacustomers/{fixedareaid}")
	@GET
	@Produces({ "application/xml", "application/json" })
	List<Customer> findHasAssociationFixedAreaCustomers(
            @PathParam("fixedareaid") String fixedAreaId);

	// 将客户关联到定区上 ， 将所有客户id 拼成字符串 1,2,3
	@Path("/associationcustomerstofixedarea")
	@PUT
	void associationCustomersToFixedArea(
            @QueryParam("customerIdStr") String customerIdStr,
            @QueryParam("fixedAreaId") String fixedAreaId);

	//保存客户的操作
	@Path("/customer")
	@POST
    @Consumes({ "application/xml", "application/json" })
    void regist(Customer customer);

	//查询用户
    @Path("/customer/telephone/{telephone}")
    @GET
    @Consumes({ "application/xml", "application/json" })
    Customer findByTelephone(@PathParam("telephone") String telephone);

    //修改Type
    @Path("/customer/updatetype/{telephone}")
    @GET
	@Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    void updateType(@PathParam("telephone") String telephone);

    //登录操作
	@Path("/customer/login")
	@GET
    @Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	Customer updateType(@QueryParam("telephone") String telephone,@QueryParam("password") String password);

	//根据地址查询定区编号
    @Path("/customer/findFixedAreaIdByAddress")
    @GET
    @Consumes({ "application/xml", "application/json" })
    String findFixedAreaIdByAddress(@QueryParam("address") String address);
}
