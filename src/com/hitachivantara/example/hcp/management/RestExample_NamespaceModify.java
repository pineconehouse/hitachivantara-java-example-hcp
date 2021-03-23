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

import static org.junit.Assert.assertTrue;

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.AclsUsage;
import com.hitachivantara.hcp.management.define.HashScheme;
import com.hitachivantara.hcp.management.define.OptimizedFor;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

public class RestExample_NamespaceModify {

	public RestExample_NamespaceModify() {
	}

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		String ns = "notexist-bucket-1";
		String ns2 = "notexist-bucket-2";
		// PREPARE TEST DATA ----------------------------------------------------------------------
		// Bucket space cannot be deleted if it is not empty
		namespaceClient.deleteNamespace(ns);
		namespaceClient.deleteNamespace(ns2);

		boolean exist = namespaceClient.doesNamespaceExist(ns);
		assertTrue(exist == false);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		String localUserName1 = "user1";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		
		// Configure a new bucket space
		NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
				.withName(ns)
				.withHardQuota(1.2, QuotaUnit.GB)
				.withSoftQuota(66)
				.withDescription("DDD")
				.withHashScheme(HashScheme.SHA512)
				.withMultipartUploadAutoAbortDays(6)
				.withOptimizedFor(OptimizedFor.CLOUD)
				.withAclsUsage(AclsUsage.ENFORCED)
				.withCustomMetadataIndexingEnabled(false)
				.withSearchEnabled(false)
				.withVersioningEnabled(true)
				.withVersioningKeepDeletionRecords(false)
				.withVersioningPrune(false)
				.withVersioningPruneDays(9)
				.withIndexingEnabled(false)
				//.withOwner(OwnerType.LOCAL, localUserName1 )
				//.withEnterpriseMode(true)
				//.withTags("AAA","BBB","中文")
				.bulid();
		// Execution space creation
		namespaceClient.createNamespace(namespaceSetting1);
		// Verify creation
		exist = namespaceClient.doesNamespaceExist(ns);
		System.out.println("Namespece [" + ns + "] " + (exist ? "created!" : "create failed!"));
		
		// Obtain bucket configuration
		NamespaceSettings namespaceSetting_before_modified = namespaceClient.getNamespaceSettings(ns);

		// Expanding quota to 3GB
		NamespaceSettings namespaceSetting2 = SettingBuilders.modifyNamespaceBuilder()
//				.withName(ns)
				// Change it to the new name
				.withName(ns2)
				// Change to new capacity
				.withHardQuota(3, QuotaUnit.GB)
				// Modify comments
//				.withDescription("xxxxxxxxx")
//				.withMultipartUploadAutoAbortDays(3)
				// Optimized to CLOUD mode (CIFS, NFS and other features will be disabled, performance can be improved)
//				.withOptimizedFor(OptimizedFor.CLOUD)
//				.withSoftQuota(10)
//				.withAclsUsage(AclsUsage.ENFORCED)
				// Meta parsing enabled
//				.withCustomMetadataIndexingEnabled(true)
				// Open search
				.withSearchEnabled(true)
				// Enable Index
				.withIndexingEnabled(true)
//				.withTags("CCC","DDD","中文")
				// Modify the Owner of the bucket
//				.withOwner(OwnerType.LOCAL, localUserName2)
				.bulid();
		
		// Perform changes
		namespaceClient.changeNamespace(ns, namespaceSetting2);

		System.out.println("Namespece [" + ns + "] configuration modified!");

		// Obtain bucket space configuration
		NamespaceSettings namespaceSetting_modified = namespaceClient.getNamespaceSettings(ns2);

		System.out.println("------------------------------------");
		System.out.println("Namespace [" + ns + "->" + ns2 + "]:");
		System.out.println("Capacity Before: " + namespaceSetting_before_modified.getHardQuota() + " " + namespaceSetting_before_modified.getHardQuotaUnit());
		System.out.println("Capacity After : " + namespaceSetting_modified.getHardQuota() + " " + namespaceSetting_modified.getHardQuotaUnit());
		System.out.println("------------------------------------");

		System.out.println("Well done!");
}
}
