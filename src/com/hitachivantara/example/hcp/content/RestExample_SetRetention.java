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

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.Retention;
import com.hitachivantara.hcp.standard.model.metadata.HCPSystemMetadata;

/**
 * An example of setting system metadata
 * 
 * @author sohan
 *
 */
public class RestExample_SetRetention {

	public static void main(String[] args) throws IOException {
		HCPNamespace hcpClient;

		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();

		{
			try {
				hcpClient = HCPClients.getInstance().getHCPClient();

				// 如果对象在retention模式下或者hold为true，对象无法被覆盖
				hcpClient.putObject(key, file);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// Modify system metadata
		{
			try {

				HCPSystemMetadata metadata = new HCPSystemMetadata();
				// Set retention period
				// Offset syntax
				// To use an offset as a retention setting, specify a standard expression that conforms to this syntax:
				// ^([RAN])?([+-]\d+y)?([+-]\d+M)?([+-]\d+w)?([+-]\d+d)?([+-]\d+h)?([+-]\d+m)?([+-]\d+s)?

				// metadata.setRetention(new Retention("A+60d+20m"));
				// Retention对象文件时间+5分钟
				metadata.setRetention(new Retention("A+5m"));
				// Retention对象文件时间+60天（也就是60天后可以删除）
				// metadata.setRetention(new Retention("A+60d"));
				hcpClient.setSystemMetadata(key, metadata);
			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		// Attempts to delete objects in RETENTION will be failed
		{
			try {
				hcpClient.deleteObject(key);
			} catch (InvalidResponseException e) {
				System.out.println("Unable to delete object under retention.");
				// e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println("Well done!");
	}

}
