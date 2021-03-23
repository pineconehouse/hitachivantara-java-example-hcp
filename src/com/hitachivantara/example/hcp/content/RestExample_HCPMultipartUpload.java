package com.hitachivantara.example.hcp.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.DateUtils;
import com.amituofo.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.MultipartUpload;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.PartETag;
import com.hitachivantara.hcp.standard.model.request.impl.MultipartUploadRequest;

/**
 * Demonstrates how to upload large files via the HCP SDK multipart upload function 
 * 
 * @author sohan
 *
 */
public class RestExample_HCPMultipartUpload {
	public static void main(String[] args) throws IOException, HSCException, NoSuchAlgorithmException {
		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
		// Large files for testing (** file size at least 500MB, small files are not recommended to be uploaded by multipart **)
		final File file = new File("D:\\Downloads\\Soft\\mysql-8.0.20-winx64.zip");
		final String key = "hcp-test1/" + file.getName();

		if (hcpClient.doesObjectExist(key)) {
			hcpClient.deleteObject(key);
		}

		String uploadId = null;
		MultipartUploadRequest request = new MultipartUploadRequest(key);
		final MultipartUpload api = hcpClient.getMultipartUpload(request);

		// Initialize
		uploadId = api.initiate();

		System.out.println("key=" + key);
		System.out.println("uploadId=" + uploadId);

		final List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());

		final long length = file.length();
		// The minimum part size is 5MB. 100MB is recommended. If the file size is smaller than 5GB, multipart uploading is not recommended
		final long partLength = 1024 * 1024 * 8; // Min 5M
		long remainLength = length;
		long startOffset = 0;

		int index = 1;
		final Queue<long[]> parts = new LinkedList<long[]>();
		while (remainLength > 0) {
			long uploadLength = Math.min(remainLength, partLength);

			parts.add(new long[] { index++, startOffset, uploadLength });

			startOffset += (uploadLength + 0);
			remainLength -= uploadLength;
		}
		final int partsSize = parts.size();

		final CountDownLatch latch = new CountDownLatch(partsSize);

		for (int i = 0; i < partsSize; i++) {
			final int id = i;
			new Thread(new Runnable() {
				public void run() {
					InputStream in = null;
					try {
						long[] part = null;
						synchronized (parts) {
							part = parts.poll();
						}

						if (part != null) {
							in = new FileInputStream(file);
							in.skip(part[1]);

							System.out.println("Uploading Part... " + id);
							PartETag etag = api.uploadPart((int) part[0], in, part[2]).getPartETag();
							System.out.println("etag=" + etag.getPartNumber() + " " + etag.getETag());
							partETags.add(etag);
							System.out.println("Part... " + id + " Done");
						} else {
							System.out.println("null");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
							}
						}
					}

					latch.countDown();
				}

			}// .run();
			).start();
		}

		try {
			latch.await();

			// Merge uploaded parts
			api.complete(partETags);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		// RESULT VERIFICATION --------------------------------------------------------------------
//		try {
//			HCPObject obj = hcpClient.getObject(key);
//			String destMd5 = DigestUtils.format2Hex(DigestUtils.calcMD5(obj.getContent()));
//			// Calculate the file's MD5 value
//			final String orgMd5 = DigestUtils.format2Hex(DigestUtils.calcMD5(file));
//
//			System.out.println("orgMd5=" + orgMd5 + "Length=" + file.length());
//			System.out.println("desMd5=" + destMd5 + "Length=" + obj.getSize());
//		} catch (HSCException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// RESULT VERIFICATION --------------------------------------------------------------------
	}
}
