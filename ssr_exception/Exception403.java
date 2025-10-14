package com.join.spring_resume._core.errors.exception;

// 400 Bad Request 상황에서 사용할 커스텀 예외 클래스
public class Exception403 extends RuntimeException{

    public Exception403(String message){
        super(message);
    }
    

}
