package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.conndb.DBHelper;
import org.wltea.analyzer.conndb.RedisHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class IKSynonymFilterFactory extends TokenFilterFactory implements ResourceLoaderAware 
,Runnable{
  private String synonyms;
  private SynonymMap map;
  private boolean loadremote;
  private boolean ignoreCase;
  private boolean expand;
  private ResourceLoader loader = null;
  boolean isAutoUpdate;
  Analyzer analyzer = null;
   int  flushtime;
  public IKSynonymFilterFactory(Map<String, String> args)
    throws IOException
  {
    super(args);

    this.expand = getBoolean(args, "expand", true);
    this.synonyms = get(args, "synonyms");
    this.ignoreCase = getBoolean(args, "ignoreCase", false);
    this.isAutoUpdate = getBoolean(args, "autoupdate", false);
    this.loadremote=getBoolean(args,"loadremote",false);
    this.flushtime=getInt(args, "flushtime", 10);
   
     
  }

  public void inform(ResourceLoader loader)
    throws IOException
  {


    Analyzer analyzer = new Analyzer()
    {
      protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
        TokenStream stream = IKSynonymFilterFactory.this.ignoreCase ? new LowerCaseFilter(tokenizer) : tokenizer;
        return new Analyzer.TokenStreamComponents(tokenizer, stream);
      }
    };
   // System.out.println("<IKSynonymFilterFactory>inform---loadSolrSynonyms!");
    try
    {
//       this.map = loadSolrSynonyms(loader, true, analyzer);//加载配置文件使用
		//this.map = loadDBSynonyms(loader, true, analyzer);//从数据库加载使用
      if(loadremote){//是否远程加载
        this.map=loadRedisSynonyms(loader,true,analyzer);//从redis加载使用
      }else{
        this.map=loadSolrSynonyms(loader, true, analyzer);//加载配置文件使用
      }

	 
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Exception thrown while loading synonyms", e);
    }

    if ((this.isAutoUpdate) && (this.synonyms != null) && (!this.synonyms.trim().isEmpty()))
    {
      this.loader = loader;
      this.analyzer = analyzer;
    }
    if(isAutoUpdate){
    	   ScheduledExecutorService updateService=Executors.newSingleThreadScheduledExecutor();
           updateService.scheduleAtFixedRate(this, 5, flushtime, TimeUnit.SECONDS);
    }
    
  }


  private SynonymMap loadSolrSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer)
    throws IOException, ParseException
  {
    if (this.synonyms == null) {
      throw new IllegalArgumentException(
        "Missing required argument 'synonyms'.");
    }
    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder()
      .onMalformedInput(CodingErrorAction.REPORT)
      .onUnmappableCharacter(CodingErrorAction.REPORT);

    SolrSynonymParser parser = new SolrSynonymParser(dedup, this.expand, 
      analyzer);
    File synonymFile = new File(this.synonyms);
    if (loader != null) {
      if (synonymFile.exists()) {
        decoder.reset();
        
        //parser.add(new InputStreamReader(loader.openResource(this.synonyms)));
       parser.parse(new InputStreamReader(loader.openResource(this.synonyms), decoder));
     
      } else {
        List<String> files = splitFileNames(this.synonyms);
        for (String file : files) {
          decoder.reset();
         // parser.add(new InputStreamReader(loader.openResource(file), 
        	  parser.parse(new InputStreamReader(loader.openResource(file), 
             decoder));
        }
      }
    }
    return parser.build();
  }

  
  /**
   * 从数据库加载同义词
   * 
   * **/
  private SynonymMap loadDBSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer)
		    throws Exception, ParseException {
	        //System.out.println("进同义词了....");
		    SolrSynonymParser parser = new SolrSynonymParser(dedup, this.expand, analyzer);
		    String dbtxt=DBHelper.getKey("synonym");	    
		    dbtxt=dbtxt.replace("#", "\n");
		    parser.parse(new StringReader(dbtxt));
		    return parser.build();
		  }


  private SynonymMap loadRedisSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer){
    SynonymMap smap=null;
    SolrSynonymParser parser = new SolrSynonymParser(dedup, this.expand, analyzer);
    try {
      String dbtxt = RedisHelper.getKey("synonym");
      parser.parse(new StringReader(dbtxt));
      smap=parser.build();
    }catch (Exception e){
      log.error("从Redis加载同义词库异常！");
    }
    return smap;
  }
  
  
  
  
  
  public static Logger log=LoggerFactory.getLogger(IKSynonymFilterFactory.class);


  
  public TokenStream create(TokenStream input)
  {
	  
	  if(input==null){
		  System.out.println("input is null");
	  }
	  if(this.map==null){
		  System.out.println("map is null");
	  }
	  
    return this.map.fst == null ? input : new SynonymFilter(input, this.map, this.ignoreCase);
  }
  static SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public  void update() {
    try {
      if(isAutoUpdate) {
        if (loadremote) {
          this.map = loadRedisSynonyms(loader, true, analyzer);
        } else if (!loadremote) {
          this.map = loadSolrSynonyms(loader, true, analyzer);
        }
        log.info("{} 同义词库词库更新了.....",f.format(new Date()));
      }
    } catch (Exception e) {
      log.error("< 同义词库更新异常： IKSynonymFilterFactory> IOException!!",e);
    }
  }

 
public void run() {
          this.update();
	 
}
}
 