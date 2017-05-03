package com.qcloud.live.handles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * Created by Eric.Zhang on 2017/1/11.
 */
public class HttpUtils {

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 *
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (queryString != null && !queryString.equals(""))
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (URIException e) {
			LogHelper.logger().info("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！");
		} catch (IOException e) {
			LogHelper.logger().info("执行HTTP Get请求" + url + "时，发生异常！");
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 执行一个HTTP Get请求，返回请求响应的内容
	 *
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的参数,可以为null
	 * @param headers
	 *            请求的header,可以为null
	 * @return 返回请求响应
	 */
	public static HttpResponseModel doGet(String url, Map<String, String> params, Map<String, String> headers) {
		HttpClient client = new HttpClient();
		HttpMethod getMethod = new GetMethod(url);
		String querys = "";
		HttpResponseModel result = new HttpResponseModel();
		try {
			// 设置Http Post数据
			if (params != null) {
				NameValuePair[] queryPairs = new NameValuePair[params.size()];
				int i = 0;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					queryPairs[i] = new NameValuePair(entry.getKey(), entry.getValue());
					i++;

				}
				if (queryPairs.length > 0) {
					getMethod.setQueryString(queryPairs);
				}
			}
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					getMethod.setRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			client.executeMethod(getMethod);
			result.code = getMethod.getStatusCode();

			InputStream jsonStr;
			jsonStr = getMethod.getResponseBodyAsStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = jsonStr.read()) != -1) {
				baos.write(i);
			}

			// result.content = baos.toString();
			result.content = new String(baos.toByteArray(), "utf-8");
			// if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
			// result.content = getMethod.getResponseBodyAsString();
			// }
		} catch (URIException e) {
			LogHelper.logger().info("执行HTTP Get请求时，编码查询字符串“" + querys + "”发生异常！");
		} catch (IOException e) {
			LogHelper.logger().info("执行HTTP Get请求" + url + "时，发生异常！");
		} finally {
			getMethod.releaseConnection();
		}
		return result;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的内容
	 *
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的参数,可以为null
	 * @param headers
	 *            请求的header,可以为null
	 * @return 返回请求响应
	 */
	public static HttpResponseModel doPost(String url, Map<String, String> params, Map<String, String> headers) {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		HttpResponseModel result = new HttpResponseModel();
		// for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
		//
		// }
		// 设置Http Post数据

		if (params != null) {

			NameValuePair[] dataPairs = new NameValuePair[params.size()];
			int i = 0;
			for (Entry<String, String> entry : params.entrySet()) {
				dataPairs[i] = new NameValuePair(entry.getKey(), entry.getValue());
				i++;

			}
			if (dataPairs.length > 0) {
				postMethod.setRequestBody(dataPairs);
			}

			// HttpMethodParams p = new HttpMethodParams();
			// for (Map.Entry<String, String> entry : params.entrySet()) {
			// p.setParameter(entry.getKey(), entry.getValue());
			// }
			// postMethod.setParams(p);
		}
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				postMethod.setRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		try {
			client.executeMethod(postMethod);
			result.code = postMethod.getStatusCode();
			// if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
			// result .content= postMethod.getResponseBodyAsString();
			// }
			InputStream jsonStr;
			jsonStr = postMethod.getResponseBodyAsStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = jsonStr.read()) != -1) {
				baos.write(i);
			}
			result.content = new String(baos.toByteArray(), "utf-8");
		} catch (IOException e) {
			LogHelper.logger().info("执行HTTP Post请求" + url + "时，发生异常！");
		} finally {
			postMethod.releaseConnection();
		}

		return result;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的内容
	 *
	 * @param url
	 *            请求的URL地址
	 * @param entity
	 *            请求的参数,可以为null
	 * @param headers
	 *            请求的header,可以为null
	 * @return 返回请求响应
	 */
	public static HttpResponseModel doPost(String url, RequestEntity entity, Map<String, String> headers) {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		HttpResponseModel result = new HttpResponseModel();
		// for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
		//
		// }
		if (entity != null) {
			// 设置Http Post数据

			postMethod.setRequestEntity(entity);

			// HttpMethodParams p = new HttpMethodParams();
			// for (Map.Entry<String, String> entry : params.entrySet()) {
			// p.setParameter(entry.getKey(), entry.getValue());
			// }
			// postMethod.setParams(p);
		}
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				postMethod.setRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		try {
			client.executeMethod(postMethod);
			result.code = postMethod.getStatusCode();
			// if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
			// result .content= postMethod.getResponseBodyAsString();
			// }
			InputStream jsonStr;
			jsonStr = postMethod.getResponseBodyAsStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = jsonStr.read()) != -1) {
				baos.write(i);
			}
			result.content = new String(baos.toByteArray(), "utf-8");
		} catch (IOException e) {
			LogHelper.logger().info("执行HTTP Post请求" + url + "时，发生异常！");
		} finally {
			postMethod.releaseConnection();
		}

		return result;
	}

}
