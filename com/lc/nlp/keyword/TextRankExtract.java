/**
 * ͨ��TextRank��ȡ�ı��ؼ��ʣ�����String��ʽ���ı� �����List<String>��ʽ�Ĺؼ���
 */
package com.lc.nlp.keyword;

import java.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

public class TextRankExtract
{
    static final float d = 0.85f;    // ����ϵ����һ��ȡֵΪ0.85
    static final int max_iter = 200; //����������
    static final float min_diff = 0.0001f;
    private static  int nKeyword=5;     //ϵͳ��ȡ�Ĺؼ��ֵĸ���
    private static  int coCurrenceWindow=5; //ͬ�ִ��ڵĴ�С
    public TextRankExtract()
    {
      //jdk bug : Exception in thread "main" java.lang.IllegalArgumentException: Comparison method violates its general contract!
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }
    
    /**
     * �����������ݣ���������õ�list��ʽ�Ĺؼ���
     * @param title           ���±���
     * @param content         ��������
     * @param sysKeywordCount ��Ҫ��ȡ�Ĺؼ��ָ���
     * @param window          ͬ�ִ��ڵĴ�С
     * @return                ���ݷ����ź���Ĺؼ����б�
     */
    public static List<String> getKeyword(String title, String content,int sysKeywordCount,int window)
    {
    	nKeyword = sysKeywordCount;
    	Map<String, Float> score = TextRankExtract.getWordScore(title, content, window);      
        //����ȡ�Ĺؼ��ְ�����÷�������
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        }
        );
        
        //System.out.println("��ÿ���ʵ÷�������"+entryList);
        
        List<String> sysKeywordList=new ArrayList<String>();
        
        //List<String>  unmergedList=new ArrayList<String>();
        for (int i = 0; i < nKeyword; ++i){
            try{
        	//unmergedList.add(entryList.get(i).getKey());
        	sysKeywordList.add(entryList.get(i).getKey());
            }catch(IndexOutOfBoundsException e){
            	continue;
            }
        }

/**ʵ������ʾ�ϲ���Ч������
        //���ݴʵ�λ�öԴ����г�ȡ�����Ĺؼ��ʽ��кϲ�        
        for(int i=0;i<unmergedList.size();i++){
        	String word1=unmergedList.get(i);
        	String  mergedKeyword=unmergedList.get(i);
        	for(int j=i;j<unmergedList.size();j++){
        		String word2=unmergedList.get(j);
        		if( (wordPosition.get(word1)-wordPosition.get(word2))== 1){
        			 mergedKeyword=word2+word1;
        			 unmergedList.remove(j);//ɾ���Ѿ��ϲ��Ĵʵ�Ȩ�ص͵�һ��
        		}
        		else if( (wordPosition.get(word1)-wordPosition.get(word2))== -1){
        			mergedKeyword=word1+word2;
        			unmergedList.remove(j);
        		}
        		else 
        			continue;
        	}
        	sysKeywordList.add(mergedKeyword);
        }
        
 */
        
        return sysKeywordList;
    }
    
    
    /**
     * �ж�ĳ�����Ƿ�һ��ֹͣ��
     * @param term  ��Ҫ�жϵĴ���
     * @return      ������ֹͣ�ʷ���false�����򷵻�true
     */
    public static boolean shouldInclude(Term term)
	    {
	        return CoreStopWordDictionary.shouldInclude(term);
	    }
 
    
    /**
     * �������±�������ݣ����طִʺ�ͨ��TextRank�㷨�õ���ÿ���ʵĵ÷�
     * @param title   ���±���
     * @param content ��������
     * @param window  ͬ�ִ��ڴ�С
     * @return  ���������дʼ���÷�
     */
    public static Map<String,Float> getWordScore(String title, String content,int window)
     {
    	 coCurrenceWindow=window;
   	
     	//�ִʲ��жϴ���
         List<Term> termList = HanLP.segment(title + content);            
         int count=1; //��¼�ؼ��ֵ�λ��
         Map<String,Integer> wordPosition = new HashMap<String,Integer>();
         List<String> wordList=new ArrayList<String>();
         //���˵�ֹͣ��
         for (Term t : termList)
         {
             if (shouldInclude(t))
             {
                 wordList.add(t.word);
                 if(!wordPosition.containsKey(t.word))
                 {
                   wordPosition.put(t.word,count);
                   count++;
                 }
             }
         }
        //System.out.println("��ѡ�ؼ����б�:"+wordList);
         
         // ����coCurrenceWindow��С�����й�ϵ�Ĵ���
         Map<String, Set<String>> words = new HashMap<String, Set<String>>();
         Queue<String> que = new LinkedList<String>();
         for (String w : wordList)
         {
             if (!words.containsKey(w))
             {
                 words.put(w, new HashSet<String>());
             }
             que.offer(w);    // insert into the end of the queue
             if (que.size() > coCurrenceWindow)
             {
                 que.poll();  // pop from the queue
             }

             for (String w1 : que)
             {
                 for (String w2 : que)
                 {
                     if (w1.equals(w2))
                     {
                         continue;
                     }

                     words.get(w1).add(w2);
                     words.get(w2).add(w1);
                 }
             }
         }
         
         //System.out.println("��ͼģ��Ϊ:"+words); //ÿ���ʺ���������ϵ�Ĵ���ɵ�hashmap��key��value�е�ֵָ��
         
         // ����ֱ������
         Map<String, Float> score = new HashMap<String, Float>();
         for (int i = 0; i < max_iter; ++i)
         {
             Map<String, Float> m = new HashMap<String, Float>();
             float max_diff = 0;
             for (Map.Entry<String, Set<String>> entry : words.entrySet())
             {
                 String key = entry.getKey();
                 Set<String> value = entry.getValue();
                 m.put(key, 1 - d);
                 for (String other : value)
                 {
                     int size = words.get(other).size();
                     if (key.equals(other) || size == 0) continue;
                     m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other))); 
                 }
                 
                 max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 1 : score.get(key))));
             }
             score = m;
             //��������˳�
             if (max_diff <= min_diff) 
             	break;
         }
         return score;
     }
}

	
