package interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SessionInterceptor extends HandlerInterceptorAdapter{
	
	@Override
	public boolean preHandle( //controller를 실행전에 실행하는 메서드.
		HttpServletRequest request, HttpServletResponse response, 
								Object handler) throws Exception {

		//request 영역에서 세션을 얻어 세션에 있는 userId항목의 값을 가져온다.
		Object userId = request.getSession().getAttribute("userId"); 
		
		System.out.println(request.getRequestURI());
		
		//요청URI가 login.do 혹은 join.do일 경우에는 
		if(request.getRequestURI().equals("/login.do") 
				|| request.getRequestURI().equals("/member/join.do")){

			System.out.println("test33333333333");
			
			if(userId != null){ //로그인을 한 상태일 경우.
				System.out.println("testttttt222222222222222");
				response.sendRedirect(request.getContextPath() + "/board/list.do"); //게시판 목록으로 보낸다.
				return true;
			
			} else { //로그인 하지 않았을 경우.
				return true;
			}

		} 
		//

		// where other pages		
		if(userId == null || userId.equals("") || request.getSession() == null){ //비 로그인의 경우 false를 리턴하고 리다이렉트 한다.
			System.out.println("testttttt");
			response.sendRedirect(request.getContextPath() + "/login.do");
			return false;
		} else { //로그인 했을경우 true를 리턴 controller를 실행한다.
			return true;
		}
		//		
		
	}
	
	@Override //컨트롤러 실행후의 실행할 영역.
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView) throws Exception {
	}
}
