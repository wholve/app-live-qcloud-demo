package com.qcloud.live.handles;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hwang
 * @E-mail wanghao@dingjintz.com
 * @version 1.0
 * @创建时间 2017年5月2日 下午5:32:23 LeCloudApi.java 说明 ☞
 */
@Component
@RequestMapping("/")
public class LeCloudApi {

	@RequestMapping("index")
	public String homePage(String activityId, Model model) {
		if (activityId != null && !"".equals(activityId)) {
			model.addAttribute("activityId", activityId);
		}
		return "index";
	}

	@RequestMapping("create")
	public String createLive(String activityName, String activityCategory, Date startTime, Date endTime,
			String coverImgUrl, String description, int playMode, int liveNum, String codeRateTypes, int needRecord,
			int needTimeShift, int needFullView, Model model) {
		LeClouldHandle clouldHandle = new LeClouldHandle();
		String activityId = clouldHandle.createLive(activityName, activityCategory, startTime, endTime, coverImgUrl,
				description, playMode, liveNum, codeRateTypes, needRecord, needTimeShift, needFullView);
		if (activityId != null && !"".equals(activityId)) {
			model.addAttribute("activityId", activityId);
		}
		return "index";
	}
}
