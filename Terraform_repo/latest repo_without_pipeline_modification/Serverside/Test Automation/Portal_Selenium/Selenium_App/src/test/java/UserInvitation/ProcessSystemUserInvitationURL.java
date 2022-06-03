package UserInvitation;

import org.testng.annotations.Test;


import com.capita.fusionPortalPages.UserInvitation_ProcessSystemUserInvitationPage;
import com.capita.fusionPortalPages.portalLogin;

import BasePackage.Basepage;

public class ProcessSystemUserInvitationURL extends Basepage {
	UserInvitation_ProcessSystemUserInvitationPage userInvitation_ProcessSystemUserInvitationPage;
	portalLogin portallogin;
	String inviteUrl, validationMessage;

	@Test
	public void VerifyProcessSystemUserInvitationForExpiredInvite() {
		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsid2luZG93c2xvZ2luIjpudWxsLCJpbnZpdGVpZCI6IjgwMDIzNjAyLTdkMTYtNDNmYy1iZjUxLTM0MGJhYjhkYzM2MCIsInNiYWNjZXNzZ3JvdXAiOiIiLCJhZG1pbiI6MCwiZXhwaXJ5IjoyMDIyMDMxNiwidXNlcmNvZGUiOiJSRVZPS0VEU1lTIiwicG9ydGFsYWNjZXNzZ3JvdXAiOiJBQ0NFU1NUT0tFIiwidXNlcmNsYXNzIjoiT1RIRVIiLCJ1cmkiOiJodHRwczpcL1wvZmllbGRzbWFydC1xMDEudWtzb3V0aC5jbG91ZGFwcC5henVyZS5jb21cL2ZkbSIsInVzZXJuYW1lIjoiUkVWT0tFRFN5c0ludml0ZSJ9fQ.L1G329TMbqrG59-XXu2eafoh-bBFukoW4PVXqPuoTFKUZKGwOwciVDewiSfd7pU6P00tk5jj5lUk8ZxsJcKxFER8Q1Jn_OU7D9mMvD6RjIaFI1CO0Z3gyD91165kA_35mdYx4_kgs034yr0ocN9u0XYsCgop6tBlAVJaa0cXblYbO0Ctv6dE1oinerkBRbhI6L_plBegcuy0zE09WhSjbsHw5O5bL-NbjSAVjfUfgOkYMkKjZjsfGJkNtPHiSk66337OEpvjLvaUlvKByvg9EGh17o-4cYFWDLYy0wB2rSZFjzxOrXUi5pRf09f4IeFYEZzwo4v8fvA6KYzZeqvihQ";
			validationMessage = "System User Invitation Revoked. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForRevokedInvite() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiMmUxNzZhOWUtNjFkMi00YzQ3LWEwYTUtOWJjMmQ1Y2UyZTA5IiwidXNlcmNvZGUiOiJHQU5FU0giLCJ1c2VybmFtZSI6ImdhbmVzaCIsInBvcnRhbGFjY2Vzc2dyb3VwIjoiRkRNUFJJTUFSWSIsInNiYWNjZXNzZ3JvdXAiOiJTQlBSSU1BUlkiLCJ3aW5kb3dzbG9naW4iOiJ0ZXN0IiwidXNlcmNsYXNzIjoidGVzdCIsImFkbWluIjoxLCJleHBpcnkiOjIwMjIwMTAxfX0.LfoMAYGXWAx1-gFl84cE7aONvR8bcX1qzxUZUpgjl6UaM3v-o7pttKaayR2uavWQbrJ_MBAvdOTqcjZ7zJV5XC36v-GvJcrog0Uwtk2vwbwsiE3qsjZNyZelOYTaPKR5W60ei06yJYcxSmiLzgLxkpmei7Sh2a9oL3n5oxL6W9CY-DC8HueSEvj4kSBTpmCLX_N_ToKlofsN1H2lqBjV03oGJX3wRB7CyQhN2tgCt24qDuyihZF_S4H0Fw2sdYhrn9DZy_lj4X1_kOES5NFgCAzccHJs_iTktowx26TNW2RtieNFh0ErKHWsUooFd0tH_SgcVxnQAG8Ub-dugu3szw&dswid=-8826";
			validationMessage = "System User Invitation has expired. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessValidSystemUserInvitation() {
		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();
			portallogin = new portalLogin();

			String inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cHM6XC9cL2ZpZWxkc21hcnQtcTAxLnVrc291dGguY2xvdWRhcHAuYXp1cmUuY29tXC9mZG0iLCJpbnZpdGVpZCI6IjY3MDg4NjNhLTllNTQtNGIyYS1hYjAzLWYyNjBlNzhkZTNlZSIsInVzZXJjb2RlIjoiU1lTVkFMIiwidXNlcm5hbWUiOiJzeXN2YWwiLCJwb3J0YWxhY2Nlc3Nncm91cCI6IkFDQ0VTU1RPS0UiLCJzYmFjY2Vzc2dyb3VwIjoiIiwid2luZG93c2xvZ2luIjpudWxsLCJ1c2VyY2xhc3MiOiJPVEhFUiIsImFkbWluIjowLCJleHBpcnkiOjIwMjIwMzE4fX0.FU2FDSv6mn0-8cdLOVDkjxaKKOZsP4ShovjnWDloh-52vEg0miMnsvv9tF_V8F_H89YzhIJUJEl3GXijOiYNfB7xgaDM0mLb43lYITRR_EGcHoqEIjiU9PKEScgOdTscUURAXlv0MapqMvVv2UsQogmOB2RXlr1pnGQmA_l9oE1J7o7VlmJ0OAAh4uz9CUasPPPK8vtXoPP_Keq7bbplOgNGBKFNgjlK3omyDPcMsRPgQnHHeAf4GrtBs6aCve_B7m1_TgG7nFAFIOdUcputocOKkN1HZnFiuB5okECoH_MJDkBwc8Qo9meiq8I86ed-WhsJSQtfhY9xcvaLrTrbvg";

			portallogin.logout();

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);

			portallogin.login("systemuser2@capita.co.uk", "Sybex101");

			// Validate the invitation once it is processed already
			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);

			validationMessage = "System User Invitation Already Processed. Please contact support";
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
			portallogin.logout();

			String filepath = System.getProperty("user.dir") + "//src//main//java//Config//data.properties";
			Basepage.initializeProp(filepath);

			portallogin.login(Basepage.readProperty("username"), Basepage.readProperty("password"));
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void VerifyProcessSystemUserInvitationForNullUserCode() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiMDAwOTZlODktZTQ2Yi00OGQ3LWE5NzItNmVhYWZiMTA2M2Y5IiwidXNlcmNvZGUiOiIiLCJ1c2VybmFtZSI6ImdhbmVzaCIsInBvcnRhbGFjY2Vzc2dyb3VwIjoiQUNDRVNTVE9LRSIsInNiYWNjZXNzZ3JvdXAiOiJTQlBSSU1BUlkiLCJ3aW5kb3dzbG9naW4iOiJHQU5FU0giLCJ1c2VyY2xhc3MiOiJ0ZXN0IiwiYWRtaW4iOjEsImV4cGlyeSI6MjAyMjAzMTZ9fQ.RT1oDmrYeRkbTez4RdADgn1WTSyY8qOvn3W6jpFPWNyiHEKxvBelLsKAqymAhcRYYgeqaDT0VnJ0PxygZJBbyBxLLiQISy48Dlthim4iKNUkVvC4nFsSmaiXiISInJXZBCvNS1DaBdeEe0J5-tyvf9WTXfmfpq9yjR4y9HEMREHULngWTXQaZ5YGBuKRDvCdRooNo5cMJHS1vAvU9NKtS3iF2a-JXO3vmmbY1L2h7kGiq6hqhNGp5xbaE_Mnk5scnwBY6Z8TgrHullA0vkVJz7khiW3vdKVopFqCenHuUJrTkNXK-AIYe8CZpfABc_mf5DiQlBLj5aitrbZbb2wt6g";
			validationMessage = "Invalid Invitation. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForNullUserName() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiNzIyMDUxZDUtZjZmOS00ZmQ0LTliM2ItZTI5ZDRhZjEwNWUzIiwidXNlcmNvZGUiOiJHQU5FU0giLCJ1c2VybmFtZSI6IiIsInBvcnRhbGFjY2Vzc2dyb3VwIjoiQUNDRVNTVE9LRSIsInNiYWNjZXNzZ3JvdXAiOiJTQlBSSU1BUlkiLCJ3aW5kb3dzbG9naW4iOiJHQU5FU0giLCJ1c2VyY2xhc3MiOiJnYW5lc2giLCJhZG1pbiI6MSwiZXhwaXJ5IjoyMDIyMDMxNn19.osDCgPbxPugmGk-Jnvfckldz7_ysVc1wBPmAW0ig21F8vvpq_ILonm6v4osVmSDYnKEHqWM4KYpDQEKOm8781x3M_XqL4tcaVYwBueQxEMUE26e4CIvMZji42oTD-yzk6dM6JPx4-15tvKbhCZSzAQCZjJHx6mRK4rTiFPVbgv6PqfS1QVK4mukjhj-kmWAvZ-h_ji4AfVnMUXvq6Ve0pfjHL5IwyC2As0LXYRj4kXT8KXUZbbheLYzXiZ3qkcTOgY3MRM7Ajdz-T2BTdTd65GOXbsd6jJZ7lBi-GovFZ9nvJx7kYHL4qcecLoFg9j7p6PNnJQKtbLLtpp7bqAmd7A";
			validationMessage = "Invalid Invitation. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForNullPortalAccessGroup() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiMmUwODY5OTEtMzVlNy00ODY0LWI4NjUtOTk5ZTlmZmEwNWMzIiwidXNlcmNvZGUiOiJHQU5FU0giLCJ1c2VybmFtZSI6ImdhbmVzaCIsInBvcnRhbGFjY2Vzc2dyb3VwIjoiIiwic2JhY2Nlc3Nncm91cCI6IlNCUFJJTUFSWSIsIndpbmRvd3Nsb2dpbiI6IkdBTkVTSCIsInVzZXJjbGFzcyI6ImdhbmVzaCIsImFkbWluIjoxLCJleHBpcnkiOjIwMjIwMzE2fX0.dVTHs7Dp4vFGwv3sKorwVIpVzyfDlsSNB_GEwEeihYLOPg2qrBFCF_Io61VNZAO0J67l0pquT2RXTrGL8-VZzkeFeFTK_-EeareLdPHMf5vNo9X-c07s0rwCkkpWmQgsdbcpz_bGtAB1NUSkj1mXe_MvK0th6XTogIulOc4v-0Qid3zkYOMGvMIzuopYqKo6d0KASQ72FdRU3cbCi9R7JYPw-lm9Z_MdlOjFaxLy79_vOv6wdXEegh_8ME5YxmcDmJu92yKL9WDv2GhX68geUTLLKrV-5WltfJSWb7-zFkR11j3h2pfTFK1Ry8_Rb5fSMpkG6YZYa3zSWR0mSEwUTA";
			validationMessage = "Invalid Invitation. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForNonExistSBAccessGroup() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiZWIzZjk4NTEtMWUwNC00OTcxLTliYWItNzdmNzI2ZDA4MzQyIiwidXNlcmNvZGUiOiJJTlZBTElEU0JCIiwidXNlcm5hbWUiOiJJTlZBTElEU0JCIiwicG9ydGFsYWNjZXNzZ3JvdXAiOiJBQ0NFU1NUT0tFIiwic2JhY2Nlc3Nncm91cCI6ImludmFsaWRzYmFjY2Vzc2dyb3VwIiwid2luZG93c2xvZ2luIjpudWxsLCJ1c2VyY2xhc3MiOiJPVEhFUiIsImFkbWluIjoxLCJleHBpcnkiOjIwMjMwMjE4fX0.s77LUrA1wgKH4W1pNrRTJbHGXoE2xjwB5JjIs6-e55wt3wcB1dHKrZpFGW7-EiuqUATIkIlM9QWPD3b8HP3lcMMXnK7aLzD-qtDm768A50_FaRgYyh3W3wIFdbECwevMSFs1ajBfVgsyrRNyfQduKS-oRIUtE5rs1gxYYMeZjqCxOS2_7VaSYPpKkkSj4W3GAHFntSoUEAMywypcboF5gr0W3IWulIdfBjdZIwpdcCttuZdvDsYjxM3LO38QYPC_vTMI29JI1eK9JaUOGsjCx0CuUKqFDRs6rDaNxNLfUY58a9HxX2VzSgEHYQNuX7D7f1-r7vCjq8hTRF77DY6rlg";
			validationMessage = "System User Invitation is no longer valid. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForNonExistPortalAccessGroup() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cHM6XC9cL2ZpZWxkc21hcnQtcTAxLnVrc291dGguY2xvdWRhcHAuYXp1cmUuY29tXC9mZG0iLCJpbnZpdGVpZCI6IjlhZDU3YjEyLTRiYzktNDY3OC04NDJlLTJiNDEwZDUyZmY1OCIsInVzZXJjb2RlIjoiUEFHTk9ORVhJUyIsInVzZXJuYW1lIjoiUEFHTk9ORVhJUyIsInBvcnRhbGFjY2Vzc2dyb3VwIjoiQUJISVBBRyIsInNiYWNjZXNzZ3JvdXAiOiIiLCJ3aW5kb3dzbG9naW4iOm51bGwsInVzZXJjbGFzcyI6Ik9USEVSIiwiYWRtaW4iOjAsImV4cGlyeSI6MjAyMjAzMjF9fQ.v6wkPMo7kdjBxSyyeVmMGYk36nDBrpt4ZT71J_bAcyyNtV9uHb4c4UTilEamoFOpnO4H5dfQWRDtj_c78GtBB3u6f0gE1p9TJLZFP9U-lmj1YzDksDafjbbFAG5LW_fHK4XgynjVV6VyU_G6N1MZob30jUMgPaA-NI1bBmBRGGMUrybuifxWpSNI_X1c6hsWuBgxdYLnBgzHMpH4tG7c6pP2OChScw2NVB6-dZhtDRzMvpGntvhi1bfBvjcD8ZSshBYGqxdjs9ZNnTVUK6fGdBRjeRqzmlcoGL05mJ6BBV3Stp5fuDConniq-wBZpci3ngTnnahd81-JUXd1g-brvQ";
			validationMessage = "System User Invitation is no longer valid. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void VerifyProcessSystemUserInvitationForExistingUserCode() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cHM6XC9cL2ZpZWxkc21hcnQtcTAxLnVrc291dGguY2xvdWRhcHAuYXp1cmUuY29tXC9mZG0iLCJpbnZpdGVpZCI6IjcxMzQyYWQ2LTJkMWEtNGEzZi1iM2E0LWE5M2E3YzljMzBkNyIsInVzZXJjb2RlIjoiQUJISVNIRUsiLCJ1c2VybmFtZSI6IkFCSElTSEVLIiwicG9ydGFsYWNjZXNzZ3JvdXAiOiJBQ0NFU1NUT0tFIiwic2JhY2Nlc3Nncm91cCI6IiIsIndpbmRvd3Nsb2dpbiI6bnVsbCwidXNlcmNsYXNzIjoiT1RIRVIiLCJhZG1pbiI6MCwiZXhwaXJ5IjoyMDIyMDMyM319.S6whtd9GbtLSy_DySIu2fUSv3ruUterh5Rq6vR67YSjS8b78HLkDLv34rkPLxum9SBsZy_m_Unq_r290aqxTpgr_UMHhV3KV69Bvjj_QVaqyqY7xAxfvtIS1Bp6GGEBeZFisMjgqxwNhCtohUS8Oyh1uNG1hAO0f6CUXUirxBAHxQP8-dyvsm0V9pNZq-G-vRiMM16tTJxuvFAr1TRUZYa1MEnnevxZm0uhZOK4uPv92B2fhbkP55ektGk7U5YyfY5CMlHkyQWOeasyofA8_KpygMG7-ov0MWvf4Z7MjVvEByaJLw0Y0-gLFcJJ_7VOUmQkAvRbYReshBnf1YJREFA";
			validationMessage = "System User Invitation is no longer valid. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// following test aims at hitting the tempered(updated with random string)
	// invite
	@Test
	public void VerifyProcessSystemUserInvitationForInvalidURL() {

		try {
			userInvitation_ProcessSystemUserInvitationPage = new UserInvitation_ProcessSystemUserInvitationPage();

			inviteUrl = "https://fieldsmart-q01.uksouth.cloudapp.azure.com/fdm/Invite.xhtml?inviteToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGFpbXMiOnsidXJpIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwNzBcL2ZkbSIsImludml0ZWlkIjoiZWIzZjk4NTEtMWUwNC00OTcxLTliYWItNzdmNzI2ZDA4MzQyIiwidXNlcmNvZGUiOiJJTlZBTElEU0JCIiwidXNlcm5hbWUiOiJJTlZBTElEU0JCIiwicG9ydGFsYWNjZXNzZ3JvdXAiOiJBQ0NFU1NUT0tFIiwic2JhY2Nlc3Nncm91cCI6ImludmFsaWRzYmFjY2Vzc2dyb3VwIiwid2luZG93c2xvZ2luIjpudWxsLCJ1c2VyY2xhc3MiOiJPVEhFUiIsImFkbWluIjoxLCJleHBpcnkiOjIwMjMwMjE4fX0.s77LUrA1wgKH4W1pNrRTJbHGXoE2xjwB5JjIs6-e55wt3wcB1dHKrZpFGW7-EiuqUATIkIlM9QWPD3b8HP3lcMMXnK7aLzD-qtDm768A50_FaRgYyh3W3wIFdbECwevMSFs1ajBfVgsyrRNyfQduKS-oRIUtE5rs1gxYYMeZjqCxOS2_7VaSYPpKkkSj4W3GAHFntSoUEAMywypcboF5gr0W3IWulIdfBjdZIwpdcCttuZdvDsYjxM3LO38QYPC_vTMI29JI1eK9JaUOGsjCx0CuUKqFDRs6rDaNxNLfUY58a9HxX2VzSgEHYQNuX7D7f1-r7vCjq8hTRFABHISHEK";
			validationMessage = "Invalid Invitation. Please contact support";

			userInvitation_ProcessSystemUserInvitationPage.navigateToInvitationUrl(inviteUrl);
			userInvitation_ProcessSystemUserInvitationPage.verifyValidationMessage(validationMessage);

			driver.navigate().back();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
