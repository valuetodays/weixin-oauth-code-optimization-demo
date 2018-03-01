package com.billy.weixinoauthcodeoptimization.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * conventer for xml &  string 
 *
 * @author liulei@foorich.com
 * 
 * @since 2016年9月5日 下午5:22:31
 */
public final class XmlStringUtil {
    private XmlStringUtil() {}

    /**
     * xml 2 map
     * @param xmlString xml 
     * @return
     */
    public static Map<String, String> xmlString2Map(String xmlString) {
        if (StringUtils.isEmpty(xmlString)) {
            return null;
        }
      
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xmlString);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        
        Element root = doc.getRootElement(); 
        List<?> children = root.elements();
        Map<String, String> map = new HashMap<String, String>();
        for (Object object : children) {
            Element o = (Element)object;
            map.put(StringUtils.uncapitalize(o.getName()), o.getText());
        }
        
        return map;
    }
}
