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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amituofo.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploadException;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploaderExecutor;
import com.hitachivantara.example.hcp.util.multipartupload.UploadEventHandler;

/**
 * S3 multipart upload example
 * @author sohan
 *
 */
public class S3Example_HCPMultipartUpload {
	public static void main(String[] args) throws MulitipartUploadException {
		final AmazonS3 s3Client = HCPClients.getInstance().getS3Client();

		final int PART_SIZE = 10 * 1024 * 1024; // Set part size to 10 MB.

//		final File tobeUploadFile = new File("C:\\VDisk\\DriverD\\Downloads\\Libs\\tika-app-1.7.jar");
//		final String objectPath = "hcp-test/" + tobeUploadFile.getName() + "6";
		
		final File tobeUploadFile = new File("D:\\Downloads\\Soft\\mysql-8.0.20-winx64.zip");
		final String objectPath = "hcp-test/" + tobeUploadFile.getName();

		final String bucketName = Account.namespace;

		if(s3Client.doesObjectExist(bucketName, objectPath)) {
			s3Client.deleteObject(bucketName, objectPath);
		}
		// ==========================================================================================================================
		MulitipartUploaderExecutor exec = new MulitipartUploaderExecutor(s3Client, bucketName, objectPath, tobeUploadFile, PART_SIZE);
		// ???????????????????????????10???????????????,???????????????10??????
		exec.multiThreadUpload(10,
				/**
				 * ????????????????????????
				 * 
				 * @author sohan
				 *
				 */
				new UploadEventHandler() {
					private final PrintStream log = System.out;

					@Override
					public void init(String bucketName, String objectPath, String uploadId) {
						log.println("Step 1: Initialize [" + objectPath + "] [" + uploadId + "]");
					}

					@Override
					public void beforePartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime) {
						log.println("Step 2: Upload parts... [" + objectPath + "] [" + uploadId + "] " + partNumber + " " + uploadPartsize);
					}

					@Override
					public void caughtPartUploadException(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, Exception e) {
						log.println("Step 2: Upload parts Error [" + objectPath + "] [" + uploadId + "] " + partNumber + " " + uploadPartsize);
						e.printStackTrace();

						// **???????????????????????????????????????????????????????????????**
						// Do something
					}

					@Override
					public void afterPartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime, long endTime) {
						log.println("Step 2: Upload parts OK ["
								+ objectPath
								+ "] ["
								+ uploadId
								+ "] "
								+ partNumber
								+ " "
								+ uploadPartsize
								+ "\t??????:"
								+ (((double) (endTime - startTime)) / 1000)
								+ " sec");
					}

					@Override
					public void complete(String bucketName, String objectPath, String uploadId, Long uploadedSize, long startTime, long endTime) {
						log.println("Step 3: Complete... [" + objectPath + "] [" + uploadId + "]");

						// ???????????????????????????MD5??????????????????????????????
						// **??????????????????????????????????????????-???????????????????????????**
						try {
							S3Object s3Object = s3Client.getObject(bucketName, objectPath);
							S3ObjectInputStream in = s3Object.getObjectContent();
							byte[] orginalFileMd5;
							orginalFileMd5 = DigestUtils.calcMD5(tobeUploadFile);
							byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
							in.close();

							boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
							assertTrue(equals == true);
							System.out.println("***Upload " + objectPath + " Successfully!***");
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}
				});
		// =========================================================================================================================

		// =========================================================================================================================
		// // ???????????????????????????
		// // ??????????????????????????????id??????
		// String uploadId = "xxxxx";
		// MulitipartUploaderExecutor exec2 = new MulitipartUploaderExecutor(s3Client, bucketName, objectPath, tobeUploadFile, PART_SIZE, uploadId);
		// // ??????????????????????????????????????????=?????????3
		// exec2.uploadPart(3);
		// // ????????????
		// exec2.complete();
		// =========================================================================================================================

	}

}
