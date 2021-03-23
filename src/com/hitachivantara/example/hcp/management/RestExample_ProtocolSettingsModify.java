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

import java.util.List;

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.Protocols;
import com.hitachivantara.hcp.management.model.HttpProtocolSettings;
import com.hitachivantara.hcp.management.model.IPSettings;
import com.hitachivantara.hcp.management.model.builder.SettingBuilders;

/**
 * Shows how to modify the configuration of the Protocol in the namespace
 * @author sohan
 *
 */
public class RestExample_ProtocolSettingsModify {

	public RestExample_ProtocolSettingsModify() {
	}

	public static void main(String[] args) {
		try {
			// Required to enable the Management functionality API and use the user with administrator permission
			HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

			// You can list all namespaces under the current tenant and configure them separately
			// String[] namespacpes = namespaceClient.listNamespaces();
			// for (String namespace : namespacpes) {
			// modifyConfigs(namespace);
			// }

			// Determine whether the current bucket exists.
			boolean exist = namespaceClient.doesNamespaceExist(Account.namespace);
			assertTrue(exist == true);

			// Modify bucket configuration
			modifyConfigs(Account.namespace);

			// Clear the bucket IP configuration
			clearIPConfigs(Account.namespace);

		} catch (InvalidResponseException e) {
			e.printStackTrace();
		} catch (HSCException e) {
			e.printStackTrace();
		}

		System.out.println("Well done!");
	}

	/**
	 * Modify the Protocol configuration
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void modifyConfigs(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		// Gets the current protocol configuration
		HttpProtocolSettings currentHttpSettings = namespaceClient.getNamespaceProtocol(namespace, Protocols.HTTP);
		IPSettings currentIPSettings = currentHttpSettings.getIpSettings();

		// Create protocol configuration
		HttpProtocolSettings httpSettings = SettingBuilders.modifyHttpProtocolBuilder()
				// .withHs3Enabled(!httpSettings1.getHs3Enabled())
				// .withHs3RequiresAuthentication(!httpSettings1.getHs3RequiresAuthentication())
				// .withHswiftEnabled(!httpSettings1.getHswiftEnabled())
				// .withHswiftRequiresAuthentication(!httpSettings1.getHswiftRequiresAuthentication())
				// .withHttpActiveDirectorySSOEnabled(!httpSettings1.getHttpActiveDirectorySSOEnabled())
				// .withHttpEnabled(!httpSettings1.getHttpEnabled())
				// .withHttpsEnabled(!httpSettings1.getHttpsEnabled())
				// .withRestEnabled(!httpSettings1.getRestEnabled())
				// .withRestRequiresAuthentication(!httpSettings1.getRestRequiresAuthentication())
				// .withWebdavBasicAuth("webdavBasicAuthUser1", "!QAZ1qaz")
				// .withWebdavCustomMetadata(!httpSettings1.getWebdavCustomMetadata())
				// .withWebdavEnabled(!httpSettings1.getWebdavEnabled())
				.withIpSettings(currentIPSettings)
				// Specifies a whitelist address that can be accessed, multiple IP addresses can be added, or masking addresses can be configured via List<string>
				.withAllowAddressees("10.10.10.99", "192.168.1.111/27")
				// Specifies a blacklist address
				.withDenyAddresses("10.10.10.1", "192.168.111.123/27", "192.168.9.123/26")
				// Configure if the same IP on the whitelist is also on the blacklist, will be allowed access
				.withAllowIfInBothLists(true)
				.bulid();

		namespaceClient.changeNamespaceProtocol(namespace, httpSettings);

		System.out.println("Namespece [" + namespace + "] configuration modified!");

		// ----------------------------------------------------------------------------------------------
		printCurrentIPSettings(namespace);
		// ----------------------------------------------------------------------------------------------
	}
	
	/**
	 * Clear IP configuration
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void clearIPConfigs(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		// Create protocol configuration
		HttpProtocolSettings httpSettings = SettingBuilders.modifyHttpProtocolBuilder()
				// You can clear the configuration separately
				 .withClearAllowAddressees()
				 .withClearDenyAddresses()
//				.withAllowIfInBothLists(true)
				.bulid();

		namespaceClient.changeNamespaceProtocol(namespace, httpSettings);

		System.out.println("Namespece [" + namespace + "] IP configuration cleared!");

		// ----------------------------------------------------------------------------------------------
		printCurrentIPSettings(namespace);
		// ----------------------------------------------------------------------------------------------
	}
	
	/**
	 * Displays the current IP configuration
	 * @param namespace
	 * @throws InvalidResponseException
	 * @throws HSCException
	 */
	private static void printCurrentIPSettings(String namespace) throws InvalidResponseException, HSCException {
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		// ----------------------------------------------------------------------------------------------
		HttpProtocolSettings httpSettings2 = namespaceClient.getNamespaceProtocol(namespace, Protocols.HTTP);

		List<String> allows = httpSettings2.getIpSettings().getAllowAddresses();
		System.out.println("AllowAddresses of Namespece [" + namespace + "] count:" + allows.size());
		for (String ip : allows) {
			System.out.println(" " + ip);
		}

		List<String> denys = httpSettings2.getIpSettings().getDenyAddresses();
		System.out.println("DenyAddresses of Namespece [" + namespace + "] count:" + denys.size());
		for (String ip : denys) {
			System.out.println(" " + ip);
		}
		// ----------------------------------------------------------------------------------------------
	}
	
	

}
