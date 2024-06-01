package com.group3.controller;

import cn.hutool.core.io.FileUtil;
import com.group3.bean.GoodsCarBean;
import com.group3.bean.ShopInformationBean;
import com.group3.bean.UserWantBean;
import com.group3.config.FileConfig;
import com.group3.pojo.*;
import com.group3.response.BaseResponse;
import com.group3.service.*;
import com.group3.token.TokenProccessor;
import com.group3.tool.SaveSession;
import com.group3.tool.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author group3
 * @date 2024/04
 */
@Controller
@Slf4j
public class UserController {

    @Resource
    private UserInformationService userInformationService;
    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private UserCollectionService userCollectionService;
    @Resource
    private UserReleaseService userReleaseService;
    @Resource
    private BoughtShopService boughtShopService;
    @Resource
    private UserWantService userWantService;
    @Resource
    private ShopCarService shopCarService;
    @Resource
    private OrderFormService orderFormService;
    @Resource
    private GoodsOfOrderFormService goodsOfOrderFormService;
    @Resource
    private UserStateService userStateService;
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private GoodsCarService goodsCarService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;
    @Autowired
    private FileConfig fileConfig;

    //Enter the login page
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String token = TokenProccessor.getInstance().makeToken();
        log.info("Enter the login page，token:" + token);
        request.getSession().setAttribute("token", token);
        model.addAttribute("token", token);
        return "page/login_page";
    }

    //Logout
    @RequestMapping(value = "/logout.do")
    public String logout(HttpServletRequest request) {
        try {
            request.getSession().removeAttribute("userInformation");
            request.getSession().removeAttribute("uid");
            System.out.println("logout");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home.do";
        }
        return "redirect:/";
    }

    //User registration
    @RequestMapping(value = "/registered.do", method = RequestMethod.POST)
    public String registered(Model model,
                             @RequestParam String name, @RequestParam String phone, @RequestParam String password) {
        UserInformation userInformation = new UserInformation();
        userInformation.setUsername(name);
        userInformation.setPhone(phone);
        userInformation.setModified(new Date());
        userInformation.setCreatetime(new Date());
        if (userInformationService.insertSelective(userInformation) == 1) {
            int uid = userInformationService.selectIdByPhone(phone);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            password = StringUtils.getInstance().getMD5(password);
            userPassword.setPassword(password);
            userPassword.setUid(uid);
            int result = userPasswordService.insertSelective(userPassword);
            if (result != 1) {
                model.addAttribute("result", "fail");
                return "success";
            }
            model.addAttribute("result", "success");
            return "success";
        }
        model.addAttribute("result", "fail");
        return "success";
    }

    //login certification
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public String login(HttpServletRequest request,
                        @RequestParam String phone, @RequestParam String password, @RequestParam String token) {
        String loginToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(phone) || StringUtils.getInstance().isNullOrEmpty(password)) {
            return "redirect:/login.do";
        }
        //Prevent double submission
        if (StringUtils.getInstance().isNullOrEmpty(token) || !token.equals(loginToken)) {
            return "redirect:/login.do";
        }
        boolean b = getId(phone, password, request);
        //Failed - the mobile number does not exist
        if (!b) {
            return "redirect:/login.do?msg=the mobile number does not exist";
        }
        return "redirect:/";
    }

    //View basic user information
    @RequestMapping(value = "/personal_info.do")
    public String personalInfo(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String personalInfoToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("personalInfoToken", personalInfoToken);
        model.addAttribute("token", personalInfoToken);
        model.addAttribute("userInformation", userInformation);
        return "page/personal/personal_info";
    }


    //Improve the basic user information
    @RequestMapping(value = "/certification.do", method = RequestMethod.POST)
    @ResponseBody
    public Map certification(HttpServletRequest request,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String realName,
                             @RequestParam(required = false) String clazz, @RequestParam String token,
                             @RequestParam(required = false) String sno, @RequestParam(required = false) String dormitory,
                             @RequestParam(required = false) String gender) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        Map<String, Integer> map = new HashMap<>();
        map.put("result", 0);
        //The user has not logged in
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return map;
        }
        String certificationToken = (String) request.getSession().getAttribute("personalInfoToken");
        //Prevent double submission
//        boolean b = token.equals(certificationToken);
        if (StringUtils.getInstance().isNullOrEmpty(certificationToken)) {
            return map;
        } else {
            request.getSession().removeAttribute("certificationToken");
        }
        if (userName != null && userName.length() < 25) {
            userName = StringUtils.getInstance().replaceBlank(userName);
            userInformation.setUsername(userName);
        } else if (userName != null && userName.length() >= 25) {
            return map;
        }
        if (realName != null && realName.length() < 25) {
            realName = StringUtils.getInstance().replaceBlank(realName);
            userInformation.setRealname(realName);
        } else if (realName != null && realName.length() >= 25) {
            return map;
        }
        if (clazz != null && clazz.length() < 25) {
            clazz = StringUtils.getInstance().replaceBlank(clazz);
            userInformation.setClazz(clazz);
        } else if (clazz != null && clazz.length() >= 25) {
            return map;
        }
        if (sno != null && sno.length() < 25) {
            sno = StringUtils.getInstance().replaceBlank(sno);
            userInformation.setSno(sno);
        } else if (sno != null && sno.length() >= 25) {
            return map;
        }
        if (dormitory != null && dormitory.length() < 25) {
            dormitory = StringUtils.getInstance().replaceBlank(dormitory);
            userInformation.setDormitory(dormitory);
        } else if (dormitory != null && dormitory.length() >= 25) {
            return map;
        }
        if (gender != null && gender.length() <= 2) {
            gender = StringUtils.getInstance().replaceBlank(gender);
            userInformation.setGender(gender);
        } else if (gender != null && gender.length() > 2) {
            return map;
        }
        int result = userInformationService.updateByPrimaryKeySelective(userInformation);
        if (result != 1) {
            // Update failed. Authentication failed
            return map;
        }
        //successful authentication
        request.getSession().setAttribute("userInformation", userInformation);
        map.put("result", 1);
        return map;
    }

    //enter the publishUserWant.do.html
    @RequestMapping(value = "/require_product.do")
    public String enterPublishUserWant(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String error = request.getParameter("error");
        if (!StringUtils.getInstance().isNullOrEmpty(error)) {
            model.addAttribute("error", "error");
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        return "page/require_product";
    }

    //Modify the items purchased
    @RequestMapping(value = "/modified_require_product.do")
    public String modifiedRequireProduct(HttpServletRequest request, Model model,
                                         @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        UserWant userWant = userWantService.selectByPrimaryKey(id);
        model.addAttribute("userWant", userWant);
        String sort = getSort(userWant.getSort());
        model.addAttribute("sort", sort);
        return "page/modified_require_product";
    }

    //publish userWant
    @RequestMapping(value = "/publishUserWant.do")
//    @ResponseBody
    public String publishUserWant(HttpServletRequest request, Model model,
                                  @RequestParam String name,
                                  @RequestParam int sort, @RequestParam int quantity,
                                  @RequestParam double price, @RequestParam String remark,
                                  @RequestParam String token) {
//        Map<String, Integer> map = new HashMap<>();
        //determine whether the user exits
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //if the user no exits in the session,
//            map.put("result", 2);
            return "redirect:/login.do";
        }
        String publishUserWantToke = (String) request.getSession().getAttribute("publishUserWantToken");
        if (StringUtils.getInstance().isNullOrEmpty(publishUserWantToke) || !publishUserWantToke.equals(token)) {
//            map.put("result", 2);
            return "redirect:require_product.do?error=3";
        } else {
            request.getSession().removeAttribute("publishUserWantToken");
        }
//        name = StringUtils.replaceBlank(name);
//        remark = StringUtils.replaceBlank(remark);
//        name = StringUtils.getInstance().txtReplace(name);
//        remark = StringUtils.getInstance().txtReplace(remark);
        try {
            if (name.length() < 1 || remark.length() < 1 || name.length() > 25 || remark.length() > 25) {
                return "redirect:require_product.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:require_product.do?error=1";
        }
        UserWant userWant = new UserWant();
        userWant.setModified(new Date());
        userWant.setName(name);
        userWant.setPrice(new BigDecimal(price));
        userWant.setQuantity(quantity);
        userWant.setRemark(remark);
        userWant.setUid((Integer) request.getSession().getAttribute("uid"));
        userWant.setSort(sort);
        int result;
        try {
            result = userWantService.insertSelective(userWant);
            if (result != 1) {
//                map.put("result", result);
                return "redirect:/require_product.do?error=2";
            }
        } catch (Exception e) {
            e.printStackTrace();
//            map.put("result", result);
            return "redirect:/require_product.do?error=2";
        }
//        map.put("result", result);
        return "redirect:/my_require_product.do";
    }

    //getUserWant
    @RequestMapping(value = {"/my_require_product.do", "/my_require_product_page.do"})
    public String getUserWant(HttpServletRequest request, Model model) {
        List<UserWant> list;
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        try {
            int uid = (int) request.getSession().getAttribute("uid");
//            list = selectUserWantByUid(4);
            list = selectUserWantByUid(uid);
            List<UserWantBean> userWantBeans = new ArrayList<>();
            for (UserWant userWant : list) {
                UserWantBean userWantBean = new UserWantBean();
                userWantBean.setId(userWant.getId());
                userWantBean.setModified(userWant.getModified());
                userWantBean.setName(userWant.getName());
                userWantBean.setPrice(userWant.getPrice().doubleValue());
                userWantBean.setUid(uid);
                userWantBean.setQuantity(userWant.getQuantity());
                userWantBean.setRemark(userWant.getRemark());
                userWantBean.setSort(getSort(userWant.getSort()));
                userWantBeans.add(userWantBean);
            }
            model.addAttribute("userWant", userWantBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        model.addAttribute("userInformation", userInformation);
        return "page/personal/my_require_product_page";
    }

    //getUserWantCounts.do
    @RequestMapping(value = "/getUserWantCounts.do")
    @ResponseBody
    public Map getUserWantCounts(HttpServletRequest request, Model model) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        try {
            int counts = getUserWantCounts((Integer) request.getSession().getAttribute("uid"));
            map.put("counts", counts);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("counts", -1);
        }
        return map;
    }

    //Delete purchase
    @RequestMapping(value = "/deleteUserWant.do")
    public String deleteUserWant(HttpServletRequest request, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return "redirect:/login.do";
        }
        UserWant userWant = new UserWant();
        userWant.setId(id);
        userWant.setDisplay(0);
        try {
            int result = userWantService.updateByPrimaryKeySelective(userWant);
            if (result != 1) {
                return "redirect:my_require_product.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:my_require_product.do";
    }

    //add the userCollection
    @RequestMapping(value = "/addUserCollection.do")
    @ResponseBody
    public BaseResponse addUserCollection(HttpServletRequest request, @RequestParam int sid) {
        //determine whether the user exits
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            //if the user no exits in the session,
            return BaseResponse.fail();
        }
        UserCollection userCollection = new UserCollection();
        userCollection.setModified(new Date());
        userCollection.setSid(sid);
        userCollection.setUid((Integer) request.getSession().getAttribute("uid"));
        //begin insert the userCollection
        int result = userCollectionService.insertSelective(userCollection);
        if (result != 1) {
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }


    // delete the userCollection
    @RequestMapping(value = "/deleteUserCollection.do")
    @ResponseBody
    public BaseResponse deleteUserCollection(HttpServletRequest request, @RequestParam int ucid) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return BaseResponse.fail();
        }
        UserCollection userCollection = new UserCollection();
//        userCollection.setUid((Integer) request.getSession().getAttribute("uid"));
//        userCollection.setSid(sid);
        userCollection.setId(ucid);
        userCollection.setModified(new Date());
        userCollection.setDisplay(0);
        int result;
        result = userCollectionService.updateByPrimaryKeySelective(userCollection);
        if (result != 1) {
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }

    //getShopCarCounts.do
    @RequestMapping(value = "/getShopCarCounts.do")
    @ResponseBody
    public BaseResponse getShopCarCounts(HttpServletRequest request) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return BaseResponse.fail();
        }
        int uid = (int) request.getSession().getAttribute("uid");
        int counts = getShopCarCounts(uid);
        return BaseResponse.success();
    }

    //check the shopping cart
    @RequestMapping(value = "/shopping_cart.do")
    public String selectShopCar(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
//            list.add(shopCar);
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = userInformation.getId();
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uid);
        List<GoodsCarBean> goodsCarBeans = new ArrayList<>();
        for (GoodsCar goodsCar : goodsCars) {
            GoodsCarBean goodsCarBean = new GoodsCarBean();
            goodsCarBean.setUid(goodsCar.getUid());
            goodsCarBean.setSid(goodsCar.getSid());
            goodsCarBean.setModified(goodsCar.getModified());
            goodsCarBean.setId(goodsCar.getId());
            goodsCarBean.setQuantity(goodsCar.getQuantity());
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
            goodsCarBean.setName(shopInformation.getName());
            goodsCarBean.setRemark(shopInformation.getRemark());
            goodsCarBean.setImage(shopInformation.getImage());
            goodsCarBean.setPrice(shopInformation.getPrice().doubleValue());
            goodsCarBean.setSort(getSort(shopInformation.getSort()));
            goodsCarBeans.add(goodsCarBean);
        }
        model.addAttribute("list", goodsCarBeans);
        return "page/shopping_cart";
    }

//    //Get the items in the cart by the id of the cart
//    @RequestMapping(value = "/selectGoodsOfShopCar")
//    @ResponseBody
//    public List<GoodsCar> selectGoodsCar(HttpServletRequest request) {
//        List<GoodsCar> list = new ArrayList<>();
//        GoodsCar goodsCar = new GoodsCar();
//        if (Empty.isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
//            list.add(goodsCar);
//            return list;
//        }
//        try {
//            int scid = shopCarService.selectByUid((Integer) request.getSession().getAttribute("uid")).getId();
//            list = goodsCarService.selectByUid(scid);
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return list;
//        }
//    }

    //Add to shopping cart
    @RequestMapping(value = "/insertGoodsCar.do")
    @ResponseBody
    public BaseResponse insertGoodsCar(HttpServletRequest request, @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail();
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(1);
        goodsCar.setModified(new Date());
        goodsCar.setQuantity(1);
        goodsCar.setUid(uid);
        goodsCar.setSid(id);
        int result = goodsCarService.insertSelective(goodsCar);
        return BaseResponse.success();
    }


    //Delete the items in cart
    @RequestMapping(value = "/deleteShopCar.do")
    @ResponseBody
    public BaseResponse deleteShopCar(HttpServletRequest request, @RequestParam int id, @RequestParam int sid) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail();
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(0);
        goodsCar.setId(id);
        goodsCar.setSid(sid);
        goodsCar.setUid(uid);
        int result = goodsCarService.updateByPrimaryKeySelective(goodsCar);
        if (result != 1) {
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }

    //Release products
    @RequestMapping(value = "/insertGoods.do", method = RequestMethod.POST)
    public String insertGoods(@RequestParam String name, @RequestParam int level,
                              @RequestParam String remark, @RequestParam double price,
                              @RequestParam int sort, @RequestParam int quantity,
                              @RequestParam String token, @RequestParam(required = false) MultipartFile image,
                              @RequestParam int action, @RequestParam(required = false) int id,
                              HttpServletRequest request, Model model) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
//        String publishProductToken = TokenProccessor.getInstance().makeToken();
//        request.getSession().setAttribute("token",publishProductToken);
        //Prevent double submission
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !goodsToken.equals(token)) {
            return "redirect:publish_product.do?error=1";
        } else {
            request.getSession().removeAttribute("goodsToken");
        }
        //Get basic information about the user from the session
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        model.addAttribute("userInformation", userInformation);
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //If the user does not exist
            return "redirect:/login.do";
        }
        name = StringUtils.getInstance().replaceBlank(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        //judge the data`s format
        if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(level) || StringUtils.getInstance().isNullOrEmpty(remark) || StringUtils.getInstance().isNullOrEmpty(price)
                || StringUtils.getInstance().isNullOrEmpty(sort) || StringUtils.getInstance().isNullOrEmpty(quantity) || name.length() > 25 || remark.length() > 122) {
            model.addAttribute("message", "Please enter the correct format!!!");
            model.addAttribute("token", goodsToken);
            request.getSession().setAttribute("goodsToken", goodsToken);
            return "page/publish_product";
        }
        //insert
        if (action == 1) {
            if (StringUtils.getInstance().isNullOrEmpty(image)) {
                model.addAttribute("message", "Please select a picture!!!");
                model.addAttribute("token", goodsToken);
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "redirect:publish_product.do?error=please select a picture";
            }
            String random;
            String path = fileConfig.getFileSave() + ":\\", save;
            random = "toImage\\" + StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
            StringBuilder thumbnails = new StringBuilder();
            thumbnails.append(path);
            thumbnails.append("toImage/thumbnails/");
            StringBuilder group3 = new StringBuilder();
            group3.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
            thumbnails.append(group3);
//        String fileName = "\\" + random + ".jpg";
            File file = new File(path, random);
            if (!file.exists()) {
                FileUtil.mkdir(file);
//                file.mkdir();
            }
            try {
                image.transferTo(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            String pornograp = Pornographic.CheckPornograp("D:\\" + random);
            if (pornograp.equals("pornography")) {
                return "redirect:publish_product?error=Do not use pornographic images";
            }
            if (!OCR.isOk2(pornograp)) {
                return "redirect:publish_product?error=images should not contain sensitive text";
            }*/
            if (StringUtils.getInstance().thumbnails(path + random, thumbnails.toString())) {
                save = "/toImage/thumbnails/" + group3;
            } else {
                return "redirect:publish_product.do?error=failed to generate thumbnail";
            }
            //begin insert the shopInformation to the MySQL
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setName(name);
            shopInformation.setLevel(level);
            shopInformation.setRemark(remark);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(sort);
            shopInformation.setQuantity(quantity);
            shopInformation.setModified(new Date());
            shopInformation.setImage(random);//This is the other uniquely identifies
            shopInformation.setThumbnails(save);
//        shopInformation.setUid(4);
            int uid = (int) request.getSession().getAttribute("uid");
            shopInformation.setUid(uid);
            try {
                int result = shopInformationService.insertSelective(shopInformation);
                //Insert failed
                if (result != 1) {
                    model.addAttribute("message", "Please enter the correct format!!!");
                    model.addAttribute("token", goodsToken);
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "Please enter the correct format!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            int sid = shopInformationService.selectIdByImage(random);// get the id which is belongs shopInformation
            // Insert the number of the published item into the user's publication
            UserRelease userRelease = new UserRelease();
            userRelease.setModified(new Date());
            userRelease.setSid(sid);
            userRelease.setUid(uid);
            try {
                int result = userReleaseService.insertSelective(userRelease);
                // If the association fails, delete the corresponding product and product image
                if (result != 1) {
                    //if insert failure,transaction rollback.
                    shopInformationService.deleteByPrimaryKey(sid);
//                shopPictureService.deleteByPrimaryKey(spid);
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "Please enter the correct format!!!");
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                //if insert failure,transaction rollback.
                shopInformationService.deleteByPrimaryKey(sid);
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "Please enter the correct format!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            shopInformation.setId(sid);
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("userInformation", userInformation);
            String sb = getSort(sort);
            model.addAttribute("sort", sb);
            model.addAttribute("action", 2);
            return "redirect:/my_publish_product_page.do";
        } else if (action == 2) {//update products
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setModified(new Date());
            shopInformation.setQuantity(quantity);
            shopInformation.setSort(sort);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setRemark(remark);
            shopInformation.setLevel(level);
            shopInformation.setName(name);
            shopInformation.setId(id);
            try {
                int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result != 1) {
                    return "redirect:publish_product.do";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:publish_product.do";
            }
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("action", 2);
            model.addAttribute("sort", getSort(sort));
        }
        return "redirect:/my_publish_product_page.do";
    }

    // Jump directly from the published item to the modified item
    @RequestMapping(value = "/modifiedMyPublishProduct.do")
    public String modifiedMyPublishProduct(HttpServletRequest request, Model model,
                                           @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("token", goodsToken);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        model.addAttribute("userInformation", userInformation);
        model.addAttribute("shopInformation", shopInformation);
        model.addAttribute("action", 2);
        model.addAttribute("sort", getSort(shopInformation.getSort()));
        return "page/publish_product";
    }

    //Post messages
    @RequestMapping(value = "/insertShopContext.do")
    @ResponseBody
    public Map insertShopContext(@RequestParam int id, @RequestParam String context, @RequestParam String token,
                                 HttpServletRequest request) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        Map<String, String> map = new HashMap<>();
        map.put("result", "1");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            map.put("result", "2");
            return map;
        }
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !token.equals(goodsToken)) {
            return map;
        }
        ShopContext shopContext = new ShopContext();
        shopContext.setContext(context);
        Date date = new Date();
        shopContext.setModified(date);
        shopContext.setSid(id);
        int uid = (int) request.getSession().getAttribute("uid");
        shopContext.setUid(uid);
        try {
            int result = shopContextService.insertSelective(shopContext);
            if (result != 1) {
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
        map.put("result", "1");
        map.put("username", userInformation.getUsername());
        map.put("context", context);
        map.put("time", StringUtils.getInstance().DateToString(date));
        return map;
    }

    //Item Down shelf
    @RequestMapping(value = "/deleteShop.do")
    public String deleteShop(HttpServletRequest request, Model model, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        ShopInformation shopInformation = new ShopInformation();
        shopInformation.setModified(new Date());
        shopInformation.setDisplay(0);
        shopInformation.setId(id);
        try {
            int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
            if (result != 1) {
                return "redirect:my_publish_product_page.do";
            }
            return "redirect:my_publish_product_page.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:my_publish_product_page.do";
        }
    }

    //View the total number of all items published
    @RequestMapping(value = "/getReleaseShopCounts.do")
    @ResponseBody
    public Map getReleaseShopCounts(HttpServletRequest request) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        int counts = getReleaseCounts((Integer) request.getSession().getAttribute("uid"));
        map.put("counts", counts);
        return map;
    }

    //View user posted merchandise
    @RequestMapping(value = "/my_publish_product_page.do")
    public String getReleaseShop(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = (int) request.getSession().getAttribute("uid");
        List<ShopInformation> shopInformations = shopInformationService.selectUserReleaseByUid(uid);
        List<ShopInformationBean> list = new ArrayList<>();
        String stringBuffer;
//            int i=0;
        for (ShopInformation shopInformation : shopInformations) {
//                if (i>=5){
//                    break;
//                }
//                i++;
            stringBuffer = getSort(shopInformation.getSort());
            ShopInformationBean shopInformationBean = new ShopInformationBean();
            shopInformationBean.setId(shopInformation.getId());
            shopInformationBean.setName(shopInformation.getName());
            shopInformationBean.setLevel(shopInformation.getLevel());
            shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
            shopInformationBean.setRemark(shopInformation.getRemark());
            shopInformationBean.setSort(stringBuffer);
            shopInformationBean.setQuantity(shopInformation.getQuantity());
            shopInformationBean.setTransaction(shopInformation.getTransaction());
            shopInformationBean.setUid(shopInformation.getUid());
            shopInformationBean.setImage(shopInformation.getImage());
            list.add(shopInformationBean);
        }
        model.addAttribute("shopInformationBean", list);
        return "page/personal/my_publish_product_page";
    }

    //Update product details
    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        sb.append(allKinds.getName());
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }

    //View the total number of items in the user's collection
    private int getCollectionCounts(int uid) {
        int counts;
        try {
            counts = userCollectionService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return counts;
    }

    //View collections, 10 at a time
    private List<UserCollection> selectContectionByUid(int uid, int start) {
        try {
            return userCollectionService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserCollection> list = new ArrayList<>();
            list.add(new UserCollection());
            return list;
        }
    }

    //View the total number of items posted by users
    private int getReleaseCounts(int uid) {
        try {
            return userReleaseService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //View posted shipments, 10 at a time
    private List<UserRelease> selectReleaseByUid(int uid, int start) {
        try {
            return userReleaseService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserRelease> list = new ArrayList<>();
            list.add(new UserRelease());
            return list;
        }
    }

    //View the total number of items purchased by the user
    private int getBoughtShopCounts(int uid) {
        try {
            return boughtShopService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //View user purchases, 10 at a time
    private List<BoughtShop> selectBoughtShopByUid(int uid, int start) {
        try {
            return boughtShopService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<BoughtShop> list = new ArrayList<>();
            list.add(new BoughtShop());
            return list;
        }
    }

    //View the total number of purchases made by a user
    private int getUserWantCounts(int uid) {
        try {
            return userWantService.getCounts(uid);
        } catch (Exception e) {
            return -1;
        }
    }

    //Order list
    private List<UserWant> selectUserWantByUid(int uid) {
        try {
            return userWantService.selectMineByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserWant> list = new ArrayList<>();
            list.add(new UserWant());
            return list;
        }
    }

    //My total shopping cart
    private int getShopCarCounts(int uid) {
        try {
            return shopCarService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Shopping cart list
    private ShopCar selectShopCarByUid(int uid) {
        try {
            return shopCarService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
//            List<ShopCar> list
            return new ShopCar();
        }
    }

    //View total orders
    private int getOrderFormCounts(int uid) {
        try {
            return orderFormService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Order list, 10 at a time
    private List<OrderForm> selectOrderFormByUid(int uid, int start) {
        try {
            return orderFormService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<OrderForm> list = new ArrayList<>();
            list.add(new OrderForm());
            return list;
        }
    }

    //Goods in order
    private List<GoodsOfOrderForm> selectGoodsOfOrderFormByOFid(int ofid) {
        try {
            return goodsOfOrderFormService.selectByOFid(ofid);
        } catch (Exception e) {
            e.printStackTrace();
            List<GoodsOfOrderForm> list = new ArrayList<>();
            list.add(new GoodsOfOrderForm());
            return list;
        }
    }

    //View the user status
    private UserState selectUserStateByUid(int uid) {
        try {
            return userStateService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserState();
        }
    }

    //Check whether the mobile phone number and its password correspond to each other
    private boolean getId(String phone, String password, HttpServletRequest request) {
        int uid = userInformationService.selectIdByPhone(phone);
        if (uid == 0 || StringUtils.getInstance().isNullOrEmpty(uid)) {
            return false;
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        if (null == userInformation) {
            return false;
        }
        password = StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            return false;
        }
        request.getSession().setAttribute("userInformation", userInformation);
        request.getSession().setAttribute("uid", uid);
        SaveSession.getInstance().save(phone, System.currentTimeMillis());
        return true;
    }

    // Get the most detailed third level classification
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    // Get the second level classification
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    // Get the second level classification
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    public void save(ShopInformation shopInformation, UserRelease userRelease) {
        shopInformationService.insertSelective(shopInformation);
        userReleaseService.insertSelective(userRelease);
    }

    //release products
    @RequestMapping(value = "/test")
    public String insertGoods() {

        Random random = new Random();
        ShopInformation shopInformation;
        UserRelease userRelease;
        int level, uid, quantity;
        double price;
        for (int i = 1, k = 1, j = 189; i < 1000; i++, j++, k++) {
            if (k > 94) {
                k = 1;
            }
            level = random.nextInt(10) + 1;
            price = Math.random() * 1000 + 1;
            quantity = random.nextInt(10) + 1;
            uid = random.nextInt(100) + 1;
            shopInformation = new ShopInformation();
            shopInformation.setId(j);
            shopInformation.setName("One Hundred Years of Solitude");
            shopInformation.setModified(new Date());
            shopInformation.setLevel(level);
            shopInformation.setRemark("See this product please contact me! Wechat: test");
//            double price = Math.random()*1000.00+1;
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(k);
            shopInformation.setQuantity(quantity);
            shopInformation.setImage("/image/QyBHYiMfYQ4XZFCqxEv0.jpg");
//            int uid = random.nextInt(100)+1;
            shopInformation.setUid(uid);
//            userRelease = new UserRelease();
//            userRelease.setUid(uid);
//            userRelease.setSid(j);
//            userRelease.setModified(new Date());
//            userRelease.setDisplay(1);
            shopInformationService.updateByPrimaryKeySelective(shopInformation);
//            userReleaseService.insertSelective(userRelease);
        }
        System.out.println("success");
        return "page/publish_product";
    }
}
