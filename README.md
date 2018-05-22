## lucene-ik

### 一些功能：

1. 纯天然基于ik源码改造(原ik已经多年不更新维护了，支持lucene4.x)
2. 支持lucene5.x和solr5.x的中文分词，同义词，禁用词，和扩展词
3. 支持从mysql和redis远程加载词库同义词，避免集群环境下，维护多份词库麻烦，需要注意的是，词库改变需要重建索引，而同义词可以动态更新，无须重建索引，当然稍改源码也能支持禁用词和扩展词的远程加载，但是，不建议这块经常变动，数据量大的情况下，重建索引是个麻烦事！


####  （1）如何在lucene里面使用？

```java

/***
	 * @param text 分词的文本
	 * @param analyzer 具体的分词器
	 * @throws Exception
     */
	public static void  testAnalyzer(String text,Analyzer analyzer)throws Exception{
		TokenStream token=analyzer.tokenStream("", new StringReader(text));
		CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
		token.reset();
		while(token.incrementToken()){
			System.out.print(term.toString()+" ");
		}
		token.end();
		token.close();
	}
	
	public static void main(String[] args)throws Exception {
		//支持过滤HTML字符的中文分词器
//		IKHtmlAnalyzer ik=new IKHtmlAnalyzer(true);
		String temp="腾讯是中国领头的互联网公司";
		//lucene里面使用普通的中文分词器,支持扩展词和禁用词
		//IKAnalyzer ik=new IKAnalyzer();//结果=> 腾讯 中国 领头 互联网 公司
		//lucene里面使用增量的中文分词，支持扩展词，禁用词，同义词
		Analyzer ik=new SynIKAnalyzer(true);//结果=>   腾讯 QQ qq 中国 祖国 china 领头 互联网 公司 
		testAnalyzer(temp, ik);
	}
	
```
	
	
#### （2）如何在solr里面使用？
```xml

	<!--  配置IK分词器 -->  
    <fieldType name="ik" class="solr.TextField" positionIncrementGap="100"> 
      <!-- 索引时的配置 -->	
      <analyzer type="index">  
	  <!-- 加入特殊字符过滤 -->
	   <charFilter class="solr.PatternReplaceCharFilterFactory" pattern="([()（） \[\]【】.。，,])" replacement=""/>
      <!--  分词-->  
        <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="false"  />
		<!-- <filter class="solr.TrimFilterFactory" />	-->	
	 <!--	<filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>  -->
		 <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/> 
      </analyzer>  
	  <!-- 查询时的配置 -->
      <analyzer type="query">  
	   <!-- 加入特殊字符过滤 -->
	    <charFilter class="solr.PatternReplaceCharFilterFactory" pattern="([()（） \[\]【】.。，,])" replacement=""/>
        <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="false"   />  
       <!-- <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>   
       可以控制是否开启远程词库加载，已经是否自动更新，如果开启自动更新，还可以设置属性flushtime属性控制，单位是秒
       -->
		 <filter class="org.wltea.analyzer.lucene.IKSynonymFilterFactory" synonyms="synonyms.txt" loadremote="true"  autoupdate="false"   />   
		 <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/> 
      </analyzer>  
    </fieldType>  
```
==
## 博客相关

（1）[微信公众号（woshigcs）：同步更新](https://github.com/qindongliang/answer_sheet_scan/blob/master/imgs/gcs.jpg)

（2）[个人站点(2018之后，同步更新）](http://8090nixi.com/) 

（3）[腾讯云社区，自动同步公众号文章](<http://qindongliang.iteye.com/>)

（4）[csdn ： (暂时同步更新)](https://blog.csdn.net/u010454030)

（5）[iteye（2018.05月之前所有的文章，之后弃用）](<http://qindongliang.iteye.com/>)  






## 我的公众号(woshigcs)

有问题可关注我的公众号留言咨询

![image](https://github.com/qindongliang/answer_sheet_scan/blob/master/imgs/gcs.jpg)

    
    
    
	
	
	
