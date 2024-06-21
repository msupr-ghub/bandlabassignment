package com.bandlab.assignment.api.response;

import lombok.*;


@Getter
@Builder
public class GenericApiResponse<T> {

    private String message;
    private T entity;

}
