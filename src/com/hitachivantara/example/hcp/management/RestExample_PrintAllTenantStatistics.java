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
import com.hitachivantara.hcp.management.api.HCPSystemManagement;
import com.hitachivantara.hcp.management.model.ContentStatistics;
import com.hitachivantara.hcp.management.model.TenantSettings;

/**
 * Prints total capacity and usage statistics for all tenants
 * 
 * @author sohan
 *
 */
public class RestExample_PrintAllTenantStatistics {

	public RestExample_PrintAllTenantStatistics() {
	}

	public static void main(String[] args) throws HSCException {
		// Required to enable the Management functionality API and use the user with administrator permission
		HCPSystemManagement tenantClient = HCPClients.getInstance().getHCPSystemManagementClient();

		String[] tenants = tenantClient.listTenants();
		for (String tenant : tenants) {
			TenantSettings tenantSetting = tenantClient.getTenantSettings(tenant);
			ContentStatistics statistic = tenantClient.getTenantStatistics(tenant);

			System.out.println("--------------------------------------------------------------------------");
			// The name of the current tenant
			System.out.println("TenantName                   = " + tenant);
			// Total Tenant Capacity
			System.out.println("Total Capacity               = " + tenantSetting.getHardQuota() + " " + tenantSetting.getHardQuotaUnit());
			// The total number of objects in the bucket
			System.out.println("Object Count                 = " + statistic.getObjectCount());
			// Capacity information used
			System.out.println("UsedCapacityBytes            = " + FormatUtils.getPrintSize(statistic.getStorageCapacityUsed(), true));
			// Size and amount of custom metadata in the current bucket
			System.out.println("Custom Metadata Object Count = " + statistic.getCustomMetadataCount());
			System.out.println("Custom Metadata Object Bytes = " + FormatUtils.getPrintSize(statistic.getCustomMetadataSize(), true));
			// The number and size of objects in the bucket that are ready to be completely purged
			System.out.println("Shred Object Count           = " + statistic.getShredCount());
			System.out.println("Shred Object Bytes           = " + FormatUtils.getPrintSize(statistic.getShredSize(), true));

		}
	}

}
