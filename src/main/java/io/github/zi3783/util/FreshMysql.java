package io.github.zi3783.util;

import io.github.zi3783.entity.ShortUrlMap;
import io.github.zi3783.mapper.ShortUrlMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Component
public class FreshMysql{

    private final ConcurrentHashMap<String, LongAdder> counterMapper = new ConcurrentHashMap<>();

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Scheduled(fixedDelay = 20000)
    public void FreshCounter() {
        log.info("FreshCounter start");
        List<Long> list = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for(Map.Entry<String,LongAdder> entry:counterMapper.entrySet()){
            Long id = Long.valueOf(entry.getKey());
            LongAdder counter = entry.getValue();
            Long sum = counter.sumThenReset();
            if(sum <= 0) continue;
//            try {
//                shortUrlMapper.incrementVisitedCount(id, sum);
//            }catch (Exception e){
//                log.warn("批量更新失败,id:{},sum:{}",id,sum,e);
//                //回滚
//                counter.add(sum);
//            }
            list.add(sum);
            ids.add(id);
        }
        if(list.isEmpty()) return;
        try{
            shortUrlMapper.UpdateBatchVisitedCountFromId(list, ids);
        }catch (Exception e){
            log.warn("写入mysql错误,回滚,warn:",e);
            int i = 0;
            for(Long id:ids){
                LongAdder longAdder = counterMapper.get(id.toString());
                longAdder.add(list.get(i++));
            }
        }
    }

    public void increment(Long id){
        String key = id.toString();
        counterMapper.computeIfAbsent(key,k -> new LongAdder()).increment();
    }

    public void decrement(Long id){
        String key = id.toString();
        LongAdder longAdder = counterMapper.get(key);
        if(longAdder != null){
            longAdder.decrement();
        }
    }

    @PreDestroy
    public void destroy(){
        log.info("程序关闭前写入mysql");
        FreshCounter();
    }
}
