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
import com.hitachivantara.hcp.management.define.Permission;
import com.hitachivantara.hcp.management.model.HttpProtocolSettings;
import com.hitachivantara.hcp.management.model.NamespaceSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

public class RestExample_NamespaceDataAccessPermissionsModify {

	public RestExample_NamespaceDataAccessPermissionsModify() {
	}

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		String ns = "test";
		// PREPARE TEST DATA ----------------------------------------------------------------------
		boolean exist = namespaceClient.doesNamespaceExist(ns);
		assertTrue(exist == true);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		{
			// Configure permissions
			NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
					.withName(ns)
					.withAuthAndAnonymousMinimumPermissions(Permission.BROWSE,Permission.READ,Permission.WRITE)
					.withAuthMinimumPermissions(Permission.BROWSE,Permission.READ,Permission.WRITE,Permission.DELETE)
//					.withTags("tag1","xxx")
					.bulid();
			namespaceClient.changeNamespace(ns, namespaceSetting1);
		}
		// Modify the protocol
		{
			HttpProtocolSettings httpProtocolSetting = SettingBuilders.modifyHttpProtocolBuilder()
					.withRestEnabled(true)
					// Enable accessed anonymously
					.withRestRequiresAuthentication(false)
					.bulid();

			namespaceClient.changeNamespaceProtocol(ns, httpProtocolSetting);
		}

		System.out.println("Namespece [" + ns + "] configuration modified!");
		
		// Remove minimum permission
		{
			NamespaceSettings namespaceSetting1 = SettingBuilders.createNamespaceBuilder()
					.withName(ns)
					.withoutAuthAndAnonymousMinimumPermissions()
//					.withoutAuthMinimumPermissions()
//					.withoutTags()
					.bulid();
			namespaceClient.changeNamespace(ns, namespaceSetting1);
		}
		
		System.out.println("Namespece [" + ns + "] minimum permissions cleared!");

		System.out.println("Well done!");
	}
}
