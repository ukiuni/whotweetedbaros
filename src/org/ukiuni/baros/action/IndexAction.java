package org.ukiuni.baros.action;

import java.util.List;

import net.arnx.jsonic.JSON;

import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;
import org.ukiuni.baros.TwitterListener;
import org.ukiuni.baros.entity.Status;


public class IndexAction {
	public String startIndex;

	@Execute(validator = false)
	public String index() {
		return "index.jsp";
	}

	@Execute(validator = false)
	public String listen() {
		List<Status> statusList = TwitterListener.listen(Long.valueOf(startIndex));
		ResponseUtil.write(JSON.encode(statusList), "application/json");
		return null;
	}
}
