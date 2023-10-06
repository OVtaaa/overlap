package com.ov.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCommonVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String avatarUrl;

    private String title;

    private String latestNews;

    private Date latestTime;

    private Integer type;

    private Long unRead;

    @Serial
    private static final long serialVersionUID = 3892389495773676391L;
}
