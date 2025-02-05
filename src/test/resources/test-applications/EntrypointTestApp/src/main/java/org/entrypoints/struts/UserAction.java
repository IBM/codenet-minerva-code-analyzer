package org.entrypoints.struts;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

@Namespace("/user")
public class UserAction extends ActionSupport {

    @Action(value = "list",
            results = {
                @Result(name = "success", location = "/user/list.jsp")})
    public String listUsers() {
        return SUCCESS;
    }

    @Action(value = "add",
            results = {
                @Result(name = "success", location = "/user/add.jsp")})
    public String addUser() {
        return SUCCESS;
    }
}
