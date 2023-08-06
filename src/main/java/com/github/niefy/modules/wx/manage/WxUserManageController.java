package com.github.niefy.modules.wx.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.dto.RequestDTO;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.service.WxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 用户表
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2020-03-07 13:55:23
 */
@RestController
@RequestMapping("/manage/wxUser")
@Api(tags = {"公众号粉丝-管理后台"})
public class WxUserManageController {
    @Autowired
    private WxUserService userService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:wxuser:list")
    @ApiOperation(value = "列表")
    public R list(@CookieValue String appid,@RequestParam Map<String, Object> params) {
        params.put("appid",appid);
        PageUtils page = new PageUtils(userService.queryPage(params));

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @PostMapping("/listByIds")
    @RequiresPermissions("wx:wxuser:list")
    @ApiOperation(value = "列表-ID查询")
    public R listByIds(@CookieValue String appid,@RequestBody String[] openids){
        List<WxUser> users = userService.listByIds(Arrays.asList(openids));
        return R.ok().put(users);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{openid}")
    @RequiresPermissions("wx:wxuser:info")
    @ApiOperation(value = "详情")
    public R info(@CookieValue String appid,@PathVariable("openid") String openid) {
        WxUser wxUser = userService.getOne(new QueryWrapper<WxUser>()
                .eq(StringUtils.hasText(appid), "appid", appid)
                .eq(StringUtils.hasText(openid), "openid", openid));
        return R.ok().put("wxUser", wxUser);
    }

    /**
     * 同步用户列表
     */
    @PostMapping("/syncWxUsers")
    @RequiresPermissions("wx:wxuser:save")
    @ApiOperation(value = "同步用户列表到数据库")
    public R syncWxUsers(@CookieValue String appid) {
        userService.syncWxUsers(appid);

        return R.ok("任务已建立");
    }



    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:wxuser:delete")
    @ApiOperation(value = "删除")
    public R delete(@CookieValue String appid,@RequestBody String[] ids) {
        userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/update")
    @RequiresPermissions("wx:wxuser:update")
    @ApiOperation(value = "修改额度")
    public R update(@CookieValue String appid, @RequestBody RequestDTO requestDTO) {
        if (null != requestDTO.getId()) {
            WxUser.ExtraInfo extraInfo = new WxUser.ExtraInfo(requestDTO.getCount(), requestDTO.getImgCount(), requestDTO.getEnableGpt4());
            userService.updateUserOpenAiCount(requestDTO.getId(), appid, extraInfo);
        }
        return R.ok();
    }

}
