package cn.xdf.tools.entity;

public class IKEntity {

	private String name;
	private String stuId;
	private String classCode;
	private String createTime;
	private Integer totalScore;
	private Integer readingScore;
	private Integer listeningScore;
	private Integer speakingScore;
	private Integer writingScore;
	private String readingItems;
	private String listeningItems;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStuId() {
		return stuId;
	}
	public void setStuId(String stuId) {
		this.stuId = stuId;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}
	public Integer getReadingScore() {
		return readingScore;
	}
	public void setReadingScore(Integer readingScore) {
		this.readingScore = readingScore;
	}
	public Integer getListeningScore() {
		return listeningScore;
	}
	public void setListeningScore(Integer listeningScore) {
		this.listeningScore = listeningScore;
	}
	public Integer getSpeakingScore() {
		return speakingScore;
	}
	public void setSpeakingScore(Integer speakingScore) {
		this.speakingScore = speakingScore;
	}
	public Integer getWritingScore() {
		return writingScore;
	}
	public void setWritingScore(Integer writingScore) {
		this.writingScore = writingScore;
	}
	public String getReadingItems() {
		return readingItems;
	}
	public void setReadingItems(String readingItems) {
		this.readingItems = readingItems;
	}
	public String getListeningItems() {
		return listeningItems;
	}
	public void setListeningItems(String listeningItems) {
		this.listeningItems = listeningItems;
	}
	public IKEntity( String stuId, String classCode, String createTime,
			String readingItems, String listeningItems) {
		super();
		this.stuId = stuId;
		this.classCode = classCode;
		this.createTime = createTime;
		this.readingItems = readingItems;
		this.listeningItems = listeningItems;
	}
	
	
	
}
