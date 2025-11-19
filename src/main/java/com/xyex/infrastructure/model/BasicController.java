package com.xyex.infrastructure.model;


import com.xyex.infrastructure.valid.Insert;
import com.xyex.infrastructure.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 通用Restful风格基础Controller
 *
 * @param <T>  实体类类型
 * @param <S>  Service接口类型（需继承IService）
 * @param <ID> 主键类型
 */
public abstract class BasicController<T extends BasicField, S extends BasicService<T>, ID extends Serializable> {

    @Autowired
    protected S service;

    @PostMapping
    @Operation(summary = "保存数据")
    public void create(@Validated(value = {Insert.class}) @RequestBody T entity) {
        service.save(entity);
    }

    @GetMapping
    @Operation(summary = "根据主键集合查询数据")
    public List<T> getById(@RequestBody List<ID> ids) {
        return service.listByIds(ids);
    }

    @PostMapping("/list")
    @Operation(summary = "查询所有数据")
    public List<T> getList() {
        return service.list();
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据主键更新数据")
    public void updateById(@PathVariable ID id, @Validated(value = {Update.class}) @RequestBody T entity) {
        service.updateById(entity);
    }

    @DeleteMapping
    @Operation(summary = "批量删除数据")
    public void deleteBatch(@RequestBody List<ID> ids) {
        service.removeBatchByIds(ids);
    }

}