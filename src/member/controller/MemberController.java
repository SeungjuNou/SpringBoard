package member.controller;

import model.member.MemberModel;
import member.service.MemberService;
import member.service.MemberValidator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/member")
public class MemberController {

	private ApplicationContext context;
	
	@RequestMapping("/join.do") //get방식의 요청 처리로 가입창을 띄운다.
	public String memberJoin(){
		return "/member/join";
	}
	
	@RequestMapping(value="/join.do", method = RequestMethod.POST)
	public ModelAndView addMember(@ModelAttribute("MemberModel") MemberModel memberModel, BindingResult result){
		
		ModelAndView mav = new ModelAndView();
		
		//유효성 검사를 한다.
		new MemberValidator().validate(memberModel, result);
		
		//유효성 검사 후에 오류가 있으면 회원가입창을 다시 띄운다.
		if(result.hasErrors()){
			mav.setViewName("/member/join");
			return mav;
		}
		
		context = new ClassPathXmlApplicationContext("/config/applicationContext.xml");

		MemberService memberService = (MemberService) context.getBean("memberService");
		MemberModel checkMemberModel = memberService.findByUserId(memberModel.getUserId());
		
		//아이디가 이미 있을 경우.
		if(checkMemberModel != null){
			mav.addObject("errCode", 1); 
			mav.setViewName("/member/join");
			return mav;
		}		
		
		//회원가입에 성공했을 경우.
		if(memberService.addMember(memberModel)){
			mav.addObject("errCode", 3);
			mav.setViewName("/member/login");
			return mav;
		} else { //회원가입에 실패한 경우.
			mav.addObject("errCode", 2); 
			mav.setViewName("/member/join");
			return mav;
		}
	}
}