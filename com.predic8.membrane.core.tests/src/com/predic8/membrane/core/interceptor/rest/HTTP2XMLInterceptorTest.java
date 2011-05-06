package com.predic8.membrane.core.interceptor.rest;

import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.InputSource;

import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.rules.ForwardingRule;
import com.predic8.membrane.core.rules.ForwardingRuleKey;
import com.predic8.membrane.core.rules.Rule;
import com.predic8.membrane.core.util.ByteUtil;
import com.predic8.membrane.core.util.MessageUtil;
import com.predic8.membrane.core.ws.relocator.Relocator;


public class HTTP2XMLInterceptorTest extends TestCase {
	
	private Exchange exc;
	
	private HTTP2XMLInterceptor interceptor = new HTTP2XMLInterceptor(); 
	
	XPath xpath = XPathFactory.newInstance().newXPath();
	
	@Override
	protected void setUp() throws Exception {
		exc = new Exchange();
		exc.setRequest(MessageUtil.getGetRequest("http://localhost/axis2/services/BLZService?wsdl"));
		exc.setResponse(MessageUtil.getOKResponse(ByteUtil.getByteArrayData(this.getClass().getResourceAsStream("/blz-service.wsdl")), "text/xml"));
		exc.setOriginalHostHeader("thomas-bayer.com:80");
		
	}
	
	@Test
	public void testXml() throws Exception {
		interceptor.handleRequest(exc);
		
		assertXPath("/request/@method", "GET");
		assertXPath("/request/uri/host", "localhost");
		assertXPath("/request/uri/path/component[1]", "axis2");
		assertXPath("/request/uri/path/component[2]", "services");
		assertXPath("/request/uri/path/component[3]", "BLZService");
	}

	private void assertXPath(String xpathExpr, String expected) throws XPathExpressionException {
		assertEquals(expected, xpath.evaluate(xpathExpr, new InputSource(exc.getRequest().getBodyAsStream())));
	}
	
}
