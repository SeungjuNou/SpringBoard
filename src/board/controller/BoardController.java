package board.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.board.BoardCommentModel;
import model.board.BoardModel;
import board.service.BoardService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/board")
public class BoardController {

	private ApplicationContext context = new ClassPathXmlApplicationContext("/config/applicationContext.xml");
	private BoardService boardService = (BoardService) context.getBean("boardService");

	private int currentPage = 1;
	private int showArticleLimit = 10; //한페이지에 노출할 게시글 수.
	private int showPageLimit = 10; // 한 화면에 노출할 페이징의 수.
	private int startArticleNum = 0;
	private int endArticleNum = 0;
	private int totalNum = 0;

	private String uploadPath; //경로를 저장할 변수 선언.

	
	//게시글 목록을 위한 boardList 
	@RequestMapping("/list.do")
	public ModelAndView boardList(HttpServletRequest request, HttpServletResponse response){
		
		String type = null;
		String keyword = null;		
		
		if(request.getParameter("page") == null || request.getParameter("page").trim().isEmpty() || request.getParameter("page").equals("0")) {
			currentPage = 1;
		} else {
			currentPage = Integer.parseInt(request.getParameter("page"));
		}
		
		if(request.getParameter("type") != null){
			type = request.getParameter("type").trim();
		} 
		
		if(request.getParameter("keyword") != null){
			keyword = request.getParameter("keyword").trim();
		} 
		//
		
		/*
		시작 게시글 번호 = (현재페이지 - 1) * 보여줄게시글 + 1;
		종료 게시글 번호 =  시작글 번호 + 보여줄게시글 -1;
		
		1 = (1페이지 - 1) * 10 + 1;
		10 = 1 + 10 - 1;

		11 = (2페이지 - 1) * 10 + 1;
		20 = 11 + 10 - 1; 
		

		startArticleNum = (currentPage - 1) * showArticleLimit + 1;
		endArticleNum = startArticleNum + showArticleLimit -1;
		*/



		startArticleNum = (currentPage - 1) * showArticleLimit;
		endArticleNum = 10;

		/*
			0 = (1 - 1) * 10;
			10 = (2 - 1) * 10;
		*/
				

		List<BoardModel> boardList;

		if(type == null || type.equals("") ){ //검색을 안했을 경우.
			boardList = boardService.getBoardList(startArticleNum, endArticleNum);
			totalNum = boardService.getTotalNum();
		} else { //검색을 한 경우.
			boardList = boardService.searchArticle(type, keyword, startArticleNum, endArticleNum);
			totalNum = boardService.getSearchTotalNum(type, keyword);
		}

		StringBuffer pageHtml = getPageHtml(currentPage, totalNum, showArticleLimit, showPageLimit, type, keyword);
		//
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("boardList", boardList); 
		mav.addObject("pageHtml", pageHtml);
		mav.setViewName("/board/list");
		
		return mav;
	} //boardList 종료.


	
	//Paging을 위한 getPageHtml 생성
	private StringBuffer getPageHtml(int currentPage, int totalNum, int showArticleLimit, int showPageLimit, String type, String keyword) {
		StringBuffer pageHtml = new StringBuffer();
		int startPage = 0;
		int lastPage = 0;
		

		if (type == null || type.equals("null")) {
			type = "";
		}

		if (keyword == null || keyword.equals("null")) {
			keyword = "";
		}

		// expression page variables
		startPage = ((currentPage-1) / showPageLimit) * showPageLimit + 1;
		lastPage = startPage + showPageLimit - 1;
		
		if(lastPage > totalNum / showArticleLimit) {
			lastPage = (totalNum / showArticleLimit) + 1;
		}
		//


		
		// create page html code		
		if(currentPage == 1){
			pageHtml.append("<span>");
		} else {
			pageHtml.append("<span><a href=\"list.do?page=" + (currentPage-1) + "&type=" + type + "&keyword=" + keyword + "\"><이전></a>&nbsp;&nbsp;");
		}
			
		for(int i = startPage ; i <= lastPage ; i++) {
			if(i == currentPage){
				pageHtml.append(".&nbsp;<strong>");
				pageHtml.append("<a href=\"list.do?page=" +i + "&type=" + type + "&keyword=" + keyword + "\" class=\"page\">" + i + "</a>&nbsp;");
				pageHtml.append("&nbsp;</strong>");
			} else {
				pageHtml.append(".&nbsp;<a href=\"list.do?page=" +i + "&type=" + type + "&keyword=" + keyword + "\" class=\"page\">" + i + "</a>&nbsp;");
			}
				
		}
		
		if(currentPage == lastPage){
			pageHtml.append("</span>");
		} else {
			pageHtml.append(".&nbsp;&nbsp;<a href=\"list.do?page=" + (currentPage+1) + "&type=" + type + "&keyword=" + keyword + "\"><다음></a></span>");
		}
		
		//		
		return pageHtml;
	} //getPageHtml 종료.



	//게시글의 정보를 가져오는 boardView 선언.
	@RequestMapping("/view.do")
	public ModelAndView boardView(HttpServletRequest request){

		//idx 파라미터의 값을 가져옴.
		int idx = Integer.parseInt(request.getParameter("idx"));		

		//idx에 해당하는 게시글의 값을 가져온다.
		BoardModel board = boardService.getOneArticle(idx); 

		//idx에 해당하는 게시글의 조회수를 증가시킨다.
		boardService.updateHitcount(board.getHitcount()+1, idx); 
		
		//idx에 해당하는 게시글의 댓글리스트를 가져온다. 
		List<BoardCommentModel> commentList = boardService.getCommentList(idx); 
		

		ModelAndView mav = new ModelAndView();

		mav.addObject("board", board);
		mav.addObject("commentList", commentList);
		mav.setViewName("/board/view");
		return mav;

	}//boardView 종료.

	

	//글 작성 폼을 위한 write 메서드 선언.
	@RequestMapping("/write.do")
	public String boardWrite(@ModelAttribute("BoardModel") BoardModel boardModel){		
		return "/board/write";
	}

	//글 작성을 위한 boardWriteProc 메서든선언.
	@RequestMapping(value="/write.do", method=RequestMethod.POST)
	public String boardWriteProc(@ModelAttribute("BoardModel") BoardModel boardModel, MultipartHttpServletRequest request){
		

		//request 영역에서 file을 가져온다.
		MultipartFile file = request.getFile("file");

		//파일이 저장될 상대경로를 반환받는다.
		uploadPath = request.getSession().getServletContext().getRealPath("/file/");
		

		String fileName = file.getOriginalFilename().replaceAll(" ", "");	
		File uploadFile = new File(uploadPath + fileName);
		
		
		if(uploadFile.exists()){

			fileName = new Date().getTime() + fileName;
			uploadFile = new File(uploadPath + fileName);

		}

		
		//파일을 해당경로에 저장한다.
		try {
			file.transferTo(uploadFile);
		} catch (Exception e) {
			
		}
		boardModel.setFileName(fileName);
		//


		String content =  boardModel.getContent().replaceAll("\r\n", "<br />");		
		boardModel.setContent(content);

		boardService.writeArticle(boardModel);		
		
		return "redirect:list.do";
	}//boardWriteProc 종료.
	

	//댓글등록을 위한 commentWriteProc 메서드 선언.
	@RequestMapping("/commentWrite.do")
	public ModelAndView commentWriteProc(@ModelAttribute("CommentModel") BoardCommentModel commentModel){

		String content = commentModel.getContent().replaceAll("\r\n", "<br />");
		commentModel.setContent(content);

		boardService.writeComment(commentModel);

		ModelAndView mav = new ModelAndView();
		mav.addObject("idx", commentModel.getLinkedArticleNum());
		mav.setViewName("redirect:view.do");
		
		return mav;
	}//commentWrtieProc 종료.


	//게시글 수정 폼을 보여주는 boardModify메서드 선언.
	@RequestMapping("/modify.do")
	public ModelAndView boardModify(HttpServletRequest request, HttpSession session){
		String userId = (String) session.getAttribute("userId");
		int idx = Integer.parseInt(request.getParameter("idx"));
		
		BoardModel board = boardService.getOneArticle(idx);
		String content = board.getContent().replaceAll("<br />", "\r\n");
		board.setContent(content);
		
		ModelAndView mav = new ModelAndView();
		
		if(!userId.equals(board.getWriterId())){
			mav.addObject("errCode", "1");	// forbidden connection
			mav.addObject("idx", idx);
			mav.setViewName("redirect:view.do");
		} else {
			mav.addObject("board", board);
			mav.setViewName("/board/modify");
		}		
		
		return mav;
	}// boardModify 메서드 종료.


	//게시글을 수정하는 boardModifyProc메서드 선언.
	@RequestMapping(value = "/modify.do", method=RequestMethod.POST)
	public ModelAndView boardModifyProc(@ModelAttribute("BoardModel") BoardModel boardModel, MultipartHttpServletRequest request){

		String orgFileName = request.getParameter("orgFile");
		MultipartFile newFile = request.getFile("newFile");
		String newFileName = newFile.getOriginalFilename();
		
		boardModel.setFileName(orgFileName);
		
		// newFile이 있다는것은 새로운 파일을 업로드 했다는것.
		if(newFile != null && !newFileName.equals("")){

			if(orgFileName != null || !orgFileName.equals("")){
				File removeFile = new File(uploadPath  + orgFileName);
				removeFile.delete();
			}
			
			File newUploadFile = new File(uploadPath +newFileName);
			try {
				newFile.transferTo(newUploadFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			boardModel.setFileName(newFileName);
		}
		//

		String content =  boardModel.getContent().replaceAll("\r\n", "<br />");		
		boardModel.setContent(content);
		
		boardService.modifyArticle(boardModel);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("idx", boardModel.getIdx());
		mav.setViewName("redirect:/board/view.do");
		return mav;
	}//boardModifyProc 종료.
	

	//게시글 삭제를 위한 baordDelete 메서드 선언.
	@RequestMapping("/delete.do")
	public ModelAndView boardDelete(HttpServletRequest request, HttpSession session){

		String userId = (String) session.getAttribute("userId");
		int idx = Integer.parseInt(request.getParameter("idx"));
		
		BoardModel board = boardService.getOneArticle(idx);
		
		ModelAndView mav = new ModelAndView();
		
		//작성자와 로그인한유저가 일치하지 않으면.
		if(!userId.equals(board.getWriterId())){

			mav.addObject("errCode", "1");	
			mav.addObject("idx", idx);
			mav.setViewName("redirect:view.do");
		} else { //일치하면

			List<BoardCommentModel> commentList = boardService.getCommentList(idx);
			
			if(commentList.size() > 0){ //댓글이 있으면 글삭제 불가.
				mav.addObject("errCode", "2"); 
				mav.addObject("idx", idx);
				mav.setViewName("redirect:view.do");
			} else { //댓글이 없으면 글삭제 가능.
			
				//업로드 된 파일이 있으면 파일삭제.
				if(board.getFileName() != null){
					File removeFile = new File(uploadPath + board.getFileName());
					removeFile.delete();
				}
				
				boardService.deleteArticle(idx);
				
				mav.setViewName("redirect:list.do");
			}
		}		
		return mav;
	} //boardDelete 종료.


	//댓글 삭제를 위한 commentDelete 메서드 선언.
	@RequestMapping("/commentDelete.do")
	public ModelAndView commentDelete(HttpServletRequest request, HttpSession session){
		
		int idx = Integer.parseInt(request.getParameter("idx"));
		int linkedArticleNum = Integer.parseInt(request.getParameter("linkedArticleNum"));
		
		String userId = (String) session.getAttribute("userId");
		BoardCommentModel comment = boardService.getOneComment(idx);
		
		ModelAndView mav = new ModelAndView();
		
		System.out.println(idx + "test");
		
		if(!userId.equals(comment.getWriterId())){
			mav.addObject("errCode", "1");
		} else {
			boardService.deleteComment(idx);
		}		
				
		mav.addObject("idx", linkedArticleNum); 
		mav.setViewName("redirect:view.do");
		
		return mav;
	} //commentDelete 종료.

	//추천수를 위한 updateRecommendcount메서드 선언.
	@RequestMapping("/recommend.do")
	public ModelAndView updateRecommendCount(HttpServletRequest request, HttpSession session){
		
		int idx = Integer.parseInt(request.getParameter("idx"));
		String userId = (String) session.getAttribute("userId");
		BoardModel board = boardService.getOneArticle(idx);
		
		ModelAndView mav = new ModelAndView();
		
		if(userId.equals(board.getWriterId())){
			mav.addObject("errCode", "1");
		} else {
			boardService.updateRecommendCount(board.getRecommendcount()+1, idx);
		}
		
		mav.addObject("idx", idx);
		mav.setViewName("redirect:/board/view.do");
		
		return mav;
	}//updateRecommendCount 종료.
	

	@RequestMapping(value = "/download.do")
	public ModelAndView callDownload(@RequestParam (value="fileName") String fileName, 
	            HttpServletRequest request, 
	            HttpServletResponse response) throws Exception {
		
		String downloadPath = request.getSession().getServletContext().getRealPath("/file/");
		downloadPath = downloadPath + fileName;
		
	    File downloadFile = new File(downloadPath);
	    
	    if(!downloadFile.canRead()){

	        throw new Exception("File can't read(파일을 찾을 수 없습니다)");

	    }
	    
	    //modelAndView 리턴(DownloadView실행)
	    return new ModelAndView("download", "downloadFile", downloadFile); 

	}
	
}
