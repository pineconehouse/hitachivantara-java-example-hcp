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

import org.dom4j.Document;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;
import com.hitachivantara.hcp.standard.util.MetadataUtils;

/**
 * Access custom Metadata example
 * 
 * @author sohan
 *
 */
public class RestExample_PutMetadata {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
//		String key = "example-hcp/subfolder1/" + file.getName()+"2";
		String key = "example-hcp/subfolder1/testFile4EncodingMeta_UTF8.txt";

		// Create a file for below metadata operation.
		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
				hcpClient.putObject(key, file);
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				S3CompatibleMetadata metadata = new S3CompatibleMetadata();
				metadata.put("name", "Rison Han");
				metadata.put("company", "hitachi vantara");
//				metadata.put("comment", "???????????????<utf-8>?????????");
//				metadata.put("file", "????????????");

				// Put S3 Compatible METADATA with specific key
				hcpClient.putMetadata(key, metadata);

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Create an XML Document object
				Document doc = RestExample_PutMetadataWithFile.createDocument();

				// Put Custom METADATA with specific key
				hcpClient.putMetadata(new PutMetadataRequest(key, "metadata2", MetadataUtils.toByteArray(doc)));
				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Get S3 compatible metadata from HCP
				S3CompatibleMetadata metadataFromHCP = hcpClient.getMetadata(key);
				// Verify contents.
				assertTrue("Rison Han".equals(metadataFromHCP.get("name")));
				assertTrue("hitachi vantara".equals(metadataFromHCP.get("company")));
//				assertTrue("???????????????<utf-8>?????????".equals(metadataFromHCP.get("comment")));

				// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

				// Get custom metadata from HCP
				HCPMetadata meta = hcpClient.getMetadata(key, "metadata2");
				String metadata2Content = StreamUtils.inputStreamToString(meta.getContent(), true);

				System.out.println("Metadata from " + key);
				System.out.println(metadata2Content);

				// ByteArrayOutputStream out = new ByteArrayOutputStream();
				// OutputFormat format = OutputFormat.createPrettyPrint(); // ??????????????????
				// format.setEncoding("UTF-8");
				// XMLWriter writer = new XMLWriter(out, format);
				// writer.write(doc);
				// assertTrue(metadata2Content.equalsIgnoreCase(out.toString()));

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Well done!");
		}
	}

}
