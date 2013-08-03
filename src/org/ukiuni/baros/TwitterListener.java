package org.ukiuni.baros;

import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@SuppressWarnings("serial")
public class TwitterListener extends HttpServlet {
	private static final int MAX_LOAD_FROM_DB_COUNT = 100;
	private static TwitterStream twitterStream;
	private static final String SEARCH_WORD = ResourceBundle.getBundle("twitter4j").getString("searchWord");

	public static List<org.ukiuni.baros.entity.Status> listen(long index) {

		List<org.ukiuni.baros.entity.Status> statuses = SingletonS2Container.getComponent(JdbcManager.class).from(org.ukiuni.baros.entity.Status.class).where(new SimpleWhere().gt("id", index)).orderBy("id").getResultList();

		if (statuses.isEmpty()) {
			synchronized (TwitterListener.class) {
				try {
					TwitterListener.class.wait();
					statuses = SingletonS2Container.getComponent(JdbcManager.class).from(org.ukiuni.baros.entity.Status.class).where(new SimpleWhere().gt("id", index)).orderBy("id").limit(MAX_LOAD_FROM_DB_COUNT).getResultList();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return statuses;
	}

	public void startListen() {
		synchronized (TwitterListener.class) {
			if (null == twitterStream) {
				TwitterStreamFactory factory = new TwitterStreamFactory();
				twitterStream = factory.getInstance();
				twitterStream.addListener(new MyStatusListener());
				FilterQuery filterQuery = new FilterQuery();
				filterQuery.track(new String[] { SEARCH_WORD });
				twitterStream.filter(filterQuery);
				twitterStream.sample();
			}
		}
	}

	static class MyStatusListener implements StatusListener {

		JdbcManager jdbcManager;

		public MyStatusListener() {
			SingletonS2ContainerFactory.init();
			jdbcManager = SingletonS2Container.getComponent(JdbcManager.class);
		}

		public void onStatus(Status status) {
			SingletonS2ContainerFactory.init();
			if (status.getText().contains(SEARCH_WORD)) {
				org.ukiuni.baros.entity.Status myStatus = new org.ukiuni.baros.entity.Status();
				myStatus.setImageUrl(status.getUser().getBiggerProfileImageURL());
				myStatus.setText(status.getText());
				myStatus.setScreenName(status.getUser().getScreenName());
				myStatus.setCreatedAt(status.getCreatedAt());
				jdbcManager.insert(myStatus).execute();
			}
			synchronized (TwitterListener.class) {
				TwitterListener.class.notifyAll();
			}
		}

		public void onDeletionNotice(StatusDeletionNotice sdn) {
		}

		public void onTrackLimitationNotice(int i) {
		}

		public void onScrubGeo(long lat, long lng) {
		}

		public void onException(Exception excptn) {
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		startListen();
	}

	@Override
	public void destroy() {
		if (null != twitterStream) {
			twitterStream.shutdown();
			twitterStream.cleanUp();
		}
	}
}
