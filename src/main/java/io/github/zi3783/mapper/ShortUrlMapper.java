package io.github.zi3783.mapper;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.zi3783.entity.ShortUrlMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShortUrlMapper extends BaseMapper<ShortUrlMap> {
    /**
     * 使visitedCount++
     * @param id
     * @param sum
     */
    @Update("update short_url_map set visited_count = visited_count + #{sum} where id = #{id}")
    void incrementVisitedCount(Long id, Long sum);
}
