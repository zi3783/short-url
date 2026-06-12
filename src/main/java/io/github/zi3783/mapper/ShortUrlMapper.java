package io.github.zi3783.mapper;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.zi3783.entity.ShortUrlMap;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShortUrlMapper extends BaseMapper<ShortUrlMap> {
}
