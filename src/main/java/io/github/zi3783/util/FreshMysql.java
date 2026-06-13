package io.github.zi3783.util;

import io.github.zi3783.entity.ShortUrlMap;
import io.github.zi3783.mapper.ShortUrlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Scheduled(fixedDelay = 10000)
    public void FreshCounter() {
        log.info("FreshCounter start");
        List<ShortUrlMap> list = new ArrayList<>();
        for(Map.Entry<String,LongAdder> entry:counterMapper.entrySet()){
            Long id = Long.valueOf(entry.getKey());
            LongAdder counter = entry.getValue();
            Long sum = counter.sumThenReset();
            if(sum <= 0) continue;
            shortUrlMapper.incrementVisitedCount(id,sum);
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
}
