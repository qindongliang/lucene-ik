package org.wltea.analyzer.lucene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.conndb.DBHelper;
import org.wltea.analyzer.dic.Dictionary;


public class UpdateTime  implements Runnable {
	
	
 public static Logger log=LoggerFactory.getLogger(UpdateTime.class);
	
	 public UpdateTime() {
	 
	}
    /**保存solr的上下文加载器**/
	 public   static ResourceLoader loader=null;
 
	 /**扩展词的路径*/
	 public static  String extdics[]=null;
	 /**禁用词的路径**/
	 public  static  String stopdics[]=null;
	 
 public static class UpdateSingle{
		 private static UpdateTime u=new UpdateTime(); 
		 public static UpdateTime getInstance(){
			 
			 return u;
		 }
		 
		 
	 public static UpdateTime getInstance(String extdics[],String stopdics[],ResourceLoader loader){
		
			 return u;
		 }
		 
		 
	 }
 	static SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**获取词典实例**/
    private static  Dictionary dic=Dictionary.getSingleton();
    
	public void run() {
		try{
			
			System.out.println("进了");
			//List<String> extwords=getByPath(extdics);
			//List<String>stopwords=getByPath(stopdics);
			 List<String> extwords=getByDB("ext");
			 List<String> stopwords=getByDB("stopword");
		     dic.reloadExtandStopWordDict(extwords,stopwords);
		 
		    log.info(f.format(new Date())+"   刷新扩展词典和禁用词典成功.......");
			//System.out.println(f.format(new Date())+"   刷新扩展词典和禁用词典成功.......");	
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}	 
	 
	
	/**
	 * 从数据库获取词库
	 * 
	 * **/
	public List<String> getByDB(String  info)throws Exception{
		
		List<String> list=new ArrayList<String>();
		 
		for(String word:DBHelper.getKey(info).split(",")){
			list.add(word);
		}
		return list;
	}
	
	
   /***
    * 
    * 获取路径下的一系列词 的集合
    * @param  path 要读取的词的路径
    * @return {@link List}读取完毕后词
    * 
    * **/
	public List<String> getByPath(String path[]){
		
		//log.info("执行了  "+f.format(new Date())+"  path ");
		List<String> list=new ArrayList<String>();
		try{
		BufferedReader br=null;
		for(String et:path){
			if(et!=null&&et.length()>0){
			br=new BufferedReader( new InputStreamReader(loader.openResource(et), "UTF-8")) ;
			  String temp=null;
			  while((temp=br.readLine())!=null){
				  if(temp.trim().length()>0){
					  list.add(temp);  
				  }
				 
			  }
			  br.close();
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		
		}
		return list;
}
	}
