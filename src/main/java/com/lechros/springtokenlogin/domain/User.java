package com.lechros.springtokenlogin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class User {

    @Setter
    private Long id;

    private String nickname;
}
