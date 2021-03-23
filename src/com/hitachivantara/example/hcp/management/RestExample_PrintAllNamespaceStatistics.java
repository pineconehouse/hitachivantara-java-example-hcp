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
package com.hitachivantara.example.hcp.management;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.FormatUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.define.QuotaUnit;
import com.hitachivantara.hcp.management.model.ContentStatistics;
import com.hitachivantara.hcp.management.model.NamespaceSettings;

/**
 * Print the capacity and usage of all buckets
 * 
 * @author sohan
 *
 */
public class RestExample_PrintAllNamespaceStatistics {

	public RestExample_PrintAllNamespaceStatistics() {
	}

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();

		String[] namespaces = namespaceClient.listNamespaces();
		for (String namespace : namespaces) {
			NamespaceSettings namespaceSetting;
			ContentStatistics statistic;
			try {
				namespaceSetting = namespaceClient.getNamespaceSettings(namespace);
				statistic = namespaceClient.getNamespaceStatistics(namespace);
				System.out.println("--------------------------------------------------------------------------");
				// The name of the current bucket
				System.out.println("NamespaceName                = " + namespace);
				// Bucket capacity
				System.out.println("Total Capacity               = " + namespaceSetting.getHardQuota() + " " + namespaceSetting.getHardQuotaUnit());
				// The total number of objects in the bucket
				System.out.println("Object Count                 = " + statistic.getObjectCount());
				// Capacity used
				System.out.println("Used Capacity Bytes          = " + FormatUtils.getPrintSize(statistic.getStorageCapacityUsed(), true));
				// Percentage used
				System.out.println("Used Capacity Percent        = " + FormatUtils.getPercent(statistic.getStorageCapacityUsed() / (double) hardQuotaToBytes(namespaceSetting), 1));
				// Size and amount of custom metadata in the current bucket
				System.out.println("Custom Metadata Object Count = " + statistic.getCustomMetadataCount());
				System.out.println("Custom Metadata Object Bytes = " + FormatUtils.getPrintSize(statistic.getCustomMetadataSize(), true));
				// The number and size of objects in the bucket that are ready to be completely purged
				System.out.println("Shred Object Count           = " + statistic.getShredCount());
				System.out.println("Shred Object Bytes           = " + FormatUtils.getPrintSize(statistic.getShredSize(), true));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("--------------------------------------------------------------------------");
				System.out.println("Error when get information from" + namespace);
				System.out.println("--------------------------------------------------------------------------");
			}
		}
	}

	private static double hardQuotaToBytes(NamespaceSettings namespaceSetting) {
		long cardinalNumber = 1;
		if (namespaceSetting.getHardQuotaUnit() == QuotaUnit.GB) {
			cardinalNumber = 1024 * 1024 * 1024;
		} else if (namespaceSetting.getHardQuotaUnit() == QuotaUnit.TB) {
			cardinalNumber = 1024 * 1024 * 1024 * 1024;
		}

		return (namespaceSetting.getHardQuota() * cardinalNumber);
	}

}
