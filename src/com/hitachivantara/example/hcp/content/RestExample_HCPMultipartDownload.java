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
import java.io.PrintStream;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ParseException;
import com.amituofo.common.util.DigestUtils;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.example.hcp.util.multipartupload.MulitipartUploadException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.ObjectParser;
import com.hitachivantara.hcp.standard.api.event.PartialHandlingListener;
import com.hitachivantara.hcp.standard.internal.FileWriteHandler;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.request.impl.GetObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.MultipartDownloadRequest;

/**
 * Download large files through HCP SDK is demonstrated. Large files can significantly improve bandwidth utilization, but the performance of
 * local disk is higher
 * 
 * @author sohan
 *
 */
public class RestExample_HCPMultipartDownload {
	public static final PrintStream console = System.out;

	public static void main(String[] args) throws MulitipartUploadException, HSCException, InterruptedException, IOException {

		// PREPARE TEST DATA ----------------------------------------------------------------------
		// Download to a local disk
		final String f1path = "C:\\temp\\anyconnect-win-4.7.01076-predeploy-k9.zip-1";
		// Download to the local file (for verification, just for example)
		final String f2path = "C:\\temp\\anyconnect-win-4.7.01076-predeploy-k9.zip-2";

		File file1 = new File(f1path);
		if (file1.exists()) {
			file1.delete();
		}

		// Please upload a large file at least 50MB before testing
		String key = "hcp-test/anyconnect-win-4.7.01076-predeploy-k9.zip";

		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

		final CountDownLatch latch = new CountDownLatch(2);
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		final long b = Calendar.getInstance().getTimeInMillis();
		MultipartDownloadRequest request = new MultipartDownloadRequest(key)
				// The minimum value of this parameter is 50MB
				// The minimum file size to enable multipart download is 100MB, Only the file size greater than 100MB will enable multipart download
				.withMinimumObjectSize(1024 * 1024 * 100)
				// Specifies the number of part
				.withParts(3)
				// Whether to wait
				.withWaitForComplete(false);

		FileWriteHandler handler = new FileWriteHandler(file1);
		// Whether to overwrite local file
		handler.setOverrideLocalFile(true);
		// Unable to verify content for multipart uploaded files, must be false
		handler.setVerifyContent(false);
		// Download listening can be configured
		handler.setListener(new PartialHandlingListener() {
			double size = 0;

			public void catchException(HSCException e) {
				e.printStackTrace();
			}

			public void completed() {
				long e = Calendar.getInstance().getTimeInMillis();
				double time = e - b;

				double mbs = (size / 1024 / 1024) / (time / 1000);
				console.println("completed speed=" + mbs + "MB/s " + mbs * 8 + "Mbps/s");

				latch.countDown();
			}

			public void partCompleted(int partNumber, long beginOffset, long length) {
				size += length;
				console.println("partCompleted= " + partNumber + " " + beginOffset + " " + ((double) length) / 1024 / 1024);
			}

			public void outProgress(int id, long seekOffset, long length) {
				// console.println("progress=" + id + " " + seekOffset + " " + length);
			}
		});
		// Execute the download
		hcpClient.getObject(request, handler);
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		latch.countDown();
		latch.await();

		// Verify the content of download file. (This code is an demonstrate only)
		// RESULT VERIFICATION --------------------------------------------------------------------
		File file2 = hcpClient.getObject(new GetObjectRequest(key), new ObjectParser<File>() {

			@Override
			public File parse(HCPObject object) throws ParseException {
				File file2 = new File(f2path);
				if (file2.exists()) {
					file2.delete();
				}
				try {
					StreamUtils.inputStreamToFile(object.getContent(), file2, true);
				} catch (IOException e) {
					throw new ParseException(e);
				}
				return file2;
			}

		});
		// DigestUtils.isMd5Equals(file1, file2);
		String file1_Md5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file1));
		String file2_Md5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file2));

		System.out.println("file1_Md5=" + file1_Md5);
		System.out.println("file2_Md5=" + file2_Md5);

		// RESULT VERIFICATION --------------------------------------------------------------------
	}

}
