package net.itdiandi.java.utils.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName XmlTool
* @Description TODO
* @Author 刘吉超
* @Date 2015-07-20 09:52:40
*/
public class XmlTool {
	private static final Logger logger = LoggerFactory.getLogger(XmlTool.class);

	 /**
	 * 创建Document
	 * 
	 * @param docContent
	 * @param process_code
	 * @return Document
	*/
	public static Document createMsg(Document docContent, String process_code)
	 {      
	        Document doc = DocumentHelper.createDocument();
	        doc.setXMLEncoding("UTF-8");
	        
	        // 根节点
	        Element root = doc.addElement("operation_in");
	        
	        // 接口服务名(必须填写, 唯一标示一个业务命令字)
	        Element pcodeE = root.addElement("process_code");
	        pcodeE.addText(process_code);
	        root.add(docContent.getRootElement());
	        
	        return doc;
	    }
	    
	    /**
	     * 在请求报文的最外层加一层操作名
	     * 
	     * @param docXML
	     * @param operation
	     * @return Document
	    */
	    public static Document getDocXmlByOperationCode(Document docXML, String operation)
	    {
	        // 如果操纵名不为空， 摘取请求报文根节点挂在操作名节点下
	        if (StringUtils.isNotEmpty(operation))
	        {
	            Document doc = DocumentHelper.createDocument();
	            doc.setXMLEncoding("UTF-8");
	            // 创建根节点
	            Element rootEle = doc.addElement(operation);
	            // 摘取请求报文根节点挂在操作名节点下
	            rootEle.add(docXML.getRootElement());
	            docXML = doc;
	        }
	        return docXML;
	    }
	    
	    public static String parseResponse(String response) throws Exception
	    {
	        Document doc = DocumentHelper.parseText(response);
	        Element root = doc.getRootElement();
	        // Element result = root.element("response");
	        return root.asXML();
	    }
	    
	    public static String send(String requestXML, String crmUrl, String soapAction) throws Exception
	    {
	        URL url = null;
	        HttpURLConnection urlConn = null;
	        BufferedReader in = null;
	        PrintWriter out = null;
	        String sLine = null;
	        StringBuffer sbBuf = new StringBuffer();
	        
	        try
	        {
	            url = new URL(crmUrl);
	            urlConn = (HttpURLConnection)url.openConnection();
	            urlConn.setConnectTimeout(10000);
	            urlConn.setReadTimeout(50000);
	            
	            urlConn.setRequestMethod("POST");
	            urlConn.setDoInput(true);
	            urlConn.setDoOutput(true);
	            urlConn.setUseCaches(false);
	            
	            urlConn.setRequestProperty("Content-Type", "text/respsi; charset=GBK");
	            
	            urlConn.setRequestProperty("SOAPAction", soapAction);
	            
	            out = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream(), "GBK"));
	            
	            out.println(requestXML);
	            
	            out.flush();
	            
	            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "GBK"));
	            
	            while ((sLine = in.readLine()) != null)
	            {
	                sbBuf.append(sLine).append("\n");
	            }
	        }
	        finally
	        {
	            if (out != null)
	            {
	                out.close();
	                out = null;
	            }
	            
	            if (in != null)
	            {
	                in.close();
	                in = null;
	            }
	            
	            if (urlConn != null)
	            {
	                urlConn.disconnect();
	                urlConn = null;
	            }
	        }
	        
	        return sbBuf.toString();
	    }
	    
	    /**
	     * 转换key
	     * 
	     * @param key
	     * @return
	     * @return String
	    */
	    public static String convertKey(String key)
	    {
	        return key;
	        
	        // 读取配置文件
	        
	    }
	    
	    /**
	     * 是否报文头
	     * 
	     * @param key
	     * @return boolean
	    */
	    public static boolean isHead(String key)
	    {
	        Set<String> set = new HashSet<String>();
	        set.add("ORIGDOMAIN");
	        set.add("HOMEDOMAIN");
	        set.add("BIPCODE");
	        set.add("ACTIVITYCODE");
	        set.add("TESTFLAG");
	        set.add("X_TRANS_CODE");
	        set.add("KIND_ID");
	        set.add("BUSI_SIGN");
	        set.add("UIPBUSIID");
	        set.add("BIPVER");
	        set.add("ACTIONCODE");
	        set.add("ROUTETYPE");
	        set.add("ROUTEVALUE");
	        set.add("PROCID");
	        set.add("TRANSIDO");
	        set.add("TRANSIDH");
	        set.add("PROCESSTIME");
	        set.add("TRANSIDC");
	        set.add("CUTOFFDAY");
	        set.add("OSNDUNS");
	        set.add("HSNDUNS");
	        set.add("CONVID");
	        set.add("MSGSENDER");
	        set.add("MSGRECEIVER");
	        set.add("SVCCONTVER");
	        set.add("OPR_NUMB");
	        set.add("TRADE_CITY_CODE");
	        set.add("TRADE_DEPART_ID");
	        set.add("TRADE_STAFF_ID");
	        set.add("TRADE_EPARCHY_CODE");
	        set.add("IN_MODE_CODE");
	        return set.contains(key);
	    }
	    
	    /**
	     * TODO
	     * 
	     * @param strXml
	     * @param headMap
	     * @param detailList
	     * @return void
	    */
	    public static void stringSplitter(String strXml, Map<String, String> headMap, List<Map<String, String>> detailList)
	    {
	        // String string =
	        // "{ORIGDOMAIN=[\"CTRM\"], HOMEDOMAIN=[\"BOSS\"], BIPCODE=[\"BIP2B477\"], ACTIVITYCODE=[\"T2002177\"], TESTFLAG=[\"0\"], X_TRANS_CODE=[\"ITF_RES_MISSION\"], KIND_ID=[\"BIP2B477_T2002177_1_0\"], BUSI_SIGN=[\"BIP2B477_T2002177_1_0\"], UIPBUSIID=[\"313072211443683086704\"], BIPVER=[\"0100\"], ACTIONCODE=[\"0\"], ROUTETYPE=[\"00\"], ROUTEVALUE=[\"898\"], PROCID=[\"CTRM1374456820130722093433\"],TRANSIDO=[\"CTRMT410103520130722093433\"], TRANSIDH=[\"\"],PROCESSTIME=[\"20130722093433\"], TRANSIDC=[\"0000BBSSBBSST410103520130722000462\"],CUTOFFDAY=[\"20130722\"], OSNDUNS=[\"0000\"], HSNDUNS=[\"8980\"], CONVID=[\"001900005555BBSST41010352013072200046220130722092954467\"],MSGSENDER=[\"8980\"], MSGRECEIVER=[\"8981\"], SVCCONTVER=[\"0100\"], OPR_NUMB=[\"\"], TAKE_DATE=[\"20130601000000\", \"20130701000000\", \"20130601000000\"], QUOTATION=[\"50\", \"55\", \"80\"], QUOTATION_COMMENT=[\"提货1\", \"提货2\", \"提货3\"], MATERIAL_CODE=[\"1001234\", \"1001234\", \"9001234\"], PURCHASE_PRICE=[\"100020\", \"100020\", \"200020\"], MISSION=[\"105\", \"105\", \"80\"],  CATEGORY_CODE=[\"123\", \"123\", \"123\"], CATEGORY_NAME=[\"测试Category\", \"测试Category\", \"测试Category\"], PUR_START_DATE=[\"20130501100000\", \"20130501100000\", \"20130501100000\"], PUR_END_DATE=[\"20131001100000\", \"20131001100000\", \"20131001100000\"], TRADE_CITY_CODE=[\"INTF\"], TRADE_DEPART_ID=[\"00309\"], TRADE_STAFF_ID=[\"IBOSS000\"], TRADE_EPARCHY_CODE=[\"INTF\"], IN_MODE_CODE=[\"6\"]}";
	        Iterable<String> split = Splitter.on(CharMatcher.inRange('[', ']')).trimResults(CharMatcher.anyOf("{}\",= ")).split(strXml);
	        
	        // Map<String, String> headMap = new HashMap<String, String>();
	        Multimap<String, String> detailMap = ArrayListMultimap.create(); // 使用MULTIMAP相当于Map<String, List<String>>
	        
	        boolean isKey = true;
	        String key = "";
	        for (Iterator<String> iterator = split.iterator(); iterator.hasNext();)
	        {
	            String splitStr = iterator.next();
	            
	            if (isKey)
	            {
	                key = convertKey(splitStr);
	                if (isHead(key)) // 如果key是head直接放入headMap,value不再拆分
	                {
	                    headMap.put(key, iterator.next());
	                    isKey = false;
	                }
	            }
	            else
	            {
	                Iterable<String> values = Splitter.on(',').trimResults(CharMatcher.anyOf("\" ")).split(splitStr);
	                for (String value : values)
	                {
	                    detailMap.put(key, value);
	                }
	            }
	            isKey = !isKey;
	        }
	        
	      
	        // 转成List
	        // List<Map<String, String>> detailList = new ArrayList<Map<String, String>>();
	        for (String detailKey : detailMap.keySet())
	        {
	            String[] values = new String[detailMap.get(detailKey).size()];
	            detailMap.get(detailKey).toArray(values);
	            for (int i = 0; i < values.length; i++)
	            {
	                String value = values[i];
	                if (detailList.size() < values.length)
	                {
	                    detailList.add(new HashMap<String, String>());
	                }
	                detailList.get(i).put(detailKey, value);
	            }
	        }
	        
	        // mapString = JSON.toJSONString(headMap);
	        // detailString = JSON.toJSONString(detailList);
	        // System.out.println(mapString);
	        // System.out.println(detailString);
	        
	        // Map<String, String> map = JSON.parseObject(JSON.toJSONString(headMap), Map.class);
	        // System.out.println("HEADMAP:" + map);
	        // List<Map> list = JSON.parseArray(JSON.toJSONString(detailList), Map.class);
	        // System.out.println("DETAILLIST:" + list);
	        
	    }

	    /**
	     * 生成返回报文
	     * 
	     * @param paramMap
	     * @return String
	    */
	    public static String createReturnXML(Map<String, String> paramMap)
	    {
	    	DocumentFactory df = DocumentFactory.getInstance();
	        Document doc = df.createDocument();
	        doc.setXMLEncoding("GBK");
	        
	        // 根节点
	        Element root = doc.addElement("MESSAGE");
	        
	        if(null != paramMap)
	        {
	            Set<Entry<String,String>> entries = paramMap.entrySet();
	            for(Entry<String,String> entry : entries)
	            {
	                root.addElement(entry.getKey()).addText(entry.getValue());
	            }
	        }
	        return doc.toString();
	    }
	    
	    /**
	     * 解析xml到List
	     * 
	     * @param documentXml
	     * @param nodeString
	     * @throws DocumentException
	     * @return List<Map<String,String>>
	    */
	    @SuppressWarnings("unchecked")
		public static List<Map<String, String>> getListByElement(String documentXml, String nodeString) throws DocumentException
	    {
	    	List<Map<String, String>> listReturn = new ArrayList<Map<String, String>>();
	    	try
	    	{
	    		Document d = DocumentHelper.parseText(documentXml);
	    		List<Node> list = d.selectNodes(nodeString);
	    		for (Node node : list)
	    		{
	    			Element e = (Element)node;
	    			Iterator<?> it = e.elementIterator();
	    			Map<String, String> map = new HashMap<String, String>();
	    			while (it.hasNext())
	    			{
	    				Element ee = (Element)it.next();
	    				map.put(ee.getQualifiedName(), ee.getText());
	    			}
	    			listReturn.add(map);
	    		}
	    	}catch (DocumentException e){
	    		throw new DocumentException("77999999" + "解析xml成List错误！");
	    	}
	    	return listReturn;
	    }
	    
	    /**
	     * 解析xml到Map
	     * 
	     * @param documentXml
	     * @param nodeString
	     * @throws DocumentException
	     * @return Map<String,String>
	    */
	    public static Map<String, String> getMapByElement(String documentXml, String nodeString) throws DocumentException
	    {
	    	Map<String, String> map = new HashMap<String, String>();
	    	try
	    	{
	    		Document d = DocumentHelper.parseText(documentXml);
	    		Node node = d.selectSingleNode(nodeString);
	    		Element eleBody = (Element)node;
	    		Iterator<?> itChild = eleBody.elementIterator();
	    		Element eChild = null;
	    		while (itChild.hasNext())
	    		{
	    			eChild = (Element)itChild.next();
	    			Iterator<?> eChildIterator = eChild.elementIterator();
	    			if (!eChildIterator.hasNext())
	    			{
	    				map.put(eChild.getName(), eChild.getText());
	    			}
	    		}
	    	}catch (DocumentException e){
	    		throw new DocumentException("77999999" + "解析xml成Map错误！");
	    	}
	    	return map;
	    }
 
	    /**
	     * 将XML对象转换为一定编码格式的字符串
	     * 
	     * @param document
	     * @param charset
	     * @return String
	    */
	    public static String xmlToString(Document document, String charset) {
	    	String _charset = StringUtils.defaultIfBlank(charset, "utf-8");
	    	
	    	StringWriter writer = new StringWriter();
	    	OutputFormat format = OutputFormat.createPrettyPrint();
	    	format.setNewLineAfterDeclaration(false);
	    	format.setEncoding(_charset);
	    	XMLWriter xmlwriter = new XMLWriter(writer, format);
	    	try {
	    		xmlwriter.write(document);
	    	} catch (IOException e) {
	    		logger.error("", e);
	    		return null;
	    	}

	    	return writer.toString();
	    }

	    public static Document stringToXML(String str) {
	    	try {
	    		return DocumentHelper.parseText(str.trim());
	    	} catch (Exception e) {
	    		logger.error("", e);
	    		return null;
	    	}
	    }
	    
	    /**
	     * 将节点中的属性和子元素的名字转换成大写<br>
	     * 转换后只能保留 Element和Attribute的name和value，还有Text<br>
	     * 方法：递归复制每个节点的属性
	     * 
	     * @param doc
	     * @return Document
	    */
	    public static Document nameToUpperCase(Document doc) {
	    	Document upperDoc = DocumentHelper.createDocument();
	    	Element root = nameToUpperCase(doc.getRootElement());
	    	if (root != null) {
	    		upperDoc.add(root);
	    	}
	    	return upperDoc;
	    }
	    
	    /**
	     * 将节点中的属性和子元素的名字转换成大写<br>
	     * 转换后只能保留 Element和Attribute的name和value，还有Text<br>
	     * 方法：递归复制每个节点的属性
	     * 
	     * @param element
	     * @return Element
	    */
	    private static Element nameToUpperCase(Element element) {
	    	if (element == null) {
	    		return null;
	    	}
	    	
	    	Element ret = DocumentHelper.createElement(StringUtils.upperCase(element.getName()));
	    	
	    	// Text
	    	ret.setText(element.getText());
	    	
	    	// Attribute
	    	Attribute att = null;
	    	List<?> atts = element.attributes();
	    	for (Object object : atts) {
	    		att = (Attribute) object;
	    		ret.addAttribute(StringUtils.upperCase(att.getName()), att.getValue());
	    	}
	    	
	    	// Element
	    	List<?> es = element.elements();
	    	for (Object object : es) {
	    		Element e = nameToUpperCase((Element) object);
	    		if (e != null) {
	    			ret.add(e);
	    		}
	    	}
	    	return ret;
	    }

	    public static Map<String, String> treeWalk(Document doc) {
	    	Map<String, String> map = new HashMap<String, String>();
	    	treeWalk(doc.getRootElement(), map);
	    	return map;
	    }
	    
	    private static void treeWalk(Element element, Map<String, String> map) {
	    	for (int i = 0, size = element.nodeCount(); i < size; i++) {
	    		Node node = element.node(i);
	    		
	    		// 如果此节点是一个NODE
	    		if (node instanceof Element) {
	    			Element tmp = (Element) node;
	    			if (tmp.isTextOnly()) {
	    				map.put(tmp.getName(), tmp.getText());
	    				
	    				Attribute tmpPro = null;
	    				
	    				for (Iterator<?> j = tmp.attributeIterator(); j.hasNext();) {
	    					tmpPro = (Attribute) j.next();
	    					
	    					map.put(tmp.getName() + "." + tmpPro.getName(), tmp.getText() + "." + tmpPro.getText());
	    				}
	    			}

	    			treeWalk((Element) node, map);
	    		}
	    	}
	    }

	    /**
	     * 查找某个节点下子元素的Text值，不区分子元素name的大小写<br>
	     * <B>如果子元素name重名(大小写不一样)，<font color="RED">取值不确定</font></B><br>
	     * 方法：循环查找父节点下所有元素，根据name匹配
	     * 
	     * @param parent
	     * @param name
	     * @return String
	    */
	    public static String getElementValueIgnoreCase(Element parent, String name) {
	    	if (parent == null) {
	    		return null;
	    	}
	    	List<?> elements = parent.elements();
	    	Element e;
	    	for (Object object : elements) {
	    		e = (Element) object;
	    		if (StringUtils.equalsIgnoreCase(e.getName(), name)) {
	    			return e.getText();
	    		}
	    	}
	    	return null;
	    }

	    public static String getElementValueIgnoreCase(Element parent, String name, String defaultValue) {
	    	return StringUtils.defaultIfBlank(getElementValueIgnoreCase(parent, name), defaultValue);
	    }
	    
	    /**
	     * 查找子元素，不区分大小写，循环查找
	     * 
	     * @param parent
	     * @param name
	     * @return Element
	    */
	    public static Element elementIgnoreCase(Element parent, String name) {
	    	if (parent == null) {
	    		return null;
	    	}
	    	List<?> elements = parent.elements();
	    	Element e;
	    	for (Object object : elements) {
	    		e = (Element) object;
	    		if (StringUtils.equalsIgnoreCase(e.getName(), name)) {
	    			return e;
	    		}
	    	}
	    	return null;
	    }
	    
	    public static String getElementValue(Element parent, String name) {
	    	if (parent == null) {
	    		return null;
	    	}
	    	Element element = parent.element(name);
	    	if (element == null) {
	    		return null;
	    	} else {
	    		return element.getText();
	    	}
	    }

	    public static String getElementValue(Element parent, String name, String defaultValue) {
	    	return StringUtils.defaultIfBlank(getElementValue(parent, name),defaultValue);
	    }
}