package cn.itcast.bos.web.action;

import cn.itcast.bos.utils.MailUtils;
import cn.itcast.bos.utils.SmsUtils;
import cn.itcast.crm.domain.Customer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {
    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    @Action(value = "customer_sendSms")
    public String sendSms() throws IOException {

        String mobile_code =(int)((Math.random() * 9 + 1) * 100000)+"";
        System.out.println(mobile_code);
        // 将短信验证码 保存到session
        ServletActionContext.getRequest().getSession()
                .setAttribute(model.getTelephone(), mobile_code);

        final String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");

        jmsTemplate.send("bos_sms", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("telephone", model.getTelephone());
                mapMessage.setString("content", content);
                return mapMessage;
            }
        });
        return NONE;






        //String code = SmsUtils.sendSms(model.getTelephone(), content);
//        String code = "2";
//        System.out.println(code);
//        if ("2".equals(code)) {
//            System.out.println("发送成功");
//            return NONE;
//        } else {
//            throw new RuntimeException("发送失败" + code);
//        }

    }

    // 属性驱动
    private String checkcode;

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Action(value = "customer_regist", results = {
            @Result(type = "redirect", location = "signup-success.html"),
            @Result(name = "input", type = "redirect", location = "signup.html")})
    public String regist() {
        // 先校验短信验证码，如果不通过，调回注册页面
        // 从session获取 之前生成验证码
        String checkcodeSession = (String) ServletActionContext.getRequest()
                .getSession().getAttribute(model.getTelephone());
        if (checkcodeSession == null || !checkcodeSession.equals(checkcode)) {
            System.out.println("短信验证码错误...");
            // 短信验证码错误
            return INPUT;
        }
        // 调用webService 连接CRM 保存客户信息
        WebClient
                .create("http://localhost:9002/crm_management/services"
                        + "/customerService/customer")
                .type(MediaType.APPLICATION_JSON).post(model);
        System.out.println("客户注册成功...");
        /*// 发送一封激活邮件
        // 生成激活码
        String activecode = RandomStringUtils.randomNumeric(32);

        // 将激活码保存到redis，设置24小时失效
        redisTemplate.opsForValue().set(model.getTelephone(), activecode, 24,
                TimeUnit.HOURS);

        // 调用MailUtils发送激活邮件
        String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='"
                + MailUtils.activeUrl + "?telephone=" + model.getTelephone()
                + "&activecode=" + activecode + "'>速运快递邮箱绑定地址</a>";
        MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());*/


        String activecode = RandomStringUtils.randomNumeric(32);
        System.out.println(activecode);
        redisTemplate.opsForValue().set(model.getTelephone(),activecode,24,TimeUnit.HOURS);
        String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='"
                + MailUtils.activeUrl + "?telephone=" + model.getTelephone()
                + "&activecode=" + activecode + "'>速运快递邮箱绑定地址</a>";
        MailUtils.sendMail("激活邮件",content,model.getEmail());
        return SUCCESS;
    }

	// 属性驱动
	private String activecode;

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}

	@Action("customer_activeMail")
	public String activeMail() throws IOException {
		/*ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		// 判断激活码是否有效
		String activecodeRedis = redisTemplate.opsForValue().get(model.getTelephone());
		if (activecodeRedis == null || !activecodeRedis.equals(activecodeRedis)) {
			// 激活码无效
			ServletActionContext.getResponse().getWriter()
					.println("激活码无效，请登录系统，重新绑定邮箱！");
		} else {
			// 激活码有效
			// 防止重复绑定
			// 调用CRM webService 查询客户信息，判断是否已经绑定
			Customer customer = WebClient
					.create("http://localhost:9002/crm_management/services"
							+ "/customerService/customer/telephone/"
							+ model.getTelephone())
					.accept(MediaType.APPLICATION_JSON).get(Customer.class);
			if (customer.getType() == null || customer.getType() != 1) {
				// 没有绑定,进行绑定
				WebClient.create(
						"http://localhost:9002/crm_management/services"
								+ "/customerService/customer/updatetype/"
								+ model.getTelephone()).get();
				ServletActionContext.getResponse().getWriter()
						.println("邮箱绑定成功！");
			} else {
				// 已经绑定过
				ServletActionContext.getResponse().getWriter()
						.println("邮箱已经绑定过，无需重复绑定！");
			}

			// 删除redis的激活码
			redisTemplate.delete(model.getTelephone());
		}*/
        ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
        String activecodeRedis = redisTemplate.opsForValue().get(model.getTelephone());
        if (activecodeRedis == null || !activecode.equals(activecodeRedis)){
            ServletActionContext.getResponse().getWriter().println("激活码无效,请重新登录绑定");
        }else{
            Customer customer = WebClient
                    .create("http://localhost:9002/crm_management/services"
                            + "/customerService/customer/telephone/"
                            + model.getTelephone())
                    .accept(MediaType.APPLICATION_JSON).get(Customer.class);
            if (customer.getType() == null || customer.getType() != 1) {
            // 没有绑定,进行绑定
            WebClient.create(
                    "http://localhost:9002/crm_management/services"
                            + "/customerService/customer/updatetype/"
                            + model.getTelephone()).get();
            ServletActionContext.getResponse().getWriter()
                    .println("邮箱绑定成功！");
            } else {
                // 已经绑定过
                ServletActionContext.getResponse().getWriter()
                        .println("邮箱已经绑定过，无需重复绑定！");
            }

        // 删除redis的激活码
        redisTemplate.delete(model.getTelephone());
        }


        return NONE;
	}
}
