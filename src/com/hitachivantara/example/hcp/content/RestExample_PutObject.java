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

/**
 * Upload the object file to the HCP bucket
 * 
 * @author sohan
 *
 */
public class RestExample_PutObject {

	public static void main(String[] args) throws IOException, HSCException {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Create an HCP access client. The client needs to be created only once
		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		// Inject file into HCP system.
		try {
			hcpClient.putObject(key, file);
		} catch (InvalidResponseException e) {
			markPutFailedObject(key, file);
			// e.getReason()
			// e.getStatusCode()
			e.printStackTrace();
		} catch (HSCException e) {
			markPutFailedObject(key, file);
			e.printStackTrace();
		}

		System.out.println("Well done!");
	}

	/**
	 * @param key
	 * @param file
	 */
	private static void markPutFailedObject(String key, File file) {
		// Your code
	}

}
