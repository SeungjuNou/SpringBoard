package member.controller;

import javax.servlet.http.HttpSession;

import model.login.LoginSessionModel;
import member.service.LoginService;
import member.service.LoginValidator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	private ApplicationContext context;
	
	@RequestMapping("/login.do") //login.do 요청에 대한 처리.
	public String login() {		
		return "/member/login";
	}
	
	@RequestMapping(value="/login.do", method = RequestMethod.POST) //post방식의 login.do요청 처리.
	public ModelAndView loginProc(@ModelAttribute("LoginModel") LoginSessionModel loginModel, BindingResult result, HttpSession session) {
		
		ModelAndView mav = new ModelAndView();
		
		// form 입력값의 유효성 검사.
		new LoginValidator().validate(loginModel, result);

		//유효성 검사에 실패할 경우login view를 띄운다.
		if(result.hasErrors()){ 
			mav.setViewName("/member/login"); 
			return mav;
		}
		
		String userId = loginModel.getUserId();
		String userPw = loginModel.getUserPw();
	
		context = new ClassPathXmlApplicationContext("/config/applicationContext.xml");
		LoginService loginService = (LoginService) context.getBean("loginService");
		LoginSessionModel loginCheckResult = loginService.checkUserId(userId);
		
		
		if(loginCheckResult == null){
			//database에 해당하는 userId가 없는 경우.
			mav.addObject("userId", userId);
			mav.addObject("errCode", 1);	
			mav.setViewName("/member/login");
			//다시 login view를 띄운다.			
			return mav; 
		}
		
		
		if(loginCheckResult.getUserPw().equals(userPw)){
			//로그인에 성공했을때 실행		
			session.setAttribute("userId", userId);
			session.setAttribute("userName", loginCheckResult.getUserName());
			mav.setViewName("redirect:/board/list.do");
			//게시판 목록을 띄운다.
			return mav;
		} else { //로그인에 실패했을경우 (비밀번호 불일치)
			mav.addObject("userId", userId);
			mav.addObject("errCode", 2);	
			mav.setViewName("/member/login");			
			return mav;  
		}	
	}
	
	@RequestMapping("/logout.do")
	public String logout(HttpSession session){
		session.invalidate(); //호출시 세션 제거.
		return "redirect:login.do";
	}
}