package com.neutech.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neutech.config.NeutechException;
import com.neutech.entity.Users;
import com.neutech.mapper.UsersMapper;
import com.neutech.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neutech.util.NeutechUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    PasswordEncoder passwordEncoder;
    @Override
    public IPage<Users> page(Integer pageNo, Integer pageSize, String name) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like("name", name);
        }
        wrapper.orderByAsc("id");
        return this.page(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public List<Users> getTopRanking() {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("score"); // 根据 score 排序
        wrapper.last("LIMIT 100"); // 获取前 100 名
        return this.list(wrapper);
    }


    @Override
    public String login(String email, String password) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        Users userFromDb = this.getOne(wrapper);
        if (null == userFromDb || !passwordEncoder.matches(password, userFromDb.getPassword())) {
            throw new NeutechException("用户名或密码错误");
        }

        // 调试信息
        System.out.println("数据库用户信息: " + userFromDb);
        System.out.println("用户IsAdmin: " + userFromDb.getIsAdmin());

        String token = NeutechUtil.encode(userFromDb);

        // 测试解析
        Users decodedUser = NeutechUtil.decode(token);
        System.out.println("Token解码后用户信息: " + decodedUser);
        if (decodedUser != null) {
            System.out.println("解码后IsAdmin: " + decodedUser.getIsAdmin());
        }

        return token;
    }


    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) {
        // 存储文件的路径
        String uploadDir = "D:/uploads/avatars";
        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + "/" + fileName;

        try {
            // 将文件保存到本地
            File dest = new File(filePath);
            file.transferTo(dest);

            // 返回头像的 URL
            String avatarUrl = "http://localhost:8080/avatars/" + fileName;

            // 获取用户并更新头像 URL
            Users user = this.getById(userId);  // 根据 userId 获取用户
            if (user != null) {
                user.setAvatarUrl(avatarUrl);  // 设置头像 URL
                this.updateById(user);  // 更新数据库中的头像路径
            }

            return avatarUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new NeutechException("头像上传失败");
        }
    }

    @Override
    public void register(Users user) {
        // 检查邮箱是否已存在
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("email", user.getEmail());
        if (this.getOne(wrapper) != null) {
            throw new NeutechException("该邮箱已被注册");
        }

        // 角色映射：将英文角色转换为数据库中的中文枚举值
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("学生"); // 默认角色
        } else {
            String role = user.getRole().trim();
            switch (role.toLowerCase()) {
                case "teacher":
                case "老师":
                    user.setRole("老师");
                    break;
                case "student":
                case "user":
                case "学生":
                default:
                    user.setRole("学生");
                    break;
            }
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认头像 URL（如果用户未上传头像）
        String avatarUrl = "D:\\default_avatar\\默认头像.png";  // 默认头像
        user.setAvatarUrl(avatarUrl);

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // 保存用户
        this.save(user);
    }
    @Override
    public Integer getPersonalRanking(Integer userId) {
        return this.baseMapper.getPersonalRanking(userId);
    }
}
