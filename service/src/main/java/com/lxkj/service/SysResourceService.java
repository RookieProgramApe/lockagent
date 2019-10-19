package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.SysResource;
import com.lxkj.mapper.SysResourceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Service
public class SysResourceService extends ServiceImpl<SysResourceMapper, SysResource>  {

    public List<Map<String,Object>> getTreeList(){
        List<Map<String,Object>> res= this.listMaps(new QueryWrapper<SysResource>().orderByAsc("orders"));
        res.stream().forEach(p->{
            p.put("pId",p.get("pid"));
            p.put("title",p.get("resName"));
            p.put("name",p.get("resName"));
        });
        return res;
    }

}
