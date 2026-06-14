package io.github.zi3783.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.zi3783.entity.ShortUrlMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShortUrlMapper extends BaseMapper<ShortUrlMap> {
    /**
     * 使visitedCount++
     * @param id
     * @param sum
     */
    @Update("update short_url_map set visited_count = visited_count + #{sum} where id = #{id}")
    void incrementVisitedCount(Long id, Long sum);

    /**
     * 获得所有id
     * @return
     */
    @Select("select id from short_url_map where is_deleted = 0")
    List<Long> getIdAndTargetUrl();

    void UpdateBatchVisitedCountFromId(@Param("list") List<Long> list, @Param("ids") List<Long> ids);
}
