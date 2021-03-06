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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;
import com.hitachivantara.hcp.standard.util.MetadataUtils;

/**
 * Access custom Metadata example
 * 
 * @author sohan
 *
 */
public class RestExample_PutMetadataWithFile {

	public static void main(String[] args) throws IOException {
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		{
			try {
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				S3CompatibleMetadata metadata = new S3CompatibleMetadata();
				metadata.put("name", "Rison");
				metadata.put("company", "hitachi vantara");

				// ????????????XML Document??????
				Document doc = createDocument();

				// Inject file with 2 pattern of metadata into HCP system.
				hcpClient.putObject(new PutObjectRequest(key, file).withMetadata(metadata).withMetadata("moreInfo", MetadataUtils.toByteArray(doc)));

				// Get metadata from HCP
				S3CompatibleMetadata metadataFromHCP = hcpClient.getMetadata(key);
				HCPMetadata meta = hcpClient.getMetadata(key, "moreInfo");
				String xmlContent = StreamUtils.inputStreamToString(meta.getContent(), true);

				// Verify contents.
				assertTrue("Rison".equals(metadataFromHCP.get("name")));
				assertTrue("hitachi vantara".equals(metadataFromHCP.get("company")));

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputFormat format = OutputFormat.createPrettyPrint(); // ??????????????????
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(out, format);
				writer.write(doc);
				assertTrue(xmlContent.equalsIgnoreCase(out.toString()));

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

	/**
	 * ????????????XML Document??????
	 * 
	 * @return
	 */
	public static Document createDocument() {
		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("result");
		root.addElement("code").addText("1");
		Element data = root.addElement("data");

		Element person1 = data.addElement("person");
		person1.addElement("name").setText("??????");
		person1.addElement("id").setText("1");
		person1.addElement("url").setText("http://192.168.191.1:9999/TestWeb/c7fe21616d2a5e2bd1e84bd453a5b30f.jpg");
		Element courses1 = person1.addElement("courses");
		Element course1 = courses1.addElement("course");
		course1.addElement("courseName").setText("??????");
		course1.addElement("courseMarks").setText("90");
		course1.addElement("courseId").setText("1");
		Element course2 = courses1.addElement("course");
		course2.addElement("courseName").setText("??????");
		course2.addElement("courseMarks").setText("80");
		course2.addElement("courseId").setText("2");
		Element course3 = courses1.addElement("course");
		course3.addElement("courseName").setText("??????");
		course3.addElement("courseMarks").setText("70");
		course3.addElement("courseId").setText("3");

		Element person2 = data.addElement("person").addAttribute("name", "??????").addAttribute("id", "2").addAttribute("url",
				"http://192.168.191.1:9999/TestWeb/4052858c526002a712ef574ccae1948f.jpg");
		person2.addElement("course").addAttribute("courseName", "??????").addAttribute("courseMarks", "91").addAttribute("courseId", "1");
		person2.addElement("course").addAttribute("courseName", "??????").addAttribute("courseMarks", "82").addAttribute("courseId", "1");
		person2.addElement("course").addAttribute("courseName", "??????").addAttribute("courseMarks", "73").addAttribute("courseId", "1");

		return document;
	}

}
