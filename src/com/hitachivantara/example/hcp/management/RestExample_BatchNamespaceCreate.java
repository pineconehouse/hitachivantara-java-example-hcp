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

import com.amituofo.common.ex.HSCException;
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

/**
 * Create a bucket example, including specifying create parameters, create users, and assign permissions
 * 
 * @author sohan
 *
 */
public class RestExample_BatchNamespaceCreate {

	public static void main(String[] args) throws HSCException {
		for (int i = 0; i < 28; i++) {
			String string = "namespace"+i;
			createNS(string, 1, "admin");
		}
		
		System.out.println("Well done!");
	}
	
	public static void createNS(String bucketName, double quotaInGB, String user) throws InvalidResponseException, HSCException {
		HCPTenantManagement tenant = HCPClients.getInstance().getHCPTenantManagementClient();

		// PREPARE TEST DATA ----------------------------------------------------------------------

		boolean exist = tenant.doesNamespaceExist(bucketName);
		System.out.println("Namespece [" + bucketName + "] " + (exist ? "exist!" : "not exist!"));

		if (exist) {
			return;
		}

		// Create bucket space where versioning is turned off
		{
			// Creating a bucket configuration
			NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
					.withName(bucketName)
					.withHardQuota(quotaInGB, QuotaUnit.GB)
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
			tenant.createNamespace(namespaceSetting1);
			
			System.out.println("Namespace [" + bucketName + "] created!");
		}
		
		// Open specific protocol 
		{
			HttpProtocolSettings httpProtocolSetting = SettingBuilders.modifyHttpProtocolBuilder()
					// Enable the S3 protocol
					.withHs3Enabled(false)
					.withHs3RequiresAuthentication(true)
					// Enable Rest protocol
					.withRestEnabled(true)
					.withRestRequiresAuthentication(true)
					// Enable HTTP and HTTPS
					.withHttpEnabled(true)
					.withHttpsEnabled(true)
					.bulid();
			
			tenant.changeNamespaceProtocol(bucketName, httpProtocolSetting);
			
//			System.out.println("S3    API      enabled!");
			System.out.println("Rest  API      enabled!");
			System.out.println("Http  Protocol enabled!");
			System.out.println("Https Protocol enabled!");
		}

		// Determine if there has the second user, trying to create if not exist
		{
			if (!tenant.doesUserAccountExist(user)) {
				UserAccount userAccountSetting = SettingBuilders.createUserAcccountBuilder()
						.withUserName(user, user)
						.withEnable(true)
						.withPassword("himitu123")
//						.withDescription("created by api")
						.withLocalAuthentication(true)
						.withForcePasswordChange(false)
						// More roles can be grant here
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
			tenant.changeDataAccessPermissions(user, permissions2);
		}
	}
}
