package de.entropia.logistiktracking.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

public class Send401SessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
		HttpServletResponse resp = event.getResponse();
		resp.sendError(401, "Session expired");
		resp.getWriter().flush();
	}
}
