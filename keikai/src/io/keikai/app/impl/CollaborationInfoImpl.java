package io.keikai.app.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import io.keikai.api.model.Book;
import io.keikai.app.CollaborationInfo;
import org.zkoss.lang.Library;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author JerryChen
 *
 */

public class CollaborationInfoImpl implements CollaborationInfo, Serializable {
	private static final long serialVersionUID = 6857473475844968874L;
	
	private static final Logger logger = Logger.getLogger(CollaborationInfoImpl.class.getName());
	protected static CollaborationInfo collaborationInfo;
	
	class UserKey {
		private String sessionId;
		private String username;
		private Set<String> desktopIds = new HashSet<String>();
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public Set<String> getDesktopIds() {
			return desktopIds;
		}
		public void setDesktopIds(Set<String> desktopIds) {
			this.desktopIds = desktopIds;
		}
	}
	
	public static CollaborationInfo getInstance() {
		if(collaborationInfo == null) {
			String clz = Library.getProperty("io.keikai.app.CollaborationInfo.class");
			if(clz != null && Boolean.valueOf(Library.getProperty("zssapp.collaboration.disabled")) != Boolean.TRUE){
				try {
					collaborationInfo = (CollaborationInfo) Class.forName(clz).newInstance();
				} catch(Exception e) {
					collaborationInfo =  new CollaborationInfoImpl();
					logger.log(Level.SEVERE, e.getMessage(), e);
				}			
			} else
				collaborationInfo =  new CollaborationInfoImpl();
		}
		
		return collaborationInfo;
	}

	@Override
	public void setRelationship(String username, Book book) {}

	@Override
	public void removeRelationship(String username) {}
	
	@Override
	public boolean isUsernameExist(String username) {
		return false;
	}
	
	@Override
	public boolean addUsername(String username, String oldUsername) {
		return false;
	}
	
	@Override
	public void removeUsername(String username) {}
	
	@Override
	public Set<String> getUsedUsernames(String bookName) {
		return new HashSet<String>(0);
	}
	
	@Override
	public String getUsername(String originName) {
		return "";
	}

	@Override
	public void addEvent(CollaborationEventListener listener) {}
}
