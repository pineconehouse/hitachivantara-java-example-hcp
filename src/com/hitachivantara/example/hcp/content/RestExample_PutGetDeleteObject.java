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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.DigestUtils;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObject;

/**
 * Examples of how to upload, retrieve, delete object include creating a client side
 * 
 * @author sohan
 *
 */
public class RestExample_PutGetDeleteObject {

	public static void main(String[] args) throws IOException, HSCException {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Create an HCP access client. The client needs to be created only once
		HCPNamespace hcpClient = null;
		{
			String endpoint = Account.endpoint;
			String namespace = Account.namespace;
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);
			// clientConfig.setProxy("localhost", 8080);

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder.withClientConfiguration(clientConfig).withCredentials(new LocalCredentials(accessKey, secretKey))
					// WithAnonymous
					// .withCredentials(new AnonymousCredentials())
					.withEndpoint(endpoint).withNamespace(namespace).bulid();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		HCPObject hcpObject = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		{
			// Inject file into HCP system.
			try {
				// There is no need to create a directory before upload objects, but you can create a specific directory also
				// hcpClient.createDirectory("folder/subfolder/123");

				// hcpClient.deleteObject(key);

				// After the successful execution of PUT action, the file will be stored to the HCP specified location, and the SDK will automatically
				// verify the integrity of the content by default
				hcpClient.putObject(key, file);
				
				// Copy objects between namespaces
				// String sourceKey = null;
				// String sourceNamespace,targetNamespace;
				// String targetKey;
				// CopyObjectRequest request = new CopyObjectRequest()
				// // Full key of source file
				// .withSourceKey(sourceKey)
				// The bucket where the source file resides
				// .withSourceNamespace(sourceNamespace)
				// Whether to copy metadata
				// .withCopyingMetadata(true)
				// Whether to copy the historical version
				// .withCopyingOldVersion(false)
				// path algorithm
				// .withSourceKeyAlgorithm(sourceKeyAlgorithm)
				// Full path to target file
				// .withTargetKey(targetKey)
				// The bucket where the target file resides
				// .withTargetNamespace(targetNamespace);
				// hcpClient.copyObject(request);
				// System.out.println(result.getHashAlgorithmName());
				// System.out.println(result.getContentHash());
				// System.out.println(result.getETag());

				// Check whether object exist.
				// boolean exist = hcpClient.doesObjectExist(key);
				// assertTrue(exist == true);

				// Get the object from HCP
				hcpObject = hcpClient.getObject(key);
			} catch (InvalidResponseException e) {
				// e.getReason()
				// e.getStatusCode()
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

//		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//		// 获得数据流
//		// Get the stream
//		InputStream in = hcpObject.getContent();
//		// 可以将文件保存至本地目录
//		// StreamUtils.inputStreamToFile(in, "C:\\myfile.doc", true)
//
//		// 以下为验证上传数据与本地数据一致性测示例，SDK已集成此功能，实际开发时不需要以下代码！
//		{
//			byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
//			byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
//			in.close();
//
//			boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
//			assertTrue(equals == true);
//		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			hcpClient.deleteObject(key);
			// or
			// You can use Purge to delete objects
			// hcpClient.deleteObject(new DeleteObjectRequest(key).withPurge(true));
			//
			// Check whether object exist.
			// boolean exist = hcpClient.doesObjectExist(key);
			// assertTrue(exist == false);
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		System.out.println("Well done!");
	}

}
