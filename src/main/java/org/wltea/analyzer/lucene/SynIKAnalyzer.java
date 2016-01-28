/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * IK支持同义词的分词器
 */
public final class SynIKAnalyzer extends Analyzer{

	private IKSynonymFilterFactory syfilter=null;
	Map<String, String> argsMap=null;
	public SynIKAnalyzer() {
		initalSynonmFilter();
	}

	/**
	 * 初始化同义词构造器
	 */
	public void initalSynonmFilter(){
		argsMap = new HashMap<String, String>();
		argsMap.put("expand", "true");
		argsMap.put("synonyms", "synonyms.txt");
		argsMap.put("autoupdate", "false");
		argsMap.put("flushtime", "10");
		try {
			syfilter=new IKSynonymFilterFactory(argsMap);
			syfilter.inform(new ClasspathResourceLoader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private boolean useSmart;

	public boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}



	/**
	 * IK分词器Lucene Analyzer接口实现类
	 *
	 * @param useSmart 当为true时，分词器进行智能切分
	 */
	public SynIKAnalyzer(boolean useSmart){
		 
		this.useSmart = useSmart;
		initalSynonmFilter();
	}


	@Override
	protected TokenStreamComponents createComponents(String s) {
		Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
		TokenStream tokenstream= syfilter.create(_IKTokenizer);
		return new TokenStreamComponents(_IKTokenizer, tokenstream);
	}


}
