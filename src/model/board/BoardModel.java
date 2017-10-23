package model.board;

public class BoardModel {
	private int idx;
	private String writer;
	private String subject;
	private String content;
	private int hitcount = 0; //조회수 초기값 0
	private int recommendcount = 0; //추천수 초기값 0
	private int comment = 0; //comment 초기값 0
	private String writeDate;
	private String writerId;
	private String fileName;
	

	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}	
	public int getHitcount() {
		return hitcount;
	}
	public void setHitcount(int hitcount) {
		this.hitcount = hitcount;
	}
	public int getRecommendcount() {
		return recommendcount;
	}
	public void setRecommendcount(int recommendcount) {
		this.recommendcount = recommendcount;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public String getWriterId() {
		return writerId;
	}
	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}