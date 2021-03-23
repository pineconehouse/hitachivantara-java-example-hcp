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
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

public class RestExample_NamespaceReplicationEnable {

	public RestExample_NamespaceReplicationEnable() {
	}

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		String ns = "ns1";
		// PREPARE TEST DATA ----------------------------------------------------------------------

		boolean exist = namespaceClient.doesNamespaceExist(ns);
		assertTrue(exist == true);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		NamespaceSettings namespaceSetting2 = SettingBuilders.modifyNamespaceBuilder()
				.withName(ns)
				.bulid();
		
		namespaceSetting2.setReplicationEnabled(true);

		namespaceClient.changeNamespace(ns, namespaceSetting2);

		System.out.println("Namespece [" + ns + "] configuration modified!");
		System.out.println("Well done!");
}
}
