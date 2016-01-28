package org.wltea.analyzer.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;


public class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

	private boolean useSmart = false;
	//private Tokenizer _IKTokenizer = null;
	private int flushtime;
	private boolean isAutoUpdate=false;
	String dicPath = null;
	/**存放map的参数*/
  //  private Map<String, String> maps=null;
	public IKTokenizerFactory(Map<String, String> args) {
		super(args);
		//assureMatchVersion();
		this.useSmart = getBoolean(args, "useSmart", false);
		this.dicPath = get(args, "dicPath");
		this.isAutoUpdate=getBoolean(args, "autoupdate", false);
		this.flushtime=getInt(args, "flushtime", 10);
	}

	public boolean useSmart() {
		return this.useSmart;
	}

	 public static Logger log=LoggerFactory.getLogger(IKTokenizerFactory.class);
 
	 static{
	 Configuration cfg = DefaultConfig.getInstance();
	 Dictionary.initial(cfg);
	 }
	public void inform(ResourceLoader loader) throws IOException {
		try {
			
 
			        String extdic[]=null;
					String stopdic[]=null;
			log.info("dicpath: "+dicPath);
			if(null!=dicPath&&dicPath.length()>0){
			String []dic=dicPath.split("#");
			extdic=dic[0].split(",");
			stopdic=dic[1].split(",");
		
			}
			//是否启用自动更新
			if(isAutoUpdate){
			 UpdateTime.extdics=extdic;
			 UpdateTime.stopdics=stopdic;
			 UpdateTime.loader=loader;
			ScheduledExecutorService flushService=Executors.newSingleThreadScheduledExecutor();
			flushService.scheduleAtFixedRate(UpdateTime.UpdateSingle.getInstance(),5, flushtime	, TimeUnit.SECONDS);
			}
			
//		  BufferedReader reader= new BufferedReader( new InputStreamReader(loader.openResource("suggest.txt"), "UTF-8")) ;
//		 
//		  String temp=null;
//				  while((temp=reader.readLine())!=null){
//					  System.out.println("*****************  :  "+temp);
//					  
//				  }
//		  reader.close();
//			if(dicPath!=null &&dicPath.length()>0){
//				
//			 
//				
//				
//				
//			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public Tokenizer create(AttributeFactory attributeFactory) {
		  //return new IKTokenizer(attributeFactory,this.useSmart);
		return new IKTokenizer(this.useSmart);
	}

//	public Tokenizer create(AttributeFactory arg0, Reader arg1) {
//		// TODO Auto-generated method stub
//
//		return new IKTokenizer(arg1,this.useSmart);
//	}
}
