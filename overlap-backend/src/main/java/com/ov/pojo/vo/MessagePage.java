package com.ov.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePage implements Serializable {

    private List<MessageCommonVO> list;

    private Long total;

    private Long pageSize;

    private Long pageNum;

    @Serial
    private static final long serialVersionUID = -5303714494019179600L;
}
