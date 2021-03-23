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
package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.IOException;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.define.ACLDefines.ACLPermission;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.metadata.AccessControlList;

/**
 * Example of how to use ACL
 * 
 * @author sohan
 *
 */
public class RestExample_SetRemoveACL {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		// Create a file for below metadata operation.
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
				hcpClient.putObject(key, file);
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				boolean hasACL = hcpClient.doesObjectACLExist(key);
				System.out.println(hasACL ? "Object has ACL" : "Object does not has ACL");
				if (hasACL) {
					hcpClient.deleteObjectACL(key);

					hasACL = hcpClient.doesObjectACLExist(key);

					if (hasACL) {
						System.out.println("Object ACL failed to removed.");
						return;
					} else {
						System.out.println("Object ACL removed.");
					}
				}

				AccessControlList acl = new AccessControlList();
				// acl.grantPermissionsToUser("user1", ACLPermission.DELETE, ACLPermission.READ, ACLPermission.WRITE);
				acl.grantPermissionsToUser("user1", ACLPermission.DELETE, ACLPermission.WRITE);
				// user2 Only have read permission
				acl.grantPermissionsToUser("user2", ACLPermission.READ);

				// Support other operations:
				// acl.grantPermissionToAllUsers(permissions);
				// acl.grantPermissionToAuthenticatedUsers(permissions);
				// acl.grantPermissionToGroup(groupName, domain, permissions);
				// acl.grantPermissionToUser(userName, domain, permissions);

				// Add current acl to key
				hcpClient.addObjectACL(key, acl);
				// Reset exist acl to current acl.
				// hcpClient.setObjectACL(key, acl);

				hasACL = hcpClient.doesObjectACLExist(key);
				System.out.println(hasACL ? "Object ACL granted" : "Failed to add ACL.");

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}

			// Verify that the ACL configuration is working. The following code is not required for actual development!
			{
				HCPNamespace hcpClient;
				try {
					hcpClient = HCPClients.getInstance().newHCPClient(Account.endpoint, Account.namespace, "dXNlcjI=", Account.secretKey);
				} catch (HSCException e) {
					e.printStackTrace();
					return;
				}

				try {
					hcpClient.deleteObject(key);
				} catch (InvalidResponseException e) {
					System.out.println(e.getMessage() + " because of user2 does not have permission to delete this object");
					// e.printStackTrace();
				} catch (HSCException e) {
					e.printStackTrace();
				}

				try {
					hcpClient.putObject(key, file);
				} catch (InvalidResponseException e) {
					System.out.println(e.getMessage() + " because of user2 does not have permission to write this object");
					// e.printStackTrace();
				} catch (HSCException e) {
					e.printStackTrace();
				}
			}

			// Verify that the ACL configuration is working. The following code is not required for actual development!
			{
				HCPNamespace hcpClient;
				try {
					hcpClient = HCPClients.getInstance().newHCPClient(Account.endpoint, Account.namespace, "dXNlcjE=", Account.secretKey);
				} catch (HSCException e) {
					e.printStackTrace();
					return;
				}

				try {
					HCPObject obj = hcpClient.getObject(key);
					StreamUtils.inputStreamToConsole(obj.getContent(), true);
				} catch (InvalidResponseException e) {
					System.out.println(e.getMessage() + " because of user2 does not have permission to read this object");
					// e.printStackTrace();
				} catch (HSCException e) {
					e.printStackTrace();
				}
			}

			System.out.println("Well done!");
		}
	}

}
