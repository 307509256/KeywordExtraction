/*
 * Dom4J ����xml�ļ�,�����ļ�����tag������tag������
 */

package com.lc.parseXML;

import java.io.File;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Dom4JParseXML implements parseXML 
{

    public  String parseXML(String fileName,String tag) 
    {
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        Document document=null;
        Element rootTag=null,subTag=null;
        boolean hasTag=false;
        try 
        {
        	//Document �ӿڱ�ʾ���� HTML �� XML �ĵ����Ӹ����Ͻ��������ĵ����ĸ������ṩ���ĵ����ݵĻ������ʡ� 
            document = saxReader.read(inputXml);
            //Element �ӿڱ�ʾ HTML �� XML �ĵ��е�һ��Ԫ��
            rootTag = document.getRootElement();
            //elementIterator()��ȡ���е��ӱ�ǩ
            //Iterator�Ƕ� collection ���е����ĵ�������
            for (Iterator i = rootTag.elementIterator(); i.hasNext();) 
            {
                subTag = (Element) i.next();    
                if(subTag.getName().equals(tag))
                {
                	hasTag=true;
                	break;
                }
            }
        } 
        catch (DocumentException e) 
        {
            System.out.println(e.getMessage());
        }
        
        
        if(hasTag)
        	return subTag.getText();
        else 
        	return "not such a tag in the xml file";
    }
}
