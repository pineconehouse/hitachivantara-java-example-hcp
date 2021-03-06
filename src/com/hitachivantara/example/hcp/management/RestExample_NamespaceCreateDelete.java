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
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

/**
 * Create bucket example
 * 
 * @author sohan
 *
 */
public class RestExample_NamespaceCreateDelete {

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		String ns = "notexist-bucket-1";
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// Determine if the bucket exists	
		boolean exist = namespaceClient.doesNamespaceExist(ns);
		System.out.println("Namespece [" + ns + "] " + (exist ? "exist!" : "not exist!"));

		if (exist) {
			// Bucket space cannot be deleted if it is not empty
			namespaceClient.deleteNamespace(ns);
		}

		exist = namespaceClient.doesNamespaceExist(ns);
		if (!exist) {
			System.out.println("Namespece [" + ns + "] deleted!");
		} else {
			System.out.println("Namespece [" + ns + "] failed to delete!");
			return;
		}

		// PREPARE TEST DATA ----------------------------------------------------------------------

		String localUserName1 = "user1";
		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		// Creating a bucket configuration
		NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
				.withName(ns)
				.withHardQuota(11.2, QuotaUnit.GB)
				.bulid();
		// Execute the bucket creation
		namespaceClient.createNamespace(namespaceSetting1);

		// Verify creation
		exist = namespaceClient.doesNamespaceExist(ns);
		System.out.println("Namespece [" + ns + "] " + (exist ? "created!" : "create failed!"));

		System.out.println("Print all namespaces in this tenant:");
		System.out.println("------------------------------------");
		String[] namespaces = namespaceClient.listNamespaces();
		for (String namespace : namespaces) {
			NamespaceSettings setting = namespaceClient.getNamespaceSettings(namespace);
			System.out.println(namespace + " (" + setting.getHardQuota() + " " + setting.getHardQuotaUnit() + ")");
		}
		System.out.println("------------------------------------");

		System.out.println("Well done!");
	}
}
