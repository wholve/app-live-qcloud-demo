package com.qcloud.live.handles;

import java.util.Calendar;
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

	public String createLive(String activityName, Model model) {
		Date startTime = new Date();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date endTime = c.getTime();
		LeClouldHandle clouldHandle = new LeClouldHandle();
		String activityId = clouldHandle.createLive(activityName, startTime, endTime, null, "测试" + c.getTime());
		if (activityId != null && !"".equals(activityId)) {
			model.addAttribute("activityId", activityId);
		}
		return "index";
	}
}
