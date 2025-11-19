package com.xyex.infrastructure.model;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Schema(name = "PageParam", description = "分页参数公共父类")
public abstract class PageParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    @NotNull(message = "页码不能为空")
    private Long pageNo = 1L;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于等于1")
    @NotNull(message = "每页大小不能为空")

    private Long pageSize = 10L;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "排序字段，使用LinkedHashMap保持插入顺序")
    private Map<String, String> orderBy = new LinkedHashMap<>();

    @Schema(description = "关键词搜索")
    private String keyword;

    /**
     * 创建分页对象
     *
     * @return Page
     */
    public final <T> Page<T> createPage() {
        return new Page<>(this.getPageNo(), this.getPageSize());
    }

    /**
     * 创建查询条件
     * 默认实现：返回空的查询条件
     * 子类可以重写此方法以实现自定义查询逻辑
     */
    public abstract Wrapper createQuery();

    /**
     * 创建查询构建器
     *
     * @return QueryBuilder
     */
    @SuppressWarnings("unused")
    protected final <T> QueryBuilder<T> buildQuery(Class<T> clazz) {
        return new QueryBuilder<>(new LambdaQueryWrapper<>(), this, new HashMap<>());
    }

    /**
     * 查询构建器，支持链式调用
     */
    protected record QueryBuilder<T>(LambdaQueryWrapper<T> wrapper,
                                     PageParam param,
                                     Map<String, SFunction<T, ?>> orderFunction) {

        public QueryBuilder {
            if (param.getOrderBy().isEmpty()) {
                param.getOrderBy().put("createdAt", "desc");
                orderFunction = new HashMap<>();
            }
        }

        /**
         * 添加关键词搜索条件（支持多个字段OR模糊匹配）
         *
         * @param fieldGetters 要搜索的字段getter方法引用（可变参数）
         * @return this
         */
        @SafeVarargs
        public final QueryBuilder<T> keyword(SFunction<T, ?>... fieldGetters) {
            if (param.keyword == null || param.keyword.isEmpty()) {
                return this;
            }
            if (fieldGetters == null || fieldGetters.length == 0) {
                return this;
            }
            wrapper.and(w -> {
                for (int i = 0; i < fieldGetters.length; i++) {
                    if (i == 0) {
                        w.like(fieldGetters[i], param.keyword);
                    } else {
                        w.or().like(fieldGetters[i], param.keyword);
                    }
                }
            });
            return this;
        }

        /**
         * 添加时间范围条件
         *
         * @param fieldGetter 时间字段getter方法引用
         * @return this
         */
        public QueryBuilder<T> timeRange(SFunction<T, ?> fieldGetter) {
            if (param.startTime != null) {
                wrapper.ge(fieldGetter, param.startTime);
            }
            if (param.endTime != null) {
                wrapper.le(fieldGetter, param.endTime);
            }
            return this;
        }

        /**
         * 添加排序条件
         *
         * @param key         排序字段key
         * @param fieldGetter 要排序的字段getter方法引用
         * @return this
         */
        public QueryBuilder<T> orderBy(String key, SFunction<T, ?> fieldGetter) {
            orderFunction.put(key, fieldGetter);
            return this;
        }

        /**
         * 构建最终的查询条件
         *
         * @return LambdaQueryWrapper
         */
        public LambdaQueryWrapper<T> buildWrapper() {
            for (Map.Entry<String, String> entry : param.getOrderBy().entrySet()) {
                if (orderFunction.containsKey(entry.getKey())) {
                    wrapper.orderBy(true, entry.getValue().equalsIgnoreCase("asc"), orderFunction.get(entry.getKey()));
                }
            }
            return wrapper;
        }
    }

}