package com.neutech.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neutech.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
public interface UsersService extends IService<Users> {
    IPage<Users> page(Integer pageNo, Integer pageSize, String name);

    List<Users> getTopRanking();
    String login(String email, String password);
    void register(Users user);
    String uploadAvatar(MultipartFile file, Integer userId);
    Integer getPersonalRanking(Integer userId);
}
