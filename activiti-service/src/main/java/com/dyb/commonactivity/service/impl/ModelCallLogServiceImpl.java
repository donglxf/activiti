package com.dyb.commonactivity.service.impl;

import com.dyb.commonactivity.utils.DateUtils;
import com.dyb.commonactivity.entity.ModelCallLog;
import com.dyb.commonactivity.mapper.ModelCallLogMapper;
import com.dyb.commonactivity.service.ModelCallLogService;
import com.dyb.commonactivity.common.service.impl.BaseServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dyb
 * @since 2018-07-23
 */
@Service
@Log4j2
public class ModelCallLogServiceImpl extends BaseServiceImpl<ModelCallLogMapper, ModelCallLog> implements ModelCallLogService {

    @Autowired
    private ModelCallLogMapper modelCallLogMapper;

    @Override
    public Map<String, Object> indexLine(Map<String, Object> paramMap) {
        Map<String, Object> map = new HashMap<>();
        Date currentDate = DateUtils.getDate();
        Date beforeDate = DateUtils.dayOffset(currentDate, -10);
        String[] days = DateUtils.minusDay(beforeDate, currentDate, ""); // 近10天数组
//        log.info("最近10天数组:" + JSON.toJSONString(days));
        List<ModelCallLog> xAxis = new ArrayList<>();
        List<Map<String, Object>> list = modelCallLogMapper.indexLine();
        list.forEach(li -> {
            Map<String, Object> tmap = new HashMap<>();
            tmap.put(String.valueOf(li.get("create_time")), String.valueOf(li.get("COUNT")));
            ModelCallLog call = new ModelCallLog();
            call.setCreateTime(String.valueOf(li.get("create_time")));
            call.setTotal(String.valueOf(li.get("COUNT")));
            xAxis.add(call);
        });
        // 对于数据查询不存在的日期，补空
        for (int i = 0; i < days.length; i++) {
            boolean bool = false;
            for (int j = 0; j < xAxis.size(); j++) {
                ModelCallLog tmp = xAxis.get(j);
                if (days[i].equals(tmp.getCreateTime())) {
                    bool = true;
                    break;
                }
            }
            if (!bool) {
                ModelCallLog tCall = new ModelCallLog();
                tCall.setTotal("0");
                tCall.setCreateTime(days[i]);
                xAxis.add(tCall);
            }
        }
        Map<String, ModelCallLog> appleMap = xAxis.stream().collect(Collectors.toMap(ModelCallLog::getCreateTime, a -> a, (k1, k2) -> k1));
        List<Map.Entry<String, ModelCallLog>> tlist = new ArrayList<Map.Entry<String, ModelCallLog>>(appleMap.entrySet());
        Collections.sort(tlist, new Comparator<Map.Entry<String, ModelCallLog>>() {
            //降序排序
            public int compare(Map.Entry<String, ModelCallLog> o1,
                               Map.Entry<String, ModelCallLog> o2) {
                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date o1key = sim.parse(o1.getKey());
                    Date o2key = sim.parse(o2.getKey());
                    if (o1key.before(o2key)) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        map.put("xAxisList", tlist);
        return map;
    }

    @Override
    public Map<String, Object> indexBar(Map<String, Object> paramMap) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> xAxis = new ArrayList<>();
        List<Map<String, Object>> list = modelCallLogMapper.indexBar(String.valueOf(paramMap.get("date")));
        list.forEach(li -> {
            Map<String, Object> tmap = new HashMap<>();
            tmap.put(String.valueOf(li.get("model_name")), String.valueOf(li.get("total")));
            xAxis.add(tmap);
        });
        map.put("xAxisList", xAxis);
        return map;
    }
}
