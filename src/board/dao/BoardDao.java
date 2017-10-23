package board.dao;

import java.util.List;

import model.board.BoardCommentModel;
import model.board.BoardModel;

public interface BoardDao {
	

	List<BoardModel> getBoardList(int startArticleNum, int showArticleLimit);
	
	
	BoardModel getOneArticle(int idx);
	
	
	List<BoardModel> searchArticle(String type, String keyword, int startArticleNum, int endArticleNum); 
	
	
	List<BoardCommentModel> getCommentList(int idx);
	
	
	BoardCommentModel getOneComment(int idx);
	
	
	boolean modifyArticle(BoardModel board);
	
	
	boolean writeArticle(BoardModel board);
	
	
	boolean writeComment(BoardCommentModel comment);
	
	
	void updateHitcount(int hitcount, int idx);
	

	void updateRecommendCount(int recommendcount, int idx);
	

	int getTotalNum();
	

	int getSearchTotalNum(String type, String keyword);
	

	void deleteComment(int idx);
	
	void deleteArticle(int idx);
}