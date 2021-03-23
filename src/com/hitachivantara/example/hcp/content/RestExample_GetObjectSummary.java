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
import java.util.Date;

import com.amituofo.common.define.DatetimeFormat;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;

/**
 * Gets the object summary information only, Not include content body.
 * 
 * @author sohan
 *
 */
public class RestExample_GetObjectSummary {

	public static void main(String[] args) throws IOException {

		{
			try {
				// Get the HCP client instance
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Gets summary information for the current object
				 HCPObjectSummary summary = hcpClient.getObjectSummary("example-hcp/moreThan100objs/file-1-0.txct");

				// Gets summary information for the specified version of object
//				HCPObjectSummary summary = hcpClient.getObjectSummary(new CheckObjectRequest()
//						// 指定对象Key
//						.withKey("folder/subfolder/WeChat Image_20180716111626.doc")
//						// 获取已经删除的对象信息（当开启版本功能，并通过DeleteObject功能删除对象后）
//						.withDeletedObject(true)
//						// 指定版本，获取过去版本的对象摘要
//						.withVersionId("1212121341313")
//						// 取得其他namespace的对象（当前登录用户需要拥有此namespace的访问权限）
//						.withNamespace("otherNamespace"));
				
				// The following information is available in the HCPObjectSummary
				// getChangeTime()
				// getContentHash()
				// getDomain()
				// getDpl()
				// getETag()
				// getHashAlgorithmName()
				// getIngestProtocol()
				// getIngestTime()
				// getKey()
				// getMetadata()
				// getName()
				// getOwner()
				// getPosixGroupIdentifier()
				// getPosixUserID()
				// getRetention()
				// getRetentionClass()
				// getRetentionString()
				// getSize()
				// getType()
				// getVersionId()

				// getContentLength()
				// getContentType()
				// getDate()
				// getHcpTime()
				// getHcpVersion()
				// getRequestId()
				// getResponseCode()
				// getServer()
				// getServicedBySystem()

				System.out.println("Name = " + summary.getName());
				if (summary.isDirectory()) {
					System.out.println("Is folder.");
				} else if (summary.isObject()) {
					System.out.println("Size = " + FormatUtils.getPrintSize(summary.getSize()));
					System.out.println("IngestTime = " + DatetimeFormat.ISO8601_DATE_FORMAT.format(new Date(summary.getIngestTime())));
				}

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
