package com.qcloud.live.handles;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Eric.Zhang on 2017/1/10.
 */
// @Component
public class LeCloudHandle {
	String apiurl = SystemConfig.getProperty("sfl.live.lecloud.url");
	String coopApiurl = SystemConfig.getProperty("sfl.live.lecloud.coopapiurl");
	String apiuserid = SystemConfig.getProperty("sfl.live.lecloud.userid");
	String apisecretkey = SystemConfig.getProperty("sfl.live.lecloud.secretkey");
	String codeRateTypes = SystemConfig.getProperty("sfl.live.lecloud.codeRateTypes");
	String apiUUID = SystemConfig.getProperty("sfl.live.lecloud.uuid");

	// region 通用方法

	// MD5加密类
	public static class MD5 {
		private static char md5Chars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
				'f' };

		public static String md5(String str) throws Exception {
			MessageDigest md5 = getMD5Instance();
			md5.update(str.getBytes("UTF-8"));
			byte[] digest = md5.digest();
			char[] chars = toHexChars(digest);
			return new String(chars);
		}

		private static MessageDigest getMD5Instance() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ignored) {
				throw new RuntimeException(ignored);
			}
		}

		private static char[] toHexChars(byte[] digest) {
			char[] chars = new char[digest.length * 2];
			int i = 0;
			for (byte b : digest) {
				char c0 = md5Chars[(b & 0xf0) >> 4];
				chars[i++] = c0;
				char c1 = md5Chars[b & 0xf];
				chars[i++] = c1;
			}
			return chars;
		}
	}

	/**
	 * 构造云视频Sign
	 * 
	 * @param params
	 *            业务参数
	 * @return string
	 * @throws Exception
	 */
	String generateSign(Map<String, String> params) {
		String result = "";

		String keyStr = "";
		try {
			Object[] array = params.keySet().toArray();
			Arrays.sort(array);
			for (int i = 0; i < array.length; i++) {
				String key = array[i].toString();
				keyStr += key + params.get(key);
			}
			keyStr += apisecretkey;
			result = MD5.md5(keyStr);
		} catch (Exception ex) {
			LogHelper.logger().info("生成乐视请求sign失败" + keyStr);
		}

		return result;
	}

	/**
	 * 获取乐视的userid
	 * 
	 * @return
	 */
	public String getApiUserID() {
		return apiuserid;
	}

	/**
	 * 获取乐视的uuid
	 * 
	 * @return
	 */
	public String getApiUUID() {
		return apiUUID;
	}

	/**
	 * 生成乐视接口需要的Sign
	 * 
	 * @param method
	 *            调用的api接口名称
	 * @param ver
	 *            接口版本 // * @param userid 用户id // * @param timestamp 时间戳
	 * @param params
	 *            接口参数
	 * @return
	 */
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
			params.put("userid", getApiUserID());
		}
		if (!params.containsKey("timestamp")) {
			params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		return generateSign(params);
	}

	/**
	 * get请求接口
	 * 
	 * @param method
	 * @param ver
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public HttpResponseModel doGet(String method, String ver, Map<String, String> params) throws Exception {
		params.put("sign", getSign(method, ver, params));
		Map<String, String> header = new HashMap<>();
		header.put("Accept-Charset", "utf-8");
		HttpResponseModel result = new HttpUtils().doGet(apiurl, params, header);

		return result;
	}

	/**
	 * post请求接口
	 * 
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

	// public OperationResultObject<String> getResult(HttpResponseModel model){
	//
	// OperationResultObject<String> result=new OperationResultObject<String>();
	// if(model.getCode()==200){
	// result.set_objectResult(model.getContent());
	// }
	// return result;
	// }
	// endregion

	// region 直播

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
		params.put("coverImgUrl", "");// 活动封面地址，如果为空，则系统会默认一张图片
		params.put("description", description);// 活动描述（1024个字符以内）
		params.put("codeRateTypes", codeRateTypes);// 流的码率类型，逗号分隔。由大到小排列。取值范围：13
													// 流畅；16 高清；19 超清； 25
													// 1080P；99 原画。
													// 默认按最高码率播放，如果有原画则按原画播放
		params.put("activityCategory", "999");// 活动分类 ：999代表其他
		params.put("playMode", "0");// 播放模式，0：实时直播 1：流畅直播。
		params.put("liveNum", "1");// 机位数量，范围为：1,2,3,4. 默认为1
		params.put("needRecord", "1");// 是否支持全程录制 0：否 1：是
		params.put("needTimeShift", "0");// 是否支持时移 0：否 1：是。默认为0
		params.put("needFullView", "0");// 是否全景观看 0：否 1：是。默认为0
		return params;
	}

	/**
	 * 获取修改直播的接口参数
	 * 
	 * @param activityId
	 *            直播ID
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
	Map<String, String> getParamsForModifyLive(String activityId, String activityName, Date startTime, Date endTime,
			String coverImgUrl, String description) {
		Map<String, String> params = getParamsForCreateLive(activityName, startTime, endTime, coverImgUrl, description);
		if (params != null) {
			params.put("activityId", activityId);
		}
		return params;
	}

	/**
	 * 获取结束直播的接口参数
	 * 
	 * @param activityId
	 *            直播ID
	 * @return
	 */
	Map<String, String> getParamsForStopLive(String activityId) {
		Map<String, String> params = new HashMap<>();
		params.put("activityId", activityId);
		return params;
	}

	/**
	 * 创建直播
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
	 * @return 直播ID
	 */
	public String createLive(String activityName, Date startTime, Date endTime, String coverImgUrl,
			String description) {
		try {
			Map<String, String> params = getParamsForCreateLive(activityName, startTime, endTime, coverImgUrl,
					activityName);
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
		return "";
	}

	/**
	 * 获取直播活动推流Token
	 * 
	 * @param activityId
	 * @return
	 * @throws Exception
	 */
	public String getPushToken(String activityId) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("activityId", activityId);

			HttpResponseModel model = doGet("lecloud.cloudlive.activity.getPushToken", "4.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				String result = rootnode.path("token").textValue();
				return result;
			} else {
				LogHelper.logger()
						.info("获取直播活动推流Token失败！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return "";
			}
		} catch (Exception ex) {
			LogHelper.logger().info("获取直播活动推流Token异常！直播ID:" + activityId + "。异常信息：" + ex.getMessage());
		}
		return "";
	}

	/**
	 * 直播活动流信息查询接口
	 * 
	 * @param activityId
	 *            直播ID
	 * @return
	 * @throws Exception
	 */
	public String getLiveStreaminfo(String activityId) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("activityId", activityId);
			HttpResponseModel model = doGet("lecloud.cloudlive.vrs.activity.streaminfo.search", "4.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				JsonNode jns = rootnode.path("lives");
				String result = "";
				if (jns != null && jns.size() > 0) {
					@SuppressWarnings("unused")
					JsonNode jn = jns.get(0).path("liveId");
					result = jns.textValue();
					if (result == null) {
						result = "";
					}
				}
				return result;
			} else {
				LogHelper.logger()
						.info("获取直播活动流信息查询接口错误！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return "";
			}
		} catch (Exception ex) {
			LogHelper.logger().info("获取直播活动流信息查询接口异常！直播ID:" + activityId + "。异常信息：" + ex.getMessage());
		}
		return "";
	}

	/**
	 * 直播活动流信息查询接口
	 * 
	 * @param activityId
	 * @return 接口需要的两个值
	 */
	public String[] getLiveStreamInfo(String activityId) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("activityId", activityId);
			HttpResponseModel model = doGet("lecloud.cloudlive.activity.getPushUrl", "4.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				JsonNode jns = rootnode.path("lives");
				String[] strs = new String[2];
				String result = "";
				if (jns != null && jns.size() > 0) {
					JsonNode jn = jns.get(0).path("pushUrl");
					result = jn.textValue();
					/**
					 * 将结果进行拆分
					 * 例如：rtmp://10.154.28.135/live/20150826309000116?sign=63a0b59238ba4db54d82ad2dd9d7e70f&tm=20150826143027
					 * 拆分结果 part1:rtmp://10.154.28.135/live
					 * part2:20150826309000116?sign=63a0b59238ba4db54d82ad2dd9d7e70f&tm=20150826143027
					 */
					if (result == null) {
						return null;
					}
					strs = result.split("live/");
					strs[0] = strs[0] + "live";
				}
				return strs;
			} else {
				LogHelper.logger()
						.info("获取直播活动流信息查询接口错误！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return null;
			}
		} catch (Exception ex) {
			LogHelper.logger().info("获取直播活动流信息查询接口异常！直播ID:" + activityId + "。异常信息：" + ex.getMessage());
		}
		return null;
	}

	/**
	 * 获取录制视频信息接口
	 * 
	 * @param activityId
	 *            直播ID
	 * @return
	 * @throws Exception
	 */
	public String getPlayInfo(String activityId) {

		String result = "";
		try {
			Map<String, String> params = new HashMap<>();
			params.put("activityId", activityId);

			HttpResponseModel model = doGet("lecloud.cloudlive.activity.getPlayInfo", "4.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				JsonNode jns = rootnode.path("machineInfo");
				if (jns != null && jns.size() > 0) {
					JsonNode jn = jns.get(0).path("videoUnique");
					result = jn.textValue();
					if (result == null) {
						result = "";
					}
				}
			} else {
				LogHelper.logger()
						.info("获取录制视频信息接口错误！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return "";
			}

		} catch (Exception ex) {
			LogHelper.logger().info("获取录制视频信息接口异常！直播ID:" + activityId + "。异常信息：" + ex.getMessage());
		}

		return result;
	}

	/**
	 * 获取录制视频信息的id接口
	 * 
	 * @param activityId
	 *            直播ID
	 * @return
	 * @throws Exception
	 */
	public String getPlayInfoID(String activityId) {

		String result = "";
		try {
			Map<String, String> params = new HashMap<>();
			params.put("activityId", activityId);

			HttpResponseModel model = doGet("lecloud.cloudlive.activity.getPlayInfo", "4.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				JsonNode jns = rootnode.path("machineInfo");
				if (jns != null && jns.size() > 0) {
					JsonNode jn = jns.get(0).path("videoId");
					result = jn.textValue();
					if (result == null) {
						result = "";
					}
				}
			} else {
				LogHelper.logger()
						.info("获取录制视频信息的id接口错误！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return "";
			}

		} catch (Exception ex) {
			LogHelper.logger().info("获取录制视频信息的id接口异常！直播ID:" + activityId + "。异常信息：" + ex.getMessage());
		}

		return result;
	}
	// endregion

	// region 录播

	String getCoopSign(String method, String ver, Map<String, String> params) {
		if (params == null || method.equals("") || ver.equals("")) {
			return "";
		}
		if (!params.containsKey("api")) {
			params.put("api", method);
		}
		if (!params.containsKey("ver")) {
			params.put("ver", ver);
		}
		if (!params.containsKey("user_unique")) {
			params.put("user_unique", getApiUUID());
		}
		if (!params.containsKey("timestamp")) {
			params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		if (!params.containsKey("format")) {
			params.put("format", "json");
		}
		return generateSign(params);
	}

	/**
	 * post请求接口
	 * 
	 * @param method
	 * @param ver
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public HttpResponseModel doCoopPost(String method, String ver, Map<String, String> params) throws Exception {
		params.put("sign", getCoopSign(method, ver, params));
		Map<String, String> header = new HashMap<>();
		header.put("Accept-Charset", "utf-8");
		HttpResponseModel result = new HttpUtils().doPost(coopApiurl, params, header);
		return result;
	}

	/**
	 * 获取点播视频时长接口
	 * 
	 * @param video_id
	 *            视频ID
	 * @return
	 * @throws Exception
	 */
	public Integer getCoopInfoVideoLength(String video_id) {

		Integer result = 0;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("video_id", video_id);

			HttpResponseModel model = doCoopPost("video.get", "2.0", params);
			ObjectMapper mapper = new ObjectMapper();
			if (model.getCode() == 200) {
				JsonNode rootnode = mapper.readTree(model.getContent());
				JsonNode jns1 = rootnode.path("data");
				if (jns1 != null && jns1.size() > 0) {
					JsonNode jns2 = jns1.path("video_duration");
					String _video_duration = jns2.textValue();
					if (_video_duration != null) {
						result = Integer.parseInt(_video_duration);
					}
				}
			} else {
				LogHelper.logger()
						.info("获取点播视频时长接口错误！请求状态码：" + model.getCode() + "参数信息：" + mapper.writeValueAsString(params));
				return 0;
			}

		} catch (Exception ex) {
			LogHelper.logger().info("获取点播视频时长接口异常！直播ID:" + video_id + "。异常信息：" + ex.getMessage());

		}

		return result;
	}
	// endregion
}
