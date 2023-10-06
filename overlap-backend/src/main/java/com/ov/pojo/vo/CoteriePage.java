package com.ov.pojo.vo;

import com.ov.pojo.Coterie;
import com.ov.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoteriePage {
    private List<Coterie> list;

    private Long total;

    private Long curPageNum;

    private Long pageSize;
}
