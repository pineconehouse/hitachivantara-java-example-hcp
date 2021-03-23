/*                                                                             
 * Copyright (C) 2019 Rison Han                                     
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */                                                                            
package com.hitachivantara.example.hcp.management;

import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.Permission;
import com.hitachivantara.hcp.management.model.DataAccessPermission;
import com.hitachivantara.hcp.management.model.DataAccessPermissions;
import com.hitachivantara.hcp.management.model.UserAccount;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

/**
 * Shows how to create users, and assign permissions
 * 
 * @author sohan
 *
 */
public class RestExample_UserPermission {

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement tenant = HCPClients.getInstance().getHCPTenantManagementClient();

		// PREPARE TEST DATA ----------------------------------------------------------------------
		String bucketName = Account.namespace;
		String localUserName1 = "user111";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		
		// Determine if there has the first user, trying to create if not exist
		{
			if (!tenant.doesUserAccountExist(localUserName1)) {
				UserAccount userAccountSetting = SettingBuilders.createUserAcccountBuilder()
					.withUserName(localUserName1, localUserName1)
					.withEnable(true)
					.withPassword("himitu123")
//					.withDescription("created by api")
					.withLocalAuthentication(true)
					.withForcePasswordChange(false)
					.bulid();
				
				tenant.createUserAccount(userAccountSetting);
			}

			// Configure the first user as a read-only user
			DataAccessPermissions permissions1 = SettingBuilders.modifyDataAccessPermissionBuilder()
					.withPermission(bucketName, 
							Permission.BROWSE, 
							Permission.READ)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName1, permissions1);
		}
			
		// Using the first user account to upload the file. The first user just has reading permission. so there will be a permission exception
		{
			String accessKey1 = DigestUtils.toBase64String(localUserName1);
			String secretKey1 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			HCPNamespace namespace1 = HCPClients.getInstance().newHCPClient(Account.endpoint, bucketName, accessKey1, secretKey1);
			
			try {
				// Here is the file will be uploaded into HCP
				File file = Account.localFile1;
				// The location in HCP where this file will be stored.
				String key = file.getName() + ".11";
				namespace1.putObject(key, file);
			} catch (InvalidResponseException e) {
				System.out.println("User [" + localUserName1 + "] does not have [write] permission. So there will be exception with [Permission denied].");
//				e.printStackTrace();
			} catch (HSCException e) {
//				e.printStackTrace();
			}
		}
		
		{
			// Configure the second user as a read-write user
			DataAccessPermissions permissions2 = SettingBuilders.modifyDataAccessPermissionBuilder()
					.withPermission(bucketName,
							Permission.BROWSE,
							Permission.READ,
							Permission.DELETE,
//							Permission.PURGE,
//							Permission.SEARCH,
							Permission.WRITE
//							Permission.READ_ACL,
//							Permission.WRITE_ACL
//							Permission.CHOWN,
//							Permission.PRIVILEGED
							)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName1, permissions2);
		}

		// Using the first user account to upload the file. The first user just has reading permission. so there will be a permission exception
		{
			String accessKey1 = DigestUtils.toBase64String(localUserName1);
			String secretKey1 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			AmazonS3 namespace1 = HCPClients.getInstance().newS3Client(Account.endpoint, accessKey1, secretKey1);
			
			try {
				// Here is the file will be uploaded into HCP
				File file = Account.localFile1;
				// The location in HCP where this file will be stored.
				String key = file.getName() + ".111";
				System.out.println("User [" + localUserName1 + "] does not have [write] permission. So there will be exception with [AccessDenied].");
				namespace1.putObject(bucketName, key, file);
			} catch (AmazonServiceException e) {
				e.printStackTrace();
			} catch (SdkClientException e) {
				e.printStackTrace();
			}
		}	
		
		// Gets the user's current permissions
		{
			System.out.println("Permissions of user [" + localUserName1 + "]:");
			DataAccessPermissions dap = tenant.getDataAccessPermissions(localUserName1);
			DataAccessPermission[] ps = dap.getAllPermissions();
			for (DataAccessPermission dataAccessPermission : ps) {
				System.out.println(dataAccessPermission.getNamespaceName() + " Permissions " + dataAccessPermission.getPermissions());
			}
		}
		
		{
			// Clear user permissions
			// Configure the second user as a read-write user
			DataAccessPermissions permissions2 = SettingBuilders.modifyDataAccessPermissionBuilder()
					// Do not assign any permission or using withoutPermission
//					.withPermission(bucketName)
					.withoutPermission(bucketName)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName1, permissions2);
		}
		
		// You can remove user account by using deleteUserAccount
//		{
//			tenant.deleteUserAccount(localUserName1);
//			tenant.deleteUserAccount(localUserName2);
//		}
		
		System.out.println("Well done!");
	}
}
