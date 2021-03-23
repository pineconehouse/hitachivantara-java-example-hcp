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

import java.io.IOException;

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;

/**
 * Create directorys
 * 
 * @author sohan
 *
 */
public class RestExample_CreateDirectory {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Generates a random directory Key
				final String directoryKey = "example-hcp/subfolder" + RandomInputStream.randomInt(100, 999);
				
				// Check if this directory exists,
				boolean exist = hcpClient.doesDirectoryExist(directoryKey);
				
				if (exist) {
					// Delete the directory recursively (all the file and folder will be deleted in the directory)
					hcpClient.deleteDirectory(new DeleteDirectoryRequest(directoryKey).withDeleteContainedObjects(true));
				}

				// Create folder
				hcpClient.createDirectory(directoryKey);
				
				System.out.println("Folder created!");
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
