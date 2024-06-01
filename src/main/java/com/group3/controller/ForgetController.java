package com.group3.controller;

import com.group3.pojo.UserInformation;
import com.group3.pojo.UserPassword;
import com.group3.response.BaseResponse;
import com.group3.service.UserInformationService;
import com.group3.service.UserPasswordService;
import com.group3.tool.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author group3
 * @date 2024/04
 */
@RestController
public class ForgetController {

    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private UserInformationService userInformationService;

    @RequestMapping(value = "checkCode.do", method = {RequestMethod.POST, RequestMethod.GET})
    public Map checkPhone(HttpServletRequest request, Model model,
                          @RequestParam String code, @RequestParam String token) {
        Map<String, Integer> map = new HashMap<>();
        String name = request.getParameter("name");
        if (!StringUtils.getInstance().isNullOrEmpty(name)) {
            request.getSession().setAttribute("name", name);
        }
        String checkCodeToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(checkCodeToken) || !checkCodeToken.equals(token)) {
            map.put("result", 0);
            return map;
        }
        // Verification code error
        if (!checkCodePhone(code, request)) {
            map.put("result", 0);
            return map;
        }
        map.put("result", 1);
        return map;
    }

    // Update password
    @RequestMapping("updatePassword.do")
    public BaseResponse updatePassword(HttpServletRequest request, Model model,
                                       @RequestParam String password, @RequestParam String token) {
        // Prevent double submission
        String updatePasswordToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(updatePasswordToken) || !updatePasswordToken.equals(token)) {
            return BaseResponse.fail();
        }
        String realPhone = (String) request.getSession().getAttribute("phone");
        UserPassword userPassword = new UserPassword();
        String newPassword = StringUtils.getInstance().getMD5(password);
        int uid;
        try {
            uid = userInformationService.selectIdByPhone(realPhone);
            if (uid == 0) {
                return BaseResponse.fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.fail();
        }
        int id = userPasswordService.selectByUid(uid).getId();
        userPassword.setId(id);
        userPassword.setUid(uid);
        userPassword.setModified(new Date());
        userPassword.setPassword(newPassword);
        int result;
        try {
            result = userPasswordService.updateByPrimaryKeySelective(userPassword);
        } catch (Exception e) {
            return BaseResponse.fail();
        }
        // Update failed
        if (result != 1) {
            return BaseResponse.fail();
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        request.getSession().setAttribute("userInformation", userInformation);
        return BaseResponse.success();
    }

    //check the phone`s code
    private boolean checkCodePhone(String codePhone, HttpServletRequest request) {
        String trueCodePhone = "1479";
        return codePhone.equals(trueCodePhone);
    }
}
