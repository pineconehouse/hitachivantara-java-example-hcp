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
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.metadata.HCPSystemMetadata;

/**
 * An example of setting system metadata
 * 
 * @author sohan
 *
 */
public class RestExample_SetSystemMetadata {

	public static void main(String[] args) throws IOException {
		HCPNamespace hcpClient;

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		{
			try {
				hcpClient = HCPClients.getInstance().getHCPClient();

				// 如果对象在retention模式下或者hold为true，对象无法被覆盖
				hcpClient.putObject(key, file);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// Modify system metadata
		{
			try {

				HCPSystemMetadata metadata = new HCPSystemMetadata();
				// Adds a lock to the object so that it cannot be deleted or updated
				metadata.setHold(true);
				// Set retention period
				// metadata.setRetention(new Retention("A+1000d+20m"));

//				metadata.setShred(true);
//				metadata.setIndex(false);
//				metadata.setOwner(localUserName);
//				metadata.setOwner(domain, domainUserName);

				hcpClient.setSystemMetadata(key, metadata);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// Attempt to delete the object with HOLD attribute, the HCP will reject the request
		{
			try {
				hcpClient.deleteObject(key);
			} catch (Exception e) {
				System.out.println("Yon can not delete this object, Because it's in HOLD");
			}

			try {
				hcpClient.putObject(key, file);
			} catch (Exception e) {
				System.out.println("Yon can not put a new object to this key, Because it's in HOLD");
			}
		}

		// Delete the Hold object
		{
			try {

				HCPSystemMetadata metadata = new HCPSystemMetadata();
				metadata.setHold(false);

				hcpClient.setSystemMetadata(key, metadata);

				hcpClient.deleteObject(key);
				// Deletes objects under RETENTION
				// hcpClient.deleteObject(new DeleteObjectRequest(key).withPrivilegedDelete(true, "I said"));
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println("Well done!");
	}

}
