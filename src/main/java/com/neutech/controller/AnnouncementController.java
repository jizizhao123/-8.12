// src/main/java/com/neutech/controller/AnnouncementController.java

package com.neutech.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neutech.entity.Announcement;
import com.neutech.entity.Users;
import com.neutech.service.AnnouncementService;
import com.neutech.service.UsersService;
import com.neutech.util.NeutechUtil;
import com.neutech.vo.ResultJson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UsersService usersService;

    @PostMapping("/add")
    public ResultJson<String> addAnnouncement(@RequestParam String title,
                                              @RequestParam String content) {
        // 获取当前用户信息
        Users currentUser = getCurrentUserFromToken();
        if (currentUser == null) {
            return ResultJson.failed("用户未登录或token无效");
        }

        // 使用用户的number作为公告的number
        String userNumber = currentUser.getNumber(); // 改为获取用户的number字段

        // 发布公告
        announcementService.addAnnouncement(userNumber, title, content);
        return ResultJson.success("公告发布成功");
    }

    // 获取所有公告（不分页，保持原有接口）
    @GetMapping("/list")
    public ResultJson<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncementsWithUserInfo();
        return ResultJson.success(announcements);
    }

    // 分页获取公告列表（新接口）
    @GetMapping("/list/page")
    public ResultJson<IPage<Announcement>> getAnnouncementsByPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 创建分页对象
            IPage<Announcement> page = new Page<>(pageNo, pageSize);

            // 调用Service进行分页查询
            IPage<Announcement> result = announcementService.getAnnouncementsByPage(page);

            return ResultJson.success(result, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failed("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public ResultJson<Announcement> getAnnouncementDetail(@PathVariable Integer id) {
        Announcement announcement = announcementService.getAnnouncementWithUserInfoById(id);
        if (announcement == null) {
            return ResultJson.failed("公告不存在");
        }
        return ResultJson.success(announcement);
    }

    // 新增：获取发布者详细信息
    @GetMapping("/publisher/info/{number}")
    public ResultJson<Users> getPublisherInfo(@PathVariable String number) {
        try {
            // 添加调试信息
            System.out.println("请求获取发布者信息，用户编号: " + number);

            // 检查参数
            if (number == null || number.trim().isEmpty()) {
                System.out.println("用户编号为空");
                return ResultJson.failed("用户编号不能为空");
            }

            // 根据用户编号获取用户信息
            Users user = usersService.getByNumber(number.trim());
            System.out.println("查询到的用户信息: " + user);

            if (user == null) {
                System.out.println("未找到编号为 " + number + " 的用户");
                return ResultJson.failed("用户不存在");
            }

            // 隐藏敏感信息（如密码）
            user.setPassword(null);

            return ResultJson.success(user);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取发布者信息异常: " + e.getMessage());
            return ResultJson.failed("获取发布者信息失败：" + e.getMessage());
        }
    }

    // 从token中获取当前用户信息的辅助方法
    private Users getCurrentUserFromToken() {
        // 获取当前HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从请求头中获取token
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // 去掉 "Bearer " 前缀获取实际token
                String token = authorizationHeader.substring(7);
                // 使用NeutechUtil解码token获取用户信息
                Users user = NeutechUtil.decode(token);
                // 重新从数据库获取最新用户信息（可选）
                if (user != null && user.getId() != null) {
                    return this.usersService.getById(user.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
