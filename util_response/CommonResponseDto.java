package com.puzzlix.solid_task._global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponseDto<T> {
    private boolean success;
    private T data;
    private String message;

    // 정적 팩토리 메서드(팩토리 패턴과 다른 개념)
    // static 객체 속성이 아니라 클래스에 포함 - className.add();
    public static <T> CommonResponseDto<T> success(T data, String message){
        return  new CommonResponseDto<>(true,data,message);
    }

    public static <T> CommonResponseDto<T> success(T data){
        return success(data,null);
    }

    public static <T> CommonResponseDto<T> error(String message){
        return  new CommonResponseDto<>(false,null,message);
    }

    /*
    * 클라이언트 코드(Controller)로 부터 객체 생성 과정을 완전히 분리하고 숨기는 것이 목표이다
    * 이는 주로 OCP 개발-폐쇄 원칙 를 만족시키는 코드이다 
    * */
}

