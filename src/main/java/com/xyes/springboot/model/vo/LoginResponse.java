package com.xyes.springboot.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResponse implements Serializable {

    private LoginUserVO user;

    private String token;
}

