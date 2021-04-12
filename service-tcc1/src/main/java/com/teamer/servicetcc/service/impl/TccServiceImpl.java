package com.teamer.servicetcc.service.impl;

import com.alibaba.fastjson.JSON;
import com.teamer.servicetcc.dao.TccDAO;
import com.teamer.servicetcc.entity.Tcc;
import com.teamer.servicetcc.service.CommonService;
import com.teamer.servicetcc.service.TccService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TccServiceImpl implements  TccService {

    @Autowired
    TccDAO tccDAO;
    @Resource
    CommonService commonService;


    /**
     * tcc服务t（try）方法
     * 根据实际业务场景选择实际业务执行逻辑或者资源预留逻辑
     *
     * @param params - name
     * @return String
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String insertTcc1(Map<String, String> params) {
        log.info("xid = " + RootContext.getXID());
        System.out.println("预提交"+RootContext.getXID());
        //todo 实际的操作，或操作MQ、redis等
        Tcc tcc=new Tcc();
        tcc.setName(params.get("name"));
        Integer id=tccDAO.insert(tcc);
        commonService.saveOrUpdateRedisEntryFor(RootContext.getXID(),tcc.getId().toString());

        return "success";


    }

    /**
     * tcc服务 confirm方法
     * 若一阶段采用资源预留，在二阶段确认时要提交预留的资源
     *
     * @param context 上下文
     * @return boolean
     */
    @Override
    public boolean commitTcc(BusinessActionContext context) {
        log.info("xid = " + context.getXid() + "提交成功");
        //todo 若一阶段资源预留，这里则要提交资源
        return true;
    }

    /**
     * tcc 服务 cancel方法
     *如果不返回true则会一直尝试 具体每隔多久尝试 默认1秒 可以修改
     * @param context 上下文
     * @return boolean
     */
    @Override
    public boolean cancel(BusinessActionContext context) {

        System.out.println("回滚"+context.getXid());
        //todo 这里写中间件、非关系型数据库的回滚操作
        System.out.println("please manually rollback this data:" + context.getActionContext("params"));
        Integer id=Integer.valueOf(commonService.getRedisValueFor(context.getXid()));
        tccDAO.delete(id);
        commonService.removeRedisKey(context.getXid());
        return true;
    }
}
