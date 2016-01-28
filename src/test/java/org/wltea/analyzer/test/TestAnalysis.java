package org.wltea.analyzer.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKHtmlAnalyzer;
import org.wltea.analyzer.lucene.SynIKAnalyzer;

import java.io.StringReader;

public class TestAnalysis {
	
	public static void main(String[] args)throws Exception {
		
		
		IKAnalyzer ik=new IKAnalyzer(true);
//		IKHtmlAnalyzer ik=new IKHtmlAnalyzer(true);
	//	IKSAnalyzer analyzer=new IKSAnalyzer();
//		String temp="北京奇虎(科技)有限公司";
		String temp="远东控股集团有限公司";
		//IKAnalyzer ik=new IKAnalyzer();
		//SynIKAnalyzer ik=new SynIKAnalyzer(true);
		testAnalyzer(temp, ik);
	//	testAnalyzer(temp, analyzer);
		
		
		
		
		
		
	}
	
	
	public static void  testAnalyzer(String text,Analyzer analyzer)throws Exception{
		TokenStream token=analyzer.tokenStream("", new StringReader(text));
		CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
		token.reset();
		while(token.incrementToken()){
			System.out.println(term.toString());
		}
		token.end();
		token.close();

	}

	public static void  testAnalyzer(String text,IKAnalyzer analyzer)throws Exception{
		TokenStream token=analyzer.tokenStream("", new StringReader(text));
		CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
		token.reset();
		while(token.incrementToken()){
			System.out.println(term.toString());
		}
		token.end();
		token.close();
	}



	public static void  testAnalyzer(String text,SynIKAnalyzer analyzer)throws Exception{
		TokenStream token=analyzer.tokenStream("", new StringReader(text));
		CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
		token.reset();
		while(token.incrementToken()){
			System.out.println(term.toString());
		}
		token.end();
		token.close();



	}
}
