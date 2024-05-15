package com.ulla.modules.business.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.PairsMapper;
import com.ulla.modules.business.mo.PairsMo;
import com.ulla.modules.business.service.IPairsService;

@Service
public class IPairsServiceImpl extends ServiceImpl<PairsMapper, PairsMo> implements IPairsService {}
