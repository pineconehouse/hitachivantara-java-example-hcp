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
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.AclsUsage;
import com.hitachivantara.hcp.management.define.HashScheme;
import com.hitachivantara.hcp.management.define.OptimizedFor;
import com.hitachivantara.hcp.management.define.Permission;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.DataAccessPermissions;
import com.hitachivantara.hcp.management.model.HttpProtocolSettings;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.UserAccount;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObject;

/**
 * Create bucket example, including specifying create parameters, create users, and assign permissions
 * 
 * @author sohan
 *
 */
public class RestExample_NamespaceIntegratedCreate {

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement tenant = HCPClients.getInstance().getHCPTenantManagementClient();

		String bucketName = "notexist-bucket-1";
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// Determine if the bucket exists
		boolean exist = tenant.doesNamespaceExist(bucketName);
		System.out.println("Namespece [" + bucketName + "] " + (exist ? "exist!" : "not exist!"));

		if (exist) {
			// Bucket space cannot be deleted if it is not empty
			// If cannot delete, please manually delete all files in the bucket, and run Garbage Collection and closing Version/Search/Keep records function in namespace first.
			tenant.deleteNamespace(bucketName);
		}

		exist = tenant.doesNamespaceExist(bucketName);
		if (!exist) {
			System.out.println("Namespece [" + bucketName + "] deleted!");
		} else {
			System.out.println("Namespece [" + bucketName + "] failed to delete!");
			return;
		}

		// PREPARE TEST DATA ----------------------------------------------------------------------
		String localUserName1 = "user111";
		String localUserName2 = "user222";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		
		// Create bucket space where versioning is turned off
		{
			// Creating a bucket configuration
			NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
					.withName(bucketName)
					.withHardQuota(4, QuotaUnit.GB)
					// Enable search and metadata parsing
					.withCustomMetadataIndexingEnabled(true)
					.withIndexingEnabled(true)
					.withCustomMetadataValidationEnabled(true)
					.withSearchEnabled(true)
					.withHashScheme(HashScheme.MD5)
					// Configure to CLOUD mode
					.withOptimizedFor(OptimizedFor.CLOUD)
					// Disable versioning
					.withVersioningEnabled(false)
					.withVersioningKeepDeletionRecords(false)
					.withVersioningPrune(false)
					// Open ACL for S3 API
					.withAclsUsage(AclsUsage.ENFORCED)
					.bulid();
			// Execute the create bucket
			tenant.createNamespace(namespaceSetting1);
			
			System.out.println("Namespace [" + bucketName + "] created!");
		}
		
		// Open specific protocol 
		{
			HttpProtocolSettings httpProtocolSetting = SettingBuilders.modifyHttpProtocolBuilder()
					// Enable the S3 protocol
					.withHs3Enabled(true)
					.withHs3RequiresAuthentication(true)
					// Enable the REST protocol
					.withRestEnabled(true)
					.withRestRequiresAuthentication(true)
					// Enable HTTP and HTTPS
					.withHttpEnabled(true)
					.withHttpsEnabled(true)
					.bulid();
			
			tenant.changeNamespaceProtocol(bucketName, httpProtocolSetting);
			
			System.out.println("S3    API      enabled!");
			System.out.println("Rest  API      enabled!");
			System.out.println("Http  Protocol enabled!");
			System.out.println("Https Protocol enabled!");
		}

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
		
		// Determine if there has the second user, trying to create if not exist
		{
			if (!tenant.doesUserAccountExist(localUserName2)) {
				UserAccount userAccountSetting = SettingBuilders.createUserAcccountBuilder()
						.withUserName(localUserName2, localUserName2)
						.withEnable(true)
						.withPassword("himitu123")
//						.withDescription("created by api")
						.withLocalAuthentication(true)
						.withForcePasswordChange(false)
						// You can grant more roles to user
//						.withRole(
//								Role.ADMINISTRATOR,
//								Role.MONITOR 
////							Role.SECURITY, 
////							Role.COMPLIANCE
//								)
						.bulid();
					
					tenant.createUserAccount(userAccountSetting);
			}
	
			// Configure the second user as a read-write user
			DataAccessPermissions permissions2 = SettingBuilders.modifyDataAccessPermissionBuilder()
					.withPermission(bucketName,
							Permission.BROWSE,
							Permission.READ,
							Permission.DELETE,
							Permission.PURGE,
							Permission.SEARCH,
							Permission.WRITE
//							Permission.READ_ACL,
//							Permission.WRITE_ACL
//							Permission.CHOWN,
//							Permission.PRIVILEGED
							)
					.bulid();
			tenant.changeDataAccessPermissions(localUserName2, permissions2);
		}
		
		// Using the second user account to upload/download/delete file.
		{
			String accessKey2 = DigestUtils.toBase64String(localUserName2);
			String secretKey2 = DigestUtils.calcMD5ToHex("himitu123").toLowerCase();
			HCPNamespace namespace2 = HCPClients.getInstance().newHCPClient(Account.endpoint, bucketName, accessKey2, secretKey2);
			
			// Here is the file will be uploaded into HCP
			File file = Account.localFile1;
			// The location in HCP where this file will be stored.
			String key = file.getName();
			try {
				namespace2.putObject(key, file);
				System.out.println("File uploaded by user [" + localUserName2 + "]");
	
				HCPObject obj = namespace2.getObject(key);
				File tempFile = File.createTempFile(file.getName(), "");
				StreamUtils.inputStreamToFile(obj.getContent(), tempFile, true);
				System.out.println("File downloaded by user [" + localUserName2 + "] " + tempFile.getPath());
				
//				namespace2.deleteObject(new DeleteObjectRequest(key).withPurge(true));
				namespace2.deleteObject(key);
				System.out.println("File deleted by user [" + localUserName2 + "]" );
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				System.out.println("User [" + localUserName1 + "] does not have [write] permission. So there will be exception with [Permission denied].");
				namespace1.putObject(key, file);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
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
		
		// You can remove user account by using deleteUserAccount
//		{
//			tenant.deleteUserAccount(localUserName1);
//			tenant.deleteUserAccount(localUserName2);
//		}
		
		System.out.println("Well done!");
	}
}
