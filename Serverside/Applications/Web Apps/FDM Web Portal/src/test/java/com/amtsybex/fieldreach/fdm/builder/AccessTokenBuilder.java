package com.amtsybex.fieldreach.fdm.builder;

import com.amtsybex.fieldreach.backend.model.AccessToken;

public class AccessTokenBuilder {
	
	
	private AccessTokenBuilder() {
		
	}
	
	public static class Builder{
		
		private String id = "UniqueRandomString";
		private String name = "FIELDSMART";
		private String linkedUserCode = "FRADM";
		private String createUser = "FRADM";
		private int createDate = 20210828;
		private int createTime = 194423;
		private String tokenNotes = "Test Token Notes";
		private int expiryDate = 20211128;
		private String checksum = "CalcCheckSumValue";
		private int revoked = 0;
		private String revokeUser = "FRADM";
		private int revokeDate = 20210828;
		private String revokeNotes = "Test Revoke Notes";

		
		public Builder(String id) {
			this.id = id;
		}


		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder linkedUserCode(String linkedUserCode) {
			this.linkedUserCode = linkedUserCode;
			return this;
		}
		
		public Builder createUser(String createUser) {
			this.createUser = createUser;
			return this;
		}
		
		public Builder createDate(Integer createDate) {
			this.createDate = createDate;
			return this;
		}
		
		public Builder createTime(Integer createTime) {
			this.createTime = createTime;
			return this;
		}
		
		public Builder tokenNotes(String tokenNotes) {
			this.tokenNotes = tokenNotes;
			return this;
		}
		
		public Builder expiryDate(Integer expiryDate) {
			this.expiryDate = expiryDate;
			return this;
		}
		
		public Builder checkSum(String checksum) {
			this.checksum = checksum;
			return this;
		}
		
		public Builder revoked(Integer revoked) {
			this.revoked = revoked;
			return this;
		}
		
		public Builder revokeUser(String revokeUser) {
			this.revokeUser = revokeUser;
			return this;
		}
		
		public Builder revokeDate(Integer revokeDate) {
			this.revokeDate = revokeDate;
			return this;
		}
		
		public Builder revokeNotes(String revokeNotes) {
			this.revokeNotes = revokeNotes;
			return this;
		}
		
		public AccessToken build() {
			
			AccessToken accessToken = new AccessToken();
			accessToken.setId(id);
			accessToken.setName(name);
			accessToken.setLinkedUserCode(linkedUserCode);
			accessToken.setCreateUser(createUser);
			accessToken.setCreateDate(createDate);
			accessToken.setCreateTime(createTime);
			accessToken.setTokenNotes(tokenNotes);
			accessToken.setExpiryDate(expiryDate);
			accessToken.setChecksum(checksum);
			accessToken.setRevoked(revoked);
			accessToken.setRevokeUser(revokeUser);
			accessToken.setRevokeDate(revokeDate);
			accessToken.setRevokeNotes(revokeNotes);
			
			return accessToken;
			
		}
		
		
	}

}
