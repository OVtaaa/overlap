package com.ov.pojo.vo;

import com.ov.pojo.Tag;
import com.ov.pojo.User;
import com.ov.pojo.vo.UserCommonVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPage {

    private List<UserCommonVO> list;

    private Long total;

    private Long curPageNum;

    private Long pageSize;
}
