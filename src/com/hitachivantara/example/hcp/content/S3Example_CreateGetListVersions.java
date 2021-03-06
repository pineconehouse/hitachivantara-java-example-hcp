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
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * S3 SDK方式列出版本示例
 * @author sohan
 *
 */
public class S3Example_CreateGetListVersions {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;

		// Make sure the Versioning function is enabled, or an exception will appear.
		// Please refer to help on how to open the version： Managing a Tenant and Its Namespaces > Managing namespaces > Configuring a namespace > Configuring object versioning
		// Create some versions of object.
		// **Make sure that [Versioning] option was enabled in HCP system.**
		{
			for (int i = 0; i < 5; i++) {
				hs3Client.putObject(bucketName, key, file);
			}
		}

		{
			try {
				// Listing versions of this object
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				VersionListing verListing = hs3Client.listVersions(bucketName, key);
				//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

				// Printout objects
				List<S3VersionSummary> objs = verListing.getVersionSummaries();
				for (S3VersionSummary s3VersionSummary : objs) {
					System.out.println(s3VersionSummary.getVersionId() + "\t" + s3VersionSummary.getSize() + "\t" + s3VersionSummary.getETag() + "\t" + s3VersionSummary.getKey());
				}

				// Get specific version of object
				S3Object specificVersionOfs3Object = hs3Client.getObject(new GetObjectRequest(bucketName, key).withVersionId(objs.get(3).getVersionId()));
				// do something

			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}

}
