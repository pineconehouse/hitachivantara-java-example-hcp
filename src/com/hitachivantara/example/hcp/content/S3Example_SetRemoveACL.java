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
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * Example of how to use ACL
 * 
 * @author sohan
 *
 */
public class S3Example_SetRemoveACL {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;

		// Create a file for below metadata operation.
		{
			AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();
			hs3Client.putObject(bucketName, key, file);
			
			hs3Client.setBucketAcl(new SetBucketAclRequest(bucketName, CannedAccessControlList.AuthenticatedRead));
		}

		{
			try {
				AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

				{
					AccessControlList acl = hs3Client.getObjectAcl(bucketName, key);
					List<Grant> grants = acl.getGrantsAsList();
					for (Grant grant : grants) {
						System.out.format("%s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
					}
				}

				System.out.println("--------------------------------------------------");

				// Start to set ACL
				// {
				// // !!!!!!!!!!This is the id of HCP local user, Please change to your ID!!!!!!!!!!
				// // user1 49f9a563-538e-4fb7-bda0-8126180029e2
				// // admin 49f9a563-538e-4fb5-bda0-8126180029e2
				// CanonicalGrantee grantee = new CanonicalGrantee("49f9a563-538e-4fb7-bda0-8126180029e2");
				//// grantee.setDisplayName("user1");
				// AccessControlList acl = new AccessControlList();
				// acl.setOwner(new Owner("49f9a563-538e-4fb7-bda0-8126180029e2","user1"));
				//// grantee.setIdentifier(id);
				// acl.grantPermission(grantee, Permission.Read);
				//// acl.grantPermission(grantee, Permission.Write);
				//// acl.grantPermission(grantee, Permission.ReadAcp);
				// hs3Client.setObjectAcl(bucketName, key, acl);
				// System.out.println("ACL created!");
				// }

				{
					// Get the ACL of the existing object
					AccessControlList list = hs3Client.getObjectAcl(bucketName, key);
					list.getGrantsAsList().clear();
					/* Grant read permission for a user only */
					Grant grant0 = new Grant(new CanonicalGrantee("49f9a563-538e-4fb7-bda0-8126180029e2"), Permission.Read);

					list.getGrantsAsList().add(grant0);

					hs3Client.setObjectAcl(bucketName, key, list);

//					AmazonS3 clientLoginByUser1 = HCPClients.getInstance().newS3Client(Account.endpoint, "dXNlcjE=", Account.secretKey);
//					try {
//						System.out.println("Key " + key + " exist=" + clientLoginByUser1.doesObjectExist(bucketName, key));
//						clientLoginByUser1.deleteObject(bucketName, key);
//						System.out.println("Key " + key + " exist=" + clientLoginByUser1.doesObjectExist(bucketName, key));
//					} catch (SdkClientException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}

				{
					// hs3Client.setObjectAcl(bucketName, key, CannedAccessControlList.AuthenticatedRead);
					// hs3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
				}

				System.out.println("--------------------------------------------------");

				// Print ACL again
				{
					AccessControlList acl = hs3Client.getObjectAcl(bucketName, key);
					List<Grant> grants = acl.getGrantsAsList();
					for (Grant grant : grants) {
						System.out.format("%s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
					}
				}

			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}
