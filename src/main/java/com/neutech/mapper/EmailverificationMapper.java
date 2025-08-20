package com.neutech.mapper;

import com.neutech.entity.Emailverification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jzz
 * @since 2025-08-12
 */
@Repository
@Mapper
public interface EmailverificationMapper extends BaseMapper<Emailverification> {

}
