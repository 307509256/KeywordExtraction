package com.lc.nlp.keyword;

import java.io.*;
import java.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.lc.parseXML.Dom4JParseXML;

public class TFIDFExtract 
{
	
	/**
     * �����ļ�·�����õ��ļ���ÿ���ʵ�TFֵ
     * @param filePath: �ļ���·��
     * @return  ��ʾÿ���ʵ�TFֵ��һ��HashMap<String, Float>
     */
    public static HashMap<String, Float> getTF(String filePath)
    {    
    	// ���ļ����зִʲ���
    	List<Term> terms=new ArrayList<Term>();
        ArrayList<String> words = new ArrayList<String>();
        Dom4JParseXML dom4j = new Dom4JParseXML();
        String text=null,title=null;
        try
        {   
        	title = dom4j.parseXML(filePath,"title");
            text = dom4j.parseXML(filePath,"content");
        }
        catch(Exception e)
        {
        	System.out.println(filePath);
        }
        terms=HanLP.segment(title+text);
        for(Term t:terms)
        {
        	if(TFIDFExtract.shouldInclude(t))
        	{
        		words.add(t.word);
        	}      		
        }
        
        //ͳ�Ʒִʺ��list���õ�ÿ���ʵ�TFֵ
    	 HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
    	 HashMap<String, Float> TFValues = new HashMap<String, Float>();
    	 for(String word : words)
         {
             if(wordCount.get(word) == null)
             {
            	 wordCount.put(word, 1);
             }
             else
             {
            	 wordCount.put(word, wordCount.get(word) + 1);
             }
         }
    	 
         int wordLen = words.size();
         //����HashMapһ�ֳ��÷���
         Iterator<Map.Entry<String, Integer>> iter = wordCount.entrySet().iterator(); 
         while(iter.hasNext())
         {
             Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)iter.next();
             TFValues.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
           //System.out.println(entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
         }
         return TFValues;
     } 
  
    
    /**
     * �ж�һ�����Ƿ�Ӧ����Ϊ��ѡ��
     * @param term: ���жϵĴ�
     * @return  boolean
     */
    public static boolean shouldInclude(Term term)
    {
        return CoreStopWordDictionary.shouldInclude(term);
    }
      
    
    /**
     * ����Ŀ¼��·��,�õ���Ŀ¼��ÿ���ļ���ÿ���ʵ�TFֵ
     * @param dirc: �ļ�����Ŀ¼
     * @return: ��ʾÿ���ļ���ÿ���ʵ�TFֵ��һ��HashMap<String,HashMap<String, Float>>
     * @throws IOException
     */
    public static HashMap<String,HashMap<String, Float>> tfForDir(String dirPath) 
    {
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        List<String> filelist = ReadDir.readDirFileNames(dirPath);
        
        for(String file : filelist)
        {
            HashMap<String, Float> dict = new HashMap<String, Float>();
            dict = TFIDFExtract.getTF(file);
            allTF.put(file, dict);
        }
        return allTF;
    }

    
    /**
     * ����Ŀ¼·�����õ����·���������ļ����дʵ�idfֵ
     * @param dirPath: �ļ�����Ŀ¼
     * @return ���������ʵ�IDFֵ��һ��HashMap<String, Float>
     */
    public static HashMap<String, Float> idfForDir(String dirPath)
    {
    	List<String> fileList = new ArrayList<String>();
    	fileList = ReadDir.readDirFileNames(dirPath);
    	int docNum = fileList.size(); //�õ���������
    	
    	Dom4JParseXML dom4j = new Dom4JParseXML();
        Map<String, Set<String>> passageWords = new HashMap<String, Set<String>>(); //�洢ÿƪ���³��ֵĲ��ظ��ĵ���       
        // �õ�ÿƪ���µ����в��ظ�����
        for(String filePath:fileList)
        {   
        	List<Term> terms=new ArrayList<Term>();
            Set<String> words = new HashSet<String>();
        	String text=null,title=null;
            try
            {
                title = dom4j.parseXML(filePath,"title");
            	text = dom4j.parseXML(filePath,"content");
            }
            catch(Exception e)
            {
            	System.out.println(filePath);
            }
            terms=HanLP.segment(title+text);
            for(Term t:terms)
            {
            	if(TFIDFExtract.shouldInclude(t))
            	{
            		words.add(t.word);
            	}      		
            }
            passageWords.put(filePath, words);
        }
        
        // ����ÿ���ʵ�idfֵ
        HashMap<String, Integer> wordPassageNum = new HashMap<String, Integer>();//�洢���ʼ�����ֵ���������
        for(String filePath : fileList)
        {
            Set<String> wordSet = new HashSet<String>();
            wordSet = passageWords.get(filePath);
            for(String word:wordSet)
            {           	
                if(wordPassageNum.get(word) == null)
                	wordPassageNum.put(word,1);
                else             
                	wordPassageNum.put(word, wordPassageNum.get(word) + 1);           
            }
        }
        
        HashMap<String, Float> wordIDF = new HashMap<String, Float>(); // �洢ÿ���ʵ�IDFֵ
        Iterator<Map.Entry<String, Integer>> iter_dict = wordPassageNum.entrySet().iterator();
        while(iter_dict.hasNext())
        {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)iter_dict.next();
            float value = (float)Math.log( docNum / (Float.parseFloat(entry.getValue().toString())) );
            wordIDF.put(entry.getKey().toString(), value);
            //System.out.println(entry.getKey().toString() + "=" +value);
        }
        return wordIDF;
    }

    
    /**
     * ����Ŀ¼��·�����õ����ļ���ÿ���ļ���ÿ���ʵ�TF-IDFֵ
     * @param dirPath:�ļ����ڵ�Ŀ¼
     */
    public static Map<String, HashMap<String, Float>> getDirTFIDF(String dirPath)
    {
        HashMap<String, HashMap<String, Float>> dirFilesTF = new HashMap<String, HashMap<String, Float>>(); //Ŀ¼�¸����ļ��ĸ����ʵ�TFֵ
        HashMap<String, Float> dirFilesIDF = new HashMap<String, Float>();//Ŀ¼�����дʵ�IDFֵ
        
        dirFilesTF = TFIDFExtract.tfForDir(dirPath);
        dirFilesIDF = TFIDFExtract.idfForDir(dirPath);
        
        Map<String, HashMap<String, Float>> dirFilesTFIDF = new HashMap<String, HashMap<String, Float>>();//Ŀ¼�����дʵ�TFIDFֵ
        Map<String,Float> singlePassageWord= new HashMap<String,Float>();
        List<String> fileList = new ArrayList<String>();
        fileList = ReadDir.readDirFileNames(dirPath);
        for (String filePath: fileList)
        {
        	HashMap<String,Float> temp= new HashMap<String,Float>();
        	singlePassageWord = dirFilesTF.get(filePath);
        	Iterator<Map.Entry<String, Float>> it = singlePassageWord.entrySet().iterator();
        	while(it.hasNext())
        	{
        		Map.Entry<String, Float> entry = it.next();
        		String word = entry.getKey();
        		Float TFIDF = entry.getValue()*dirFilesIDF.get(word);
        		temp.put(word, TFIDF);
        	}
        	dirFilesTFIDF.put(filePath, temp);
        }
        return dirFilesTFIDF;
    }
 
    
    /**
     * �����ļ���·������ȡ�Ĺؼ��ֵĸ����������ļ�����ÿ���ļ���ȡ�Ĺؼ��� 
     * @param dirPath: �ļ���·��
     * @param keywordNum: ��Ҫ��ȡ�Ĺؼ��ֵĸ���
     * @return ����ÿƪ���µĹؼ��ֵ�һ��Map<String,List<String>>
     */
    public static Map<String,List<String>> getKeywords(String dirPath, int keywordNum)
    {
    	List<String> fileList = new ArrayList<String>();
    	fileList = ReadDir.readDirFileNames(dirPath);
    	
    	Map<String, HashMap<String, Float>> dirTFIDF = new HashMap<String, HashMap<String, Float>>(); //�õ�Ŀ¼��ÿ���ļ���ÿ���ʵ�TFIDFֵ 
    	dirTFIDF = TFIDFExtract.getDirTFIDF(dirPath);
    	
    	Map<String,List<String>> keywordsForDir = new HashMap<String,List<String>>(); //�洢Ŀ¼��ÿ���ļ���ȡ�Ĺؼ���
    	for (String file:fileList)
    	{
    		Map<String,Float> singlePassageTFIDF= new HashMap<String,Float>();
    		singlePassageTFIDF = dirTFIDF.get(file);
    		
    		//�Եõ���TFIDFֵ�Ӵ�С����
	        List<Map.Entry<String,Float>> entryList=new ArrayList<Map.Entry<String,Float>>(singlePassageTFIDF.entrySet());
	        
	
	        Collections.sort(entryList,new Comparator<Map.Entry<String,Float>>()
	        {
	        	@Override
	        	public int compare(Map.Entry<String,Float> c1,Map.Entry<String,Float> c2)
	        	{
	        		return c2.getValue().compareTo(c1.getValue()); //�����������Ҫ��������ĳ�c1.getValue().compareTo(c2.getValue())	        		
	        	}
	        }
	        );
	        	        
	        //��ȡǰn���ؼ���List
            List<String> systemKeywordList=new ArrayList<String>();
            for(int k=0;k<keywordNum;k++)
            {
            	try
            	{
            	systemKeywordList.add(entryList.get(k).getKey());
            	}
            	catch(IndexOutOfBoundsException e)
            	{
            		continue;
            	}
            }
            
            keywordsForDir.put(file, systemKeywordList);
        }
        return keywordsForDir;
    }
           
}
	


