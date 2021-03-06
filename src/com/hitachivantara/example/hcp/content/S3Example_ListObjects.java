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
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * S3 lists directories including examples of subdirectories
 * 
 * @author sohan
 *
 */
public class S3Example_ListObjects {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String bucketName = Account.namespace;

		// ??????????????????object??????list
		// Prepare some objects for list.
//		{
//			for (int i = 0; i < 5; i++) {
//				String key = "Folder/moreThan100objs" + i + ".doc";
//				hs3Client.putObject(bucketName, key, file);
//			}
//
//			for (int i = 0; i < 10; i++) {
//				String key = "Folder/moreThan100objs/L2TestObject" + i + ".doc";
//				hs3Client.putObject(bucketName, key, file);
//			}
//		}

		{
			long i = 0;
			try {
				// Here is the folder path you want to list.
				String directoryKey = "";//"example-hcp/moreThan100objs/";

				// Request HCP to list all the objects in this folder.
//				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey));
//				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey).withDelimiter("/"));
				ObjectListing objlisting = hs3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(directoryKey));//.withDelimiter("/"));

				// Printout objects
				do {
					List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
					for (S3ObjectSummary obj : objs) {
						System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getETag() + "\t" + obj.getKey());
					}
					objlisting = hs3Client.listNextBatchOfObjects(objlisting);
				} while (objlisting.isTruncated());
				
				// Printout remain items
				List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
				for (S3ObjectSummary obj : objs) {
					System.out.println(++i + "\t" + obj.getSize() + "\t" + obj.getETag() + "\t" + obj.getKey());
				}
				
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
