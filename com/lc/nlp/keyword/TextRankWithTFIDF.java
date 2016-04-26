/*
 * ��TextRank��TF-IDF�����ۺϣ����������ַ�����
 * 1.��ͨ����ÿƪ������ȡ20���ؼ��֣��ٶ���Щ�ؼ��ֽ���TFIDF
 * 2.ͬʱͨ��tf-idf��TextRank���йؼ�����ȡ��ѡȡ��ͬ�Ĺؼ���
 */
package com.lc.nlp.keyword;

import java.util.*;

import com.lc.parseXML.Dom4JParseXML;


public class TextRankWithTFIDF 
{
	/**
	 * ��TextRank�ó��Ĺؼ����ٳ���ÿ���ʵ�IDFֵ��Ȼ�������򷵻�ǰn��
	 * @param dirPath:��Ҫ��ȡ�ؼ��ֵ��ĵ�����Ŀ¼
	 * @param sysKeywords: ��Ҫ��ȡ�Ĺؼ��ֵĸ���
	 * @return: dirPathĿ¼��ÿ���ĵ��Ĺؼ���
	 */
	public static Map<String,List<String>> textRankThenTFIDF(String dirPath, int sysKeywordNum)
	{
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		TextRankExtract tr = new TextRankExtract();
		Dom4JParseXML dom4j = new Dom4JParseXML();
		Map<String,Float> idfForDir = TFIDFExtract.idfForDir(dirPath);
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		String title=null,content= null;
		
		for(String file:fileList)
		{
			title = dom4j.parseXML(file, "title");
			content = dom4j.parseXML(file, "content");
			Map<String,Float> trKeywords = tr.getWordScore(title, content, 5);
			Iterator<Map.Entry<String, Float>> it = trKeywords.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String,Float> temp =it.next();
				String key = temp.getKey();
				trKeywords.put(key, temp.getValue()*idfForDir.get(key));
			}
			//���ݵ÷ִ�С�������򣬲���ȡ�ؼ���
			List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String,Float>>(trKeywords.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Float>>()
				{
					public int compare(Map.Entry<String, Float> c1, Map.Entry<String, Float> c2)
					{
						return c2.getValue().compareTo(c1.getValue());
					}
					
				}
			);
			
			List<String> temp = new ArrayList<String>();
			for (int i=0;i<sysKeywordNum;i++)
			{
				temp.add(entryList.get(i).getKey());
			}
		result.put(file, temp);
		}
		return result;
	}
   
	/**
	 * ��TextRank��TFIDF��ȡ�Ĺؼ��ֽ���ͶƱ��ѡ��ͬʱ���ֵģ�����Ĵ�TFIDF��ѡ
	 * @param dirPath: ��Ҫ��ȡ�ؼ��ֵ��ĵ���Ŀ¼
	 * @param sysKeywords: ��Ҫ��ȡ�Ĺؼ��ֵĸ���
	 * @return: dirPathĿ¼��ÿ���ĵ��Ĺؼ���
	 */
	public static Map<String,List<String>> textRankWithTFIDF(String dirPath, int sysKeywords)
	{
		Map<String, List<String>> result = new HashMap<String,List<String>>();
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		Dom4JParseXML dom4j = new Dom4JParseXML();
		int keywordCandidateNum = 10;
		Map<String,List<String>> tfidfKeywordsForDir = TFIDFExtract.getKeywords(dirPath, keywordCandidateNum);
		List<String> trKeyword = new ArrayList<String>();
		List<String> tfidfKeyword = new ArrayList<String>();
		String title= null,content = null;
		for(String file:fileList)
		{
			title = dom4j.parseXML(file, "title");
			content = dom4j.parseXML(file, "content");
			trKeyword = TextRankExtract.getKeyword(title, content, keywordCandidateNum, 4);
			tfidfKeyword = tfidfKeywordsForDir.get(file);
			
			List<String> temp = new ArrayList<String>();
			for(String keyword:tfidfKeyword)
			{
				if (trKeyword.contains(keyword))
					temp.add(keyword);
				if (temp.size()==sysKeywords)
					break;
			}
			if (temp.size()==sysKeywords)
				result.put(file,temp);
			else
				for(String keyword:tfidfKeyword)
				{
					if (!temp.contains(keyword))
						temp.add(keyword);
				    if (temp.size()==sysKeywords)
				    	result.put(file, temp);
				}
		}
		return result;
	}

}
