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

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.NamespaceStatistics;

/**
 * Example of statistics bucket, Get information such as total capacity, used capacity, number of objects, etc.
 * 
 * @author sohan
 *
 */
public class RestExample_NamespaceStatistics {

	public static void main(String[] args) throws IOException {

		{
			try {
				// Get the HCP client instance
				HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Gets statistics for the default namespace
				NamespaceStatistics statistics = hcpClient.getNamespacesStatistics();
				// Gets statistics info of specific namespace
				// hcpClient.getNamespacesStatistics("namespaceName");

				// The name of the current bucket
				System.out.println("NamespaceName = " + statistics.getNamespaceName());
				// The total number of objects in the bucket
				System.out.println("ObjectCount = " + statistics.getObjectCount());
				// The total capacity of the bucket
				System.out.println("TotalCapacityBytes = " + FormatUtils.getPrintSize(statistics.getTotalCapacityBytes(), true));
				// Capacity used
				System.out.println("UsedCapacityBytes = " + FormatUtils.getPrintSize(statistics.getUsedCapacityBytes(), true));
				// Size and amount of custom metadata in the current bucket
				System.out.println("CustomMetadataObjectCount = " + statistics.getCustomMetadataObjectCount());
				System.out.println("CustomMetadataObjectBytes = " + FormatUtils.getPrintSize(statistics.getCustomMetadataObjectBytes(), true));
				System.out.println("ShredObjectBytes = " + FormatUtils.getPrintSize(statistics.getShredObjectBytes(), true));
				System.out.println("ShredObjectCount = " + statistics.getShredObjectCount());
				// Default suota
				System.out.println("SoftQuotaPercent = " + statistics.getSoftQuotaPercent());

				// The error message returned by HCP can be obtained by catching an InvalidResponseException
			} catch (InvalidResponseException e) {
				// The error code returned
				e.getStatusCode();
				// Summary of the cause of the error
				e.getReason();
				// Details of the circumstances that caused the error
				e.getMessage();
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
