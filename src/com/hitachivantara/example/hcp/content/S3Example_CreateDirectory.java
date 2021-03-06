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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amituofo.common.util.URLUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.RandomInputStream;

/**
 * Show how to create a directory! There is no need to create a directory by default!
 * 
 * @author sohan
 *
 */
public class S3Example_CreateDirectory {

	public static void main(String[] args) throws IOException {
		// The location in HCP where this file will be stored.
		final String directoryKey = "example-hcp/subfolder" + RandomInputStream.randomInt(100, 999);
		String bucketName = Account.namespace;

		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		createFolder(bucketName, directoryKey, hs3Client);

		System.out.println("Well done!");
	}

	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, URLUtils.catPath(folderName, "/"), emptyContent, metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

}
