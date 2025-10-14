package com.join.spring_resume._core.errors;


import com.join.spring_resume._core.errors.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 모든 컨트롤러에서 발생하는 예외 처리를 이 클래스에 서 처리한다
// RuntimeException 발생하면 해당 파일로 예외 처리가 집중 됨
@ControllerAdvice 
// @RestControllerAdvice // 데이터를 반환해서 내려 줄 떄 사용
public class MyExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    // 로그인 유무를 체크 하는 오류
    @ExceptionHandler(Exception401.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex401(Exception401 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/login-form'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
    
    // 오류 발생시 메인 페이지로 이동
    @ExceptionHandler(Exception403.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex403(Exception403 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception400.class)
    @ResponseBody
    public ResponseEntity<String> ex400(Exception400 e, HttpServletRequest request) {
        String referer = request.getHeader("Referer"); // 이전 페이지
        String redirectUrl = referer != null ? referer : "/";
        String script = "<script>alert('" + e.getMessage() + "'); location.href='" + redirectUrl + "';</script>";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 오류 발생시 메인 페이지로 이동
    @ExceptionHandler(Exception404.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex404(Exception404 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 오류 발생시 메인 페이지로 이동
    @ExceptionHandler(Exception500.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex500(Exception500 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<String> handleMaxSizeException(HttpServletRequest request) {
        String script = "<script>alert('파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다.'); history.back();</script>";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
}
