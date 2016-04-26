/**
 * ���ܣ����ݸ�����ϵͳ�Ĺؼ��ֵ�������˹���ȡ�����Ĺؼ��֣�����Pֵ��Rֵ��Fֵ���Լ�������ֵ��ƽ��ֵ
 */
package com.lc.nlp.keyword;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat; //��ʽ�������������λС��

public class Caculate {
	//�洢�������µ�����ֵ
	private static List<Float> pValueList=new ArrayList<Float> ();
	private static List<Float> rValueList=new ArrayList<Float> ();
	private static List<Float> fValueList=new ArrayList<Float> ();
	private static int sysLen;
	private static DecimalFormat df=new DecimalFormat("0.00");//������ʽ�����������2λС��
	
	/**
	 * ������ϵͳ�õ��Ĺؼ��ֺ��˹��õ��Ĺؼ��֣�����Pֵ��Rֵ��Fֵ
	 * @param systemKeywords: ϵͳ�㷨��ȡ�Ĺؼ���
	 * @param manualKeywords: �˹���ȡ�Ĺؼ���
	 */
	public static void  caculate(List<String> systemKeywords,String[] manualKeywords)
	{
	    sysLen=systemKeywords.size();
		int manLen=manualKeywords.length;
		//Caculate.printKeywords(systemKeywords,manualKeywords);
		int hit=0; //��ϵͳ�õ��Ĺؼ����м��������˹���ȡ�г��ֵ�
		for(int i=0;i<sysLen;i++)
		{
			for(int j=0;j<manLen;j++)
			{
				if(systemKeywords.get(i).equals(manualKeywords[j]))
				{
					hit++;
					break;
				}	
			}
		}
		
		
		//Get Precision Value
		float pValue=(float)hit/sysLen;
		pValue*=100;//�ðٷֺű�ʾ
        pValueList.add(pValue);
        
        
		//Get Recall Value
	    float rValue=(float)hit/manLen;
	    rValue*=100;
	    rValueList.add(rValue);

	    //Get F-Measure
	    float fValue;
	    if(rValue==0 || pValue == 0)
	    	fValue=0;
	    else
	    	fValue=2*rValue*pValue/(rValue+pValue);
	    
	    fValueList.add(fValue);
	    
	}

	/**
	 * ��ӡϵͳ��ȡ���˹���ȡ�Ĺؼ���
	 * @param systemKeywords: ϵͳ�㷨��ȡ�Ĺؼ���
	 * @param manualKeywords: �˹���ȡ�Ĺؼ���
	 */
	public static void printKeywords(List<String> systemKeywords,String[] manualKeywords)
	{	
		//�˹���
		System.out.print("�˹���ȡ�Ĺؼ���:");
		int manLen=manualKeywords.length;
		for(int i=0;i<manLen;i++)
			System.out.print(manualKeywords[i]+' ');
		System.out.println(' ');
				
		//ϵͳ��
		System.out.print("ϵͳ��ȡ�Ĺؼ���:");
		int sysLen=systemKeywords.size();
		for(int i=0;i<sysLen;i++)
			System.out.print(systemKeywords.get(i)+" ");
		System.out.println(" ");	
	}

	/**
	 * ������������ֵ��ƽ��ֵд���ļ�
	 * @param fileName: �������ļ�
	 */
	public static void writeAverageResult(String fileName)
	{
		float sum=0;
		int pLen=pValueList.size();
		for(int i=0;i<pLen;i++)
			sum+=pValueList.get(i);
		String pResult="Pֵ��ƽ��ֵΪ("+pLen+')'+df.format(sum/pLen)+"%\n";
		
		sum=0;
		int rLen=rValueList.size();
		for(int i=0;i<rLen;i++)
			sum+=rValueList.get(i);
		String rResult="Rֵ��ƽ��ֵΪ("+rLen+')'+df.format(sum/rLen)+"%\n";
		
		sum=0;
		int fLen=fValueList.size();
		for(int i=0;i<fLen;i++)
			sum+=fValueList.get(i);
		String fResult="Fֵ��ƽ��ֵΪ("+fLen+')'+df.format(sum/fLen)+"%\n";
		
		//������д�뵽�ļ���
		BufferedWriter  bw=null;
		try
		{
			bw=new BufferedWriter(new FileWriter(new File(fileName),true));//true����Ϊ׷���ļ�
			bw.write("\nϵͳ��ȡ�ؼ��ָ���Ϊ"+sysLen+"\n"+pResult+rResult+fResult);		
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				bw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		//��ո���list
		pValueList.clear();
		rValueList.clear();
		fValueList.clear();
	}

}

