package com.amtsybex.fieldreach.fdm.builder;

import com.amtsybex.fieldreach.backend.model.SystemUsers;

public class SystemUsersBuilder {
	
	private SystemUsersBuilder() {
		
	}
	
	public static class Builder{
		private String id = "1";
		private String userName = "FRADM";
		private Integer dateAdded = 20210828;
		private String sbGroupCode = "SBPRIMARY";
		private String fdmGroupCode = "FDMPRIMARY";
		private Integer revoked = 0;
		private Integer adminUser = 0;
		private String winLogin = "P12345678";
		private String smGroupCode = "SBPRIMARY";
		private String userClass = "BA1";
		private Integer lastModDate = 20210828;
		private String lastModTime = "135723";
		
		public Builder(String id) {
			this.id = id;
		}
		
		
		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder dateAdded(Integer dateAdded) {
			this.dateAdded = dateAdded;
			return this;
		}

		public Builder sbGroupCode(String sbGroupCode) {
			this.sbGroupCode = sbGroupCode;
			return this;
		}

		public Builder fdmGroupCode(String fdmGroupCode) {
			this.fdmGroupCode = fdmGroupCode;
			return this;
		}

		public Builder revoked(Integer revoked) {
			this.revoked = revoked;
			return this;
		}

		public Builder adminUser(Integer adminUser) {
			this.adminUser = adminUser;
			return this;
		}

		public Builder winLogin(String winLogin) {
			this.winLogin = winLogin;
			return this;
		}

		public Builder smGroupCode(String smGroupCode) {
			this.smGroupCode = smGroupCode;
			return this;
		}

		public Builder userClass(String userClass) {
			this.userClass = userClass;
			return this;
		}

		public Builder lastModDate(Integer lastModDate) {
			this.lastModDate = lastModDate;
			return this;
		}

		public Builder lastModTime(String lastModTime) {
			this.lastModTime = lastModTime;
			return this;
		}
		
		public SystemUsers build() {
			
			SystemUsers systemUsers = new SystemUsers();
			systemUsers.setId(id);
			systemUsers.setUserName(userName);
			systemUsers.setDateAdded(dateAdded);
			systemUsers.setSbGroupCode(sbGroupCode);
			systemUsers.setFdmGroupCode(fdmGroupCode);
			systemUsers.setRevoked(revoked);
			systemUsers.setAdminUser(adminUser);
			systemUsers.setWinLogin(winLogin);
			systemUsers.setSmGroupCode(smGroupCode);
			systemUsers.setUserClass(userClass);
			systemUsers.setLastModDate(lastModDate);
			systemUsers.setLastModTime(lastModTime);
			return systemUsers;
			
		}
		
		
		
	}

}
