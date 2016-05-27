package cn.xdf.tools.exportIK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import cn.xdf.tools.entity.IKEntity;
import cn.xdf.tools.entity.SpeakExam;
import cn.xdf.tools.util.ExportExcel;


public class ExportReadAndListen {

	final String  exprtDIR = "E:/"; 
	
	static Map<String, String> PAPER_LINK = new HashMap();
	static Map<String, String> NUM_LINK = new HashMap();

	  static
	  {
//		  NUM_LINK.put("32",1);
//		  NUM_LINK.put("47",2);
//		  NUM_LINK.put("48",3);
//		  NUM_LINK.put("49",4);
//		  NUM_LINK.put("50",5);
//		  NUM_LINK.put("51",6);
//		  NUM_LINK.put("52",7);
//		  NUM_LINK.put("53",8);
//		  NUM_LINK.put("54",9);
		 /* NUM_LINK.put("55",10);
		  NUM_LINK.put("56",11);
		  NUM_LINK.put("57",12);
		  NUM_LINK.put("58",13);
		  NUM_LINK.put("59",14);*/
//		  NUM_LINK.put("60",15);
//		  NUM_LINK.put("61",16);
//		  NUM_LINK.put("62",17);
//		  NUM_LINK.put("63",18);
//		  NUM_LINK.put("64",19);
//		  NUM_LINK.put("65",20);
//		  NUM_LINK.put("66",21);
//		  NUM_LINK.put("30",22);
//		  NUM_LINK.put("41",23);
//		  NUM_LINK.put("40",24);
//		  NUM_LINK.put("25",25);
//		  NUM_LINK.put("39",26);
//		  NUM_LINK.put("38",27);
//		  NUM_LINK.put("37",28);
//		  NUM_LINK.put("36",29);
//		  NUM_LINK.put("35",30);
//		  NUM_LINK.put("34",31);
//		  NUM_LINK.put("33",32);
//		  NUM_LINK.put("26",33);
//		  NUM_LINK.put("27",34);
//		  NUM_LINK.put("28",35);
//		  NUM_LINK.put("76",36);
//		  NUM_LINK.put("77",37);
//		  NUM_LINK.put("9",40);
//		  NUM_LINK.put("10",41);
//		  NUM_LINK.put("11",42);
//		  NUM_LINK.put("83",43);
/**************************************
 * XPO
 */
		  NUM_LINK.put("1","XPO 1");
//		  NUM_LINK.put("2","XPO 2");
//		  NUM_LINK.put("3","XPO 3");
//		  NUM_LINK.put("4","XPO 4");
//		  NUM_LINK.put("5","XPO 5");
//		  NUM_LINK.put("6","XPO 6");
//		  NUM_LINK.put("7","XPO 7");
//		  NUM_LINK.put("42","新XPO1");
//		  NUM_LINK.put("43","新XPO2");
//		  NUM_LINK.put("44","新XPO3");
//		  NUM_LINK.put("45","新XPO4");
//		  NUM_LINK.put("67","新XPO5");
//		  NUM_LINK.put("80","XPO A1");
//		  NUM_LINK.put("62","XPO A2");
	  }

	public static void main(String[] args) {
		ExportReadAndListen ex = new ExportReadAndListen();
		for (String key : NUM_LINK.keySet()) {
			System.out.println(key);
			   ex.findItems(key);
			  }
		
	}
	Connection connection = JDBCConnection.getJDBCConnection().getConnection();
	
	public void findItems(String examId){
		
		ResultSet rs = null;
		String sql ="select er.student_code student_code,er.class_code class_code,er.create_date create_date,GROUP_CONCAT(utr.item_code ORDER BY utr.timu_id) read_item,listen.s listen_item  \r\n" + 
				"from user_timu_record utr,exam_record er,(select er.time_str ts,GROUP_CONCAT(utr.item_code ORDER BY utr.timu_id) s \r\n" + 
				"from user_timu_record utr,exam_record er where utr.timestr = er.time_str  AND  utr.section_id = 2\r\n" + 
				"AND er.exam_id = "+examId+"  GROUP BY er.student_code ORDER BY er.class_code) listen where utr.timestr = er.time_str AND listen.ts =er.time_str  AND  utr.section_id = 1 AND er.submit_status = 1\r\n" + 
				"AND er.exam_id = "+examId+"   GROUP BY er.student_code ORDER BY er.class_code";
		System.out.println("findItems-------"+sql);
		java.sql.Statement statement;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			List<IKEntity> sel = new ArrayList<IKEntity>();
			while (rs.next()) {
				IKEntity tpo = new IKEntity(rs.getString("student_code"), rs.getString("class_code"), rs.getString("create_date"), rs.getString("read_item"), rs.getString("listen_item"));
				sel.add(tpo);
			}
			System.out.println("开始找份");
			findScore(sel, examId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String findListen(IKEntity tpo,String examId){
		ResultSet rs = null;
		String sql ="SELECT u.item_code,r.right_answer,u.is_right FROM (select id,right_answer from timu where id in(select timu_id from audio_timu where exam_id ="+examId+") order by id)r \r\n" + 
				"LEFT JOIN (SELECT timu_id,item_code,is_right FROM user_timu_record WHERE timestr = (select time_str from exam_record where student_code = '"+tpo.getStuId()+"' and class_code = '"+tpo.getClassCode()+"' AND exam_id = "+examId+"))u\r\n" + 
				"ON r.id = u.timu_id;";
		java.sql.Statement statement;
		System.out.println("findListen-------"+sql);
		StringBuffer liten = new StringBuffer();
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				liten.append(rs.getString("item_code"));
				liten.append(",");
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return liten.toString();
	}
	
	
	public void findScore(List<IKEntity> tpos,String examId){
		List<IKEntity> sel = new ArrayList<IKEntity>();
		for (IKEntity tpo : tpos) {
			ResultSet rs = null;
			String sql ="SELECT sj.section_id,sj.scaled_score,u.name FROM section_judge sj,ls_student u where u.code = sj.code AND sj.code = '"+tpo.getStuId()+"' AND sj.class_code = '"+tpo.getClassCode()+"' AND sj.exam_id = "+examId+" ;";
			System.out.println("findScore------"+sql);
			java.sql.Statement statement;
			try {
				statement = connection.createStatement();
				rs = statement.executeQuery(sql);
				
				String sectionId = "";
				while (rs.next()) {
					sectionId = rs.getString("section_id");
					if(sectionId.equals("1")){
						tpo.setReadingScore(rs.getInt("scaled_score"));
					}else if(sectionId.equals("2")){
						tpo.setListeningScore(rs.getInt("scaled_score"));
					}else if(sectionId.equals("3")){
						tpo.setSpeakingScore(rs.getInt("scaled_score"));
					}else{
						tpo.setWritingScore(rs.getInt("scaled_score"));
					}
					tpo.setName(rs.getString("name"));
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Integer s = (tpo.getSpeakingScore()==null?0:tpo.getSpeakingScore());
			Integer w = (tpo.getWritingScore()==null?0:tpo.getWritingScore());
			tpo.setSpeakingScore(s);
			tpo.setWritingScore(w);
			if(null == tpo.getReadingScore() || null ==tpo.getListeningScore() )
				continue;
			tpo.setTotalScore(tpo.getReadingScore()+tpo.getListeningScore()+s+w);
			//单独查询听力分数
			tpo.setListeningItems(findListen(tpo,examId));
			sel.add(tpo);
			System.out.println("-------------");
		}
		ExportExcel<IKEntity> ex = new ExportExcel<IKEntity>();
	    String[] headers = { "学生姓名","学号ID", "班号", "创建时间", "总分", "阅读","听力","口语","写作","阅读选项","听力选项"};
	    try {
			OutputStream out = new FileOutputStream("E://"+NUM_LINK.get(examId)+".xls");
			ex.exportExcel(headers, sel, out);
			out.close();
			System.out.println("excel导出成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			String fileName = "IK_XPO_mock/"+PAPER_LINK.get(examID)+"/"+speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+PAPER_LINK.get(examID)+"_w"+speakExam.getCountScore()+"_"+type+score+".txt";
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
	public List<SpeakExam> getAllSpeakingFile(String examId){
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
				"exam_record er,user_timu_zg_judge utzj   WHERE er.time_str=utzj.timestr  and er.exam_id = "+examId+" AND utzj.section_id = 3) score,section_judge sj,exam_task task,sys_user u \n" + 
				"WHERE file.timu_id = score.timu_id \n" + 
				"AND file.student_code = score.student_code\n" + 
				"AND file.student_code = sj.`code` AND sj.class_code = file.class_code \n" + 
				"AND sj.section_id = file.section_id AND task.time_str = file.time_str AND u.source_code = file.student_code AND  file.exam_id = "+examId+" AND task.`status` = 3 ORDER BY u.`name` DESC,file.timu_id;";
		java.sql.Statement statement;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			List<SpeakExam> sel = new ArrayList<SpeakExam>();
			
			while (rs.next()) {
				SpeakExam se = new SpeakExam(rs.getString("name"), rs.getString("clas_code"), rs.getString("student_code"), rs.getString("audio_url"), rs.getString("score"), rs.getInt("count_score"),null);
				sel.add(se);
			}
			makeSpeapkFile(sel,examId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	public void makeSpeapkFile(List<SpeakExam> sle,String examID){
		int index = 0;
		
		for (SpeakExam speakExam : sle) {
			index ++;
			String path = exprtDIR+"IK_XPO_mock_speak/"+PAPER_LINK.get(examID)+"/";
			String oldFileName = "E:/user_record"+speakExam.getAudioUrl();
			String fileName = speakExam.getClassCode()+"_"+speakExam.getName()+"_"+speakExam.getStudentCode()+"_"+PAPER_LINK.get(examID)+"_s"+speakExam.getCountScore()+"_t"+NUM_LINK.get(index+"")+speakExam.getScore().replace(".00", "")+".mp3";
			fileChannelCopy(new File(oldFileName),new File(path+fileName));
			
			if(index == 6){
				index = 0;
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
}
