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

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

/**
 * Examples of how to upload object by stream
 * 
 * @author sohan
 *
 */
public class RestExample_PutObjectWithStream {

	public static void main(String[] args) throws IOException, HSCException {
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Create an HCP access client. The client needs to be created only once
		HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/file_upload_by_stream.txt";

		{
			// Inject file into HCP system.
			InputStream in = null;
			try {
				in = getInputStream();
			
				hcpClient.putObject(key, in);
			} catch (InvalidResponseException e) {
//				e.getReason()
//				e.getStatusCode()
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//Close the data stream
				StreamUtils.close(in);
			}
		}

		System.out.println("Well done!");
	}
	
	private static InputStream getInputStream() throws IOException {
		byte[] bytes = null;
		bytes = "This text will be saved to the HCP as an object file".getBytes();
		return StreamUtils.bytesToInputStream(bytes);
	}

}
