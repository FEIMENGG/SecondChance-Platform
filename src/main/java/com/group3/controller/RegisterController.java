package com.group3.controller;

import com.group3.pojo.UserInformation;
import com.group3.pojo.UserPassword;
import com.group3.response.BaseResponse;
import com.group3.service.UserInformationService;
import com.group3.service.UserPasswordService;
import com.group3.tool.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author group3
 * @date 2024/04
 */
@Controller
public class RegisterController {
    @Resource
    private UserPasswordService userPasswordService;

    @Resource
    private UserInformationService userInformationService;

    //Start registering users
    @RequestMapping("/insertUser.do")
    @ResponseBody
    public BaseResponse insertUser(HttpServletRequest request,
                                   @RequestParam String password, @RequestParam String token) {
        //Store the phone number with the session
        String realPhone = (String) request.getSession().getAttribute("phone");
        //token - unique identification
        String insertUserToken = (String) request.getSession().getAttribute("token");
        //Prevent double submission
        if (StringUtils.getInstance().isNullOrEmpty(insertUserToken) || !insertUserToken.equals(token)) {
            return BaseResponse.fail();
        }
        //The phone number already exists
        int uid = userInformationService.selectIdByPhone(realPhone);
        if (uid != 0) {
            return BaseResponse.fail();
        }

        //user details
        UserInformation userInformation = new UserInformation();
        userInformation.setPhone(realPhone);
        userInformation.setCreatetime(new Date());
        String username = (String) request.getSession().getAttribute("name");
        userInformation.setUsername(username);
        userInformation.setModified(new Date());
        int result;
        result = userInformationService.insertSelective(userInformation);
        // If the basic user information is successfully written
        if (result == 1) {
            uid = userInformationService.selectIdByPhone(realPhone);
            String newPassword = StringUtils.getInstance().getMD5(password);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            userPassword.setUid(uid);
            userPassword.setPassword(newPassword);
            result = userPasswordService.insertSelective(userPassword);
            // Failed to write the password
            if (result != 1) {
                userInformationService.deleteByPrimaryKey(uid);
                return BaseResponse.fail();
            } else {
                // Registration is successful
                userInformation = userInformationService.selectByPrimaryKey(uid);
                request.getSession().setAttribute("userInformation", userInformation);
                return BaseResponse.success();
            }
        }
        return BaseResponse.fail();
    }
}
