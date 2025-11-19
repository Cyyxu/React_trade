package com.xyes.springboot.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentEditRequest implements Serializable {

    /**
     * 评论 ID
     */
    private Long id;


    /**
     * 评论内容
     */
    private String content;



    private static final long serialVersionUID = 1L;
}