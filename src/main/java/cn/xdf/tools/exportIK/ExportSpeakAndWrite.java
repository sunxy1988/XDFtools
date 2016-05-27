package cn.xdf.tools.exportIK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xdf.tools.JDBCConnection;
import cn.xdf.tools.entity.SpeakExam;


public class ExportSpeakAndWrite {

	final String  exprtDIR = "E:/"; 
	
	static Map<String, String> XPO_LINK = new HashMap();
	static Map<String, String> TPO_LINK = new HashMap();
	static Map<String, String> NUM_LINK = new HashMap();

	  

	public static void main(String[] args) {
		ExportSpeakAndWrite ex = new ExportSpeakAndWrite();
		for (String key : TPO_LINK.keySet()) {
			System.out.println(key);
			   ex.getAllSpeakingFile("TPO",key);
//				ex.findWriteFile(key);
			  }
		for (String key : XPO_LINK.keySet()) {
			System.out.println(key);
			ex.getAllSpeakingFile("XPO",key);
//				ex.findWriteFile(key);
		}
		
		
//		for (int i = 1; i < 9; i++) {
////			ex.findWriteFile(i+"");
//			ex.getAllSpeakingFile(i+"");
//		}
//
//		for (int i = 42; i < 46; i++) {
////			ex.findWriteFile(i+"");
//			ex.getAllSpeakingFile(i+"");
//		}
////		ex.findWriteFile(67+"");
//		
//		ex.getAllSpeakingFile(67+"");
	}
	public void findWriteFile(String examId){
		Connection connection = JDBCConnection.getJDBCConnection()
				.getConnection();
		ResultSet rs = null;
		String sql ="SELECT DISTINCT timu.qtype qtype,file.timu_id,file.exam_id exam_id,u.`name` `name`,file.class_code clas_code,file.class_exam_id class_exam_id,file.student_code student_code,file.content content,score.score score,sj.scaled_score count_score,file.time time \r\n" + 
				"FROM \r\n" + 
				"(SELECT er.submit_date time,er.exam_id exam_id,utzr.section_id section_id,utzr.timu_id timu_id,er.time_str time_str,er.class_code class_code,er.class_exam_id class_exam_id,er.student_code student_code,utzr.content content FROM \r\n" + 
				"exam_record er,user_timu_zg_record utzr   WHERE er.time_str=utzr.timestr  and er.exam_id = "+examId+" AND utzr.section_id = 4 ) file,\r\n" + 
				"(SELECT utzj.timu_id,er.time_str time_str,er.class_code class_code,er.class_exam_id class_exam_id,er.student_code student_code,utzj.score score\r\n" + 
				" FROM \r\n" + 
				"exam_record er,user_timu_zg_judge utzj   WHERE er.time_str=utzj.timestr  and er.exam_id ="+examId+" AND utzj.section_id = 4) score,section_judge sj,exam_task task,ls_student u,timu timu\r\n" + 
				"WHERE file.timu_id = score.timu_id \r\n" + 
				"AND file.student_code = score.student_code\r\n" + 
				"AND file.student_code = sj.`code` AND sj.class_code = file.class_code \r\n" + 
				"AND sj.section_id = file.section_id AND task.time_str = file.time_str AND u.code = file.student_code AND file.timu_id = timu.id AND   file.exam_id ="+examId+" AND task.`status` = 3 ORDER BY u.`name` DESC,file.timu_id;";
		java.sql.Statement statement;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			List<SpeakExam> sel = new ArrayList<SpeakExam>();
			while (rs.next()) {
				SpeakExam se = new SpeakExam(rs.getString("name"), rs.getString("clas_code"), rs.getString("student_code"), rs.getString("content"), rs.getString("score"), rs.getInt("count_score"),rs.getInt("qtype"));
				sel.add(se);
			}
			makeWriteFile(sel,examId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void makeWriteFile(List<SpeakExam> sle,String examID){
		for (SpeakExam speakExam : sle) {
			String type = "";
			String score = "";
			if(speakExam.getqType()==14){
				type = "integrate"; 
			}else{
				type = "independent";
			}
			if(speakExam.getScore().contains(".5")){
				score = speakExam.getScore().replace(".50", "a");
			}else if(speakExam.getScore().contains("0.5")){
				score = speakExam.getScore().replace("0.50", "a");
			}else{
				score = speakExam.getScore().replace(".00", "");
			}
			String fileName = "IK_XPO_mock/"+XPO_LINK.get(examID)+"/"+speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+XPO_LINK.get(examID)+"_w"+speakExam.getCountScore()+"_"+type+score+".txt";
			System.out.println(fileName);
			BufferedWriter fw = null;
			try {
				File file = new File(exprtDIR+fileName);
				//判断目标文件所在的目录是否存在
		        if(!file.getParentFile().exists()) {
		            //如果目标文件所在的目录不存在，则创建父目录
		            System.out.println("目标文件所在目录不存在，准备创建它！");
		            if(!file.getParentFile().mkdirs()) {
		                System.out.println("创建目标文件所在目录失败！");
		            }
		        }
				fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
				fw.append(speakExam.getAudioUrl());
				fw.flush(); // 全部写入缓存中的内容
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 批量下载口语
	 * @return
	 */
	public List<SpeakExam> getAllSpeakingFile(String type,String examId){
		Connection connection = JDBCConnection.getJDBCConnection()
				.getConnection();
		ResultSet rs = null;
		String sql = "SELECT DISTINCT u.`name` `name`,file.class_code clas_code,file.student_code student_code,file.audio_url audio_url,score.score score,sj.scaled_score count_score \n" + 
				"FROM \n" + 
				"(SELECT er.submit_date time,er.exam_id exam_id,utzr.section_id section_id,utzr.timu_id timu_id,er.time_str time_str,er.class_code class_code,er.class_exam_id class_exam_id,er.student_code student_code,utzr.audio_url audio_url\n" + 
				" FROM \n" + 
				"exam_record er,user_timu_zg_record utzr   WHERE er.time_str=utzr.timestr  and er.exam_id = "+examId+" AND utzr.section_id = 3  ) file,\n" + 
				"(SELECT utzj.timu_id,er.time_str time_str,er.class_code class_code,er.class_exam_id class_exam_id,er.student_code student_code,utzj.score score\n" + 
				" FROM \n" + 
				"exam_record er,user_timu_zg_judge utzj   WHERE er.time_str=utzj.timestr  and er.exam_id = "+examId+" AND utzj.section_id = 3) score,section_judge sj,exam_task task,ls_student u \n" + 
				"WHERE file.timu_id = score.timu_id \n" + 
				"AND file.student_code = score.student_code\n" + 
				"AND file.student_code = sj.`code` AND sj.class_code = file.class_code \n" + 
				"AND sj.section_id = file.section_id AND sj.class_exam_id = score.class_exam_id AND task.time_str = file.time_str AND u.code = file.student_code AND  file.exam_id = "+examId+" AND task.`status` = 3 ORDER BY u.`name` DESC,file.timu_id;";
		java.sql.Statement statement;
		System.out.println(sql);
		System.out.println("----------------");
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			List<SpeakExam> sel = new ArrayList<SpeakExam>();
			
			while (rs.next()) {
				SpeakExam se = new SpeakExam(rs.getString("name"), rs.getString("clas_code"), rs.getString("student_code"), rs.getString("audio_url"), rs.getString("score"), rs.getInt("count_score"),null);
				sel.add(se);
			}
			exportTXT(type,sel,examId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	public void makeSpeapkFile(List<SpeakExam> sle,String examID){
		int index = 0;
		
		for (SpeakExam speakExam : sle) {
			index ++;
			String path = exprtDIR+"IK_XPO_mock_speak/"+XPO_LINK.get(examID)+"/";
			String oldFileName = "E:/user_record"+speakExam.getAudioUrl();
			String fileName = speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+XPO_LINK.get(examID)+"_s"+speakExam.getCountScore()+"_t"+NUM_LINK.get(index+"")+speakExam.getScore().replace(".00", "")+".mp3";
//			fileChannelCopy(new File(oldFileName),new File(path+fileName));
			
			//导出TXT
//			exportTXT(speakExam.getAudioUrl(),fileName);
			if(index == 6){
				index = 0;
			}
		}
	}
	
	public void exportTXT(String type,List<SpeakExam> sle,String examID){
		BufferedWriter fw = null;
		try {
			File file = new File("E://"+type+".txt");
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			int index = 0;
			
			for (SpeakExam speakExam : sle) {
				index ++;
//				String path = exprtDIR+"IK_XPO_mock_speak/"+PAPER_LINK.get(examID)+"/";
//				String oldFileName = "E:/user_record"+speakExam.getAudioUrl();
				String result;
				if(type.equals("TPO")){
					String fileName = speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+TPO_LINK.get(examID)+"_s"+speakExam.getCountScore()+"_t"+NUM_LINK.get(index+"")+speakExam.getScore().replace(".00", "")+".mp3";
					result = "IK_"+type+"_mock_speak/"+TPO_LINK.get(examID)+"/"+fileName;
				}else{
					String fileName = speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+XPO_LINK.get(examID)+"_s"+speakExam.getCountScore()+"_t"+NUM_LINK.get(index+"")+speakExam.getScore().replace(".00", "")+".mp3";
					result = "IK_"+type+"_mock_speak/"+XPO_LINK.get(examID)+"/"+fileName;
				}
				String source = speakExam.getAudioUrl().substring(1, speakExam.getAudioUrl().length()) ;
				fw.append("user_record/"+source   +"\t"+   result);
				fw.newLine();
				fw.flush(); // 全部写入缓存中的内容
				if(index == 6){
					index = 0;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/** *//**文件重命名 
	    * @param path 文件目录 
	    * @param oldname  原来的文件名 
	    * @param newname 新文件名 
	    */ 
	    public void renameFile(String oldname,String newname){ 
	        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
	            File oldfile=new File(oldname); 
	            File newfile=new File(newname); 
	            if(!newfile.getParentFile().exists()) {
		            //如果目标文件所在的目录不存在，则创建父目录
		            System.out.println("目标文件所在目录不存在，准备创建它！");
		            if(!newfile.getParentFile().mkdirs()) {
		                System.out.println("创建目标文件所在目录失败！");
		            }
		        }
	            if(!oldfile.exists()){
	                return ;//重命名文件不存在
	            }
	            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	                System.out.println(newname+"已经存在！"); 
	            else{ 
	                oldfile.renameTo(newfile); 
	            } 
	        }else{
	            System.out.println("新文件名和旧文件名相同...");
	        }
	    }
	    /**
	     * 复制文件
	     * @param s
	     * @param t
	     */
	    public void fileChannelCopy(File s, File t) {

	        FileInputStream fi = null;

	        FileOutputStream fo = null;

	        FileChannel in = null;

	        FileChannel out = null;

	        try {

	        	if(!t.getParentFile().exists()) {
		            //如果目标文件所在的目录不存在，则创建父目录
		            System.out.println("目标文件所在目录不存在，准备创建它！");
		            if(!t.getParentFile().mkdirs()) {
		                System.out.println("创建目标文件所在目录失败！");
		            }
		        }
	            fi = new FileInputStream(s);

	            fo = new FileOutputStream(t);

	            in = fi.getChannel();//得到对应的文件通道

	            out = fo.getChannel();//得到对应的文件通道

	            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

	        } catch (IOException e) {

	            e.printStackTrace();

	        } finally {

	            try {

	                fi.close();

	                in.close();

	                fo.close();

	                out.close();

	            } catch (IOException e) {

	                e.printStackTrace();

	            }

	        }

	    }
	    static
		  {
			  XPO_LINK.put("1", "mockone");
			  XPO_LINK.put("2", "mocktwo");
			  XPO_LINK.put("3", "mockthree");
			  XPO_LINK.put("4", "mockfour");
			  XPO_LINK.put("5", "mockfive");
			  XPO_LINK.put("6", "mocksix");
			  XPO_LINK.put("7", "mockseven");
			  XPO_LINK.put("42", "newmockone");
			  XPO_LINK.put("43", "newmocktwo");
			  XPO_LINK.put("44", "newmockthree");
			  XPO_LINK.put("45", "newmockfour");
			  XPO_LINK.put("67", "newmockfive");
			  XPO_LINK.put("80", "newmockA1");
			  XPO_LINK.put("62", "newmockA2");
			  
			  TPO_LINK.put("32","TPO1");
			  TPO_LINK.put("47","TPO2");
			  TPO_LINK.put("48","TPO3");
			  TPO_LINK.put("49","TPO4");
			  TPO_LINK.put("50","TPO5");
			  TPO_LINK.put("51","TPO6");
			  TPO_LINK.put("52","TPO7");
			  TPO_LINK.put("53","TPO8");
			  TPO_LINK.put("54","TPO9");
			  TPO_LINK.put("55","TPO10");
			  TPO_LINK.put("56","TPO11");
			  TPO_LINK.put("57","TPO12");
			  TPO_LINK.put("58","TPO13");
			  TPO_LINK.put("59","TPO14");
			  TPO_LINK.put("60","TPO15");
			  TPO_LINK.put("61","TPO16");
			  TPO_LINK.put("62","TPO17");
			  TPO_LINK.put("63","TPO18");
			  TPO_LINK.put("64","TPO19");
			  TPO_LINK.put("65","TPO20");
			  TPO_LINK.put("66","TPO21");
			  TPO_LINK.put("30","TPO22");
			  TPO_LINK.put("41","TPO23");
			  TPO_LINK.put("40","TPO24");
			  TPO_LINK.put("25","TPO25");
			  TPO_LINK.put("39","TPO26");
			  TPO_LINK.put("38","TPO27");
			  TPO_LINK.put("37","TPO28");
			  TPO_LINK.put("36","TPO29");
			  TPO_LINK.put("35","TPO30");
			  TPO_LINK.put("34","TPO31");
			  TPO_LINK.put("33","TPO32");
			  TPO_LINK.put("26","TPO33");
			  TPO_LINK.put("27","TPO34");
			  TPO_LINK.put("28","TPO35");
			  TPO_LINK.put("76","TPO36");
			  TPO_LINK.put("77","TPO37");
			  TPO_LINK.put("9","TPO40");
			  TPO_LINK.put("10","TPO41");
			  TPO_LINK.put("11","TPO42");
			  TPO_LINK.put("83","TPO43");
			  
			  
			  
			  NUM_LINK.put("1", "one");
			  NUM_LINK.put("2", "two");
			  NUM_LINK.put("3", "three");
			  NUM_LINK.put("4", "four");
			  NUM_LINK.put("5", "five");
			  NUM_LINK.put("6", "six");
		  }
}
