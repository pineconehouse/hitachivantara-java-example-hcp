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
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploadException;

/**
 * S3 multipart upload example
 * @author sohan
 *
 */
public class S3Example_HCPMultipartUpload2 {
	public static void main(String[] args) throws MulitipartUploadException {
		final AmazonS3 s3Client = HCPClients.getInstance().getS3Client();

		final int PART_SIZE = 10 * 1024 * 1024; // Set part size to 10 MB.

		final File tobeUploadFile = new File("D:\\Downloads\\Soft\\mysql-8.0.20-winx64.zip");
		final String objectPath = "hcp-test/" + tobeUploadFile.getName() + "6";
		final String bucketName = Account.namespace;

		// ==========================================================================================================================
		MultipartUploadListing mpoListing = s3Client.listMultipartUploads(new ListMultipartUploadsRequest(bucketName));
		List<MultipartUpload> ups = mpoListing.getMultipartUploads();
		for (MultipartUpload multipartUpload : ups) {
			System.out.println(multipartUpload.getKey()+"\t"+multipartUpload.getUploadId());
		}
		
		InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, objectPath));
		String uploadId = initResult.getUploadId();
		for (int partNumber = 1; partNumber < 4; partNumber++) {
			s3Client.uploadPart(new UploadPartRequest()
					.withKey(objectPath)
					.withBucketName(bucketName)
					.withPartNumber(partNumber)
					.withFile(tobeUploadFile)
					.withPartSize(PART_SIZE)
					.withUploadId(uploadId)
					);
			System.out.println(partNumber);
		}
		
		PartListing partListing = s3Client.listParts(new ListPartsRequest(bucketName, objectPath, uploadId));
		List<PartSummary> parts = partListing.getParts();
		for (PartSummary partSummary : parts) {
			System.out.println(partSummary.getPartNumber()+"\t"+partSummary.getSize()+"\t"+partSummary.getETag());
		}
		// =========================================================================================================================
		// =========================================================================================================================

	}

}
