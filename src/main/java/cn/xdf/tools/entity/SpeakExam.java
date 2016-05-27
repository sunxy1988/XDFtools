/**
 * 项目名称：Iknowledge
 * 
 * @author  Administrator 
 * 新东方教育集团
 * 创建时间 2015年11月4日  下午4:25:00
 */
package cn.xdf.tools.entity;

/**
 *
 * iknowledge ： cn.xdf.iknowledge.modules.exam.entity.SpeakExam
 * 功能描述：
 *
 * 修改记录：
 *
 */
public class SpeakExam {

	String name ;
	String classCode ;
	String studentCode ;
	String audioUrl ;
	String score ;
	Integer countScore ;
	Integer qType ;
	
	
	
	public SpeakExam(String name, String classCode, String studentCode,
			String audioUrl, String score, Integer countScore,Integer qType ) {
		super();
		this.name = name;
		this.classCode = classCode;
		this.studentCode = studentCode;
		this.audioUrl = audioUrl;
		this.score = score;
		this.countScore = countScore;
		this.qType = qType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public Integer getCountScore() {
		return countScore;
	}
	public void setCountScore(Integer countScore) {
		this.countScore = countScore;
	}
	public Integer getqType() {
		return qType;
	}
	public void setqType(Integer qType) {
		this.qType = qType;
	}
 
}
