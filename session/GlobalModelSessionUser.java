package com.nationwide.nationwide_server.core.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice // 모든 Controller에 적용됨 실행되면
public class GlobalModelSessionUser {
    @Autowired
    private HttpSession httpSession;

    @ModelAttribute
    public void GlobalAttributes(Model model){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("sessionUser");

        if(sessionUser != null){
            model.addAttribute("sessionUser", sessionUser);
        }
    }
}
