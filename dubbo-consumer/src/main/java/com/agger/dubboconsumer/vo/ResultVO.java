package com.agger.dubboconsumer.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @classname: ResultVO
 * @description: 返回类型
 * @author chenhx
 * @date 2020-01-14 14:02:36
 */
@Data
@Builder
public class ResultVO {

    private Boolean flag;
    private String msg;
    private Object data;
}
