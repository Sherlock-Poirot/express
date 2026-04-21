package com.express.yto.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/21
 * 分页查询基础类，每个分页接口的传参都要继承该类
 */
@Data
public class PageInput {

    @NotNull
    private Integer pageNo = 1;

    @NotNull
    private Integer pageSize = 10;
}
