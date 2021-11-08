package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.SystemComponment;
import com.ziroom.qa.quality.defende.provider.mapper.SystemComponmentMapper;
import com.ziroom.qa.quality.defende.provider.service.SystemComponmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemComponmentServiceImpl extends ServiceImpl<SystemComponmentMapper, SystemComponment> implements SystemComponmentService {
}
