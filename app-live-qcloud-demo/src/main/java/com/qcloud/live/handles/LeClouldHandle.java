package com.qcloud.live.handles;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hwang
 * @E-mail wanghao@dingjintz.com
 * @version 1.0
 * @创建时间 2017年5月2日 下午4:25:05 LeClouldHandle.java 说明 ☞ 乐视云直播接口实现
 */
public class LeClouldHandle {

	String apiurl = SystemConfig.getProperty("sfl.live.lecloud.url");
	String coopApiurl = SystemConfig.getProperty("sfl.live.lecloud.coopapiurl");
	String apiuserid = SystemConfig.getProperty("sfl.live.lecloud.userid");
	String apisecretkey = SystemConfig.getProperty("sfl.live.lecloud.secretkey");
	String codeRateTypes = SystemConfig.getProperty("sfl.live.lecloud.codeRateTypes");
	String apiUUID = SystemConfig.getProperty("sfl.live.lecloud.uuid");

	/**
	 * 构造云视频的Sign
	 * 
	 * @author hwang
	 * @创建时间 2017年5月2日 下午4:36:39
	 * @param params
	 * @return
	 */
	public String generateSign(Map<String, String> params) {
		String result = "";
		String keyStr = "";
		try {
			// 步骤一：按key升序
			Object[] array = params.keySet().toArray();
			Arrays.sort(array);
			// 步骤二：按照步骤一的顺序，拼接所有key、value
			for (int i = 0; i < array.length; i++) {
				String key = array[i].toString();
				keyStr += key + params.get(key);
			}
			// 步骤三：将用户密钥(secretkey)的值，拼接到步骤二后边
			keyStr += apisecretkey;
			// 步骤四：计算步骤三中字符串的md5值，即为sign
			result = MD5.getMd5(keyStr);
		} catch (Exception ex) {
			System.out.println("获取直播sign失败..." + keyStr);
		}
		return result;
	}

	public String getSign(String method, String ver, Map<String, String> params) {
		if (params == null || method.equals("") || ver.equals("")) {
			return "";
		}
		if (!params.containsKey("method")) {
			params.put("method", method);
		}
		if (!params.containsKey("ver")) {
			params.put("ver", ver);
		}
		if (!params.containsKey("userid")) {
			params.put("userid", apiUUID);
		}
		if (!params.containsKey("timestamp")) {
			params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		return generateSign(params);
	}

	/**
	 * 获取创建直播的接口参数
	 * 
	 * @param activityName
	 *            直播名称(200个字符以内)
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param coverImgUrl
	 *            封面URl，如果为空，则系统会默认一张图片
	 * @param description
	 *            描述（1024个字符以内）
	 * @return
	 */
	Map<String, String> getParamsForCreateLive(String activityName, Date startTime, Date endTime, String coverImgUrl,
			String description) {
		Map<String, String> params = new HashMap<>();
		params.put("activityName", activityName);// 直播活动名称(200个字符以内)
		params.put("startTime", String.valueOf(startTime.getTime()));// 开始时间，从1970开始的毫秒数
		params.put("endTime", String.valueOf(endTime.getTime()));// 结束时间，从1970开始的毫秒数
		params.put("coverImgUrl", coverImgUrl);// 活动封面地址，如果为空，则系统会默认一张图片
		params.put("description", description);// 活动描述（1024个字符以内）
		params.put("codeRateTypes", codeRateTypes);// 流的码率类型，逗号分隔。由大到小排列。取值范围：13
													// 流畅；16 高清；19 超清； 25
													// 1080P；99 原画。
													// 默认按最高码率播放，如果有原画则按原画播放
		params.put("activityCategory", "999");// 活动分类 ：999代表其他
		params.put("playMode", "0");// 播放模式，0：实时直播 1：流畅直播。
		params.put("liveNum", "1");// 机位数量，范围为：1,2,3,4. 默认为1
		params.put("needRecord", "1");// 是否支持全程录制 0：否 1：是
		return params;
	}

	/**
	 * 
	 * @author hwang
	 * @创建时间 2017年5月2日 下午4:47:25
	 * @param activityName
	 *            直播活动名称(200个字符以内)
	 * @param activityCategory
	 *            活动分类，参见《活动分类编码表》
	 * @param startTime
	 *            开始时间，从1970开始的毫秒数
	 * @param endTime
	 *            结束时间，从1970开始的毫秒数
	 * @param coverImgUrl
	 *            活动封面地址，如果为空，则系统会默认一张图片
	 * @param description
	 *            活动描述（1024个字符以内）
	 * @param playMode
	 *            播放模式，0：实时直播 1：流畅直播。
	 * @param liveNum
	 *            机位数量，范围为：1,2,3,4. 默认为1
	 * @param codeRateTypes
	 *            流的码率类型，逗号分隔。由大到小排列。取值范围：13 流畅；16 高清；19 超清； 25 1080P；99 原画。
	 *            默认按最高码率播放，如果有原画则按原画播放
	 * @param needRecord
	 *            是否支持全程录制 0：否 1：是。默认为0
	 * @param needTimeShift
	 *            是否支持时移 0：否 1：是。默认为0
	 * @param needFullView
	 *            是否全景观看 0：否 1：是。默认为0
	 * @return activityId 活动ID
	 */
	public String createLive(String activityName, Date startTime, Date endTime,
			String coverImgUrl, String description) {
		String activityId = "";
		try {
			Map<String, String> params = getParamsForCreateLive(activityName, startTime, endTime, coverImgUrl,
					description);
			HttpResponseModel model = doPost("lecloud.cloudlive.activity.create", "4.1", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				String result = rootnode.path("activityId").textValue();
				return result;
			} else {
				LogHelper.logger()
						.info("创建直播时失败！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return "";
			}
		} catch (Exception ex) {
			LogHelper.logger().info("创建直播时异常！参数信息：" + activityName + ">>>>" + startTime.toString() + ">>>>>"
					+ endTime.toString() + ">>>>" + coverImgUrl + ">>>>" + description);
		}
		return activityId;
	}
	
	/**
	 * 
	* @author hwang 
	* @创建时间 2017年5月2日 下午5:18:23
	* @param method
	* @param ver
	* @param params
	* @return
	* @throws Exception
	 */
	@SuppressWarnings("static-access")
	public HttpResponseModel doPost(String method, String ver, Map<String, String> params) throws Exception {
		params.put("sign", getSign(method, ver, params));
		Map<String, String> header = new HashMap<>();
		header.put("Accept-Charset", "utf-8");
		HttpResponseModel result = new HttpUtils().doPost(apiurl, params, header);
		return result;
	}
}
