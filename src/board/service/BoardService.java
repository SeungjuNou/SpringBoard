package board.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import board.dao.BoardDao;
import model.board.BoardCommentModel;
import model.board.BoardModel;


public class BoardService implements BoardDao {

	//sqlMapper를 이용하기 위한 객체 생성
	private SqlMapClientTemplate sqlMapClientTemplate;

	//DB와의 작업을 위한 데이터를 담는 Map생성.
	private HashMap<String, Object> valueMap = new HashMap<String, Object>();
		

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	@Override
	public List<BoardModel> getBoardList(int startArticleNum, int endArticleNum) {
		valueMap.put("startArticleNum", startArticleNum);
		valueMap.put("endArticleNum", endArticleNum);
		return sqlMapClientTemplate.queryForList("board.getBoardList", valueMap);
	}

	@Override
	public BoardModel getOneArticle(int idx) {
		return (BoardModel) sqlMapClientTemplate.queryForObject("board.getOneArticle", idx);
	}

	@Override
	public List<BoardModel> searchArticle(String type, String keyword, int startArticleNum, int endArticleNum) {
		valueMap.put("type", type);
		valueMap.put("keyword", keyword);
		valueMap.put("startArticleNum", startArticleNum);
		valueMap.put("endArticleNum", endArticleNum);
		return sqlMapClientTemplate.queryForList("board.searchArticle", valueMap);
	}

	@Override
	public List<BoardCommentModel> getCommentList(int idx) {
		return sqlMapClientTemplate.queryForList("board.getCommentList", idx);
	}

	@Override
	public boolean writeArticle(BoardModel board) {
		sqlMapClientTemplate.insert("board.writeArticle", board);		
		return true;
	}

	@Override
	public boolean writeComment(BoardCommentModel comment) {
		sqlMapClientTemplate.insert("board.writeComment", comment);
		return true;
	}

	@Override
	public void updateHitcount(int hitcount, int idx) {
		valueMap.put("hitcount", hitcount);
		valueMap.put("idx", idx);
		sqlMapClientTemplate.update("board.updateHitcount", valueMap);		
	}

	@Override
	public void updateRecommendCount(int recommendcount, int idx) {
		valueMap.put("recommendcount", recommendcount);
		valueMap.put("idx", idx);
		sqlMapClientTemplate.update("board.updateRecommendcount", valueMap);
		
	}
	@Override
	public int getTotalNum() {
		return (Integer) sqlMapClientTemplate.queryForObject("board.getTotalNum");
	}

	@Override
	public int getSearchTotalNum(String type, String keyword) {
		valueMap.put("type", type);
		valueMap.put("keyword", keyword);
		return (Integer) sqlMapClientTemplate.queryForObject("board.getSearchTotalNum", valueMap);
	}

	@Override
	public void deleteComment(int idx) {
		sqlMapClientTemplate.delete("board.deleteComment", idx);
	}
	
	@Override
	public void deleteArticle(int idx) {
		sqlMapClientTemplate.delete("board.deleteArticle", idx);	
	}

	@Override
	public BoardCommentModel getOneComment(int idx) {
		return (BoardCommentModel) sqlMapClientTemplate.queryForObject("board.getOneComment", idx);		
	}

	@Override
	public boolean modifyArticle(BoardModel board) {
		sqlMapClientTemplate.update("board.modifyArticle", board);
		return true;
	}	

}