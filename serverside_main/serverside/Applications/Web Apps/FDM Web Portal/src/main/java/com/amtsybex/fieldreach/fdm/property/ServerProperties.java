package com.amtsybex.fieldreach.fdm.property;

public class ServerProperties {

	private Servlet servlet;

	public static class Servlet {

		private Session session;

		public static class Session {
			private Integer timeout;

			public Integer getTimeout() {
				return timeout;
			}

			public void setTimeout(Integer timeout) {
				this.timeout = timeout;
			}

		}

		public Session getSession() {
			return session;
		}

		public void setSession(Session session) {
			this.session = session;
		}


	}
	public Servlet getServlet() {
		return servlet;
	}

	public void setServlet(Servlet servlet) {
		this.servlet = servlet;
	}

}
