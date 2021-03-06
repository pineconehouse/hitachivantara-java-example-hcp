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
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;

/**
 * Use the HCP SDK to delete all files in a directory including subdirectories
 * 
 * @author sohan
 *
 */
public class RestExample_DeleteDirectory {

	public static void main(String[] args) throws IOException {
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				final String directoryKey = "example-hcp/moreThan100objs/";
				
				// After execute folder "moreThan100objs" will be removed from HCP
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				hcpClient.deleteDirectory(new DeleteDirectoryRequest().withDirectory(directoryKey)
						// You cannot delete specific old versions of an object, but if you have purge permission, you can purge the object to delete all its versions.
						//.withPurge(true)
						// Support privileged delete
//						.withPrivileged(true, "I Said!")
						// Delete the objects in folder/subfolder.
						.withDeleteContainedObjects(true)
						// You can add a delete event listener to listen for each object's deletion event
//						.withDeleteListener(new ObjectDeletingListener() {
//							// Trigger after deletion
//							@Override
//							public NextAction afterDeleting(HCPObjectSummary obj, boolean deleted) {
//								System.out.println("Object " + obj.getKey() + (deleted ? " deleted. " : " count not be deleted."));
//								return null;
//							}
//							// Trigger before deletion
//							@Override
//							public NextAction beforeDeleting(HCPObjectSummary objectSummary) {
//								// TODO Auto-generated method stub
//								return null;
//							}
//						})
						);
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				
				boolean exist = hcpClient.doesDirectoryExist(directoryKey);
				
				System.out.println("Folder " + directoryKey + (exist ? " failed to deleted!" : " deleted!"));
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
