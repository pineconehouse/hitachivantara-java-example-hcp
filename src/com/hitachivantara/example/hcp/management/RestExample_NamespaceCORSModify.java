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

import static org.junit.Assert.assertTrue;

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.management.model.CORSSettings;

public class RestExample_NamespaceCORSModify {

	public RestExample_NamespaceCORSModify() {
	}

	public static void main(String[] args) throws HSCException {
		String ns = Account.namespace;
		// PREPARE TEST DATA ----------------------------------------------------------------------
		HCPTenantManagement namespaceClient = HCPClients.getInstance().getHCPTenantManagementClient();
		// PREPARE TEST DATA ----------------------------------------------------------------------

		// EXEC TEST FUNCTION ---------------------------------------------------------------------
		boolean deleted = namespaceClient.deleteNamespaceCORSSettings(ns);

		System.out.println("CORS Setting deleted:" + deleted);

		CORSSettings cors = namespaceClient.getNamespaceCORSSettings(ns);
		System.out.println("--------------------------------------------------------------");
		System.out.println("CORS Configuration:\n" + cors.getCORSConfiguration());
		System.out.println("--------------------------------------------------------------");

		cors.setCORSConfiguration("<CORSConfiguration>\n"
				+ " <CORSRule>\n"
				+ "   <AllowedOrigin>http://www.example.com</AllowedOrigin>\n"
				+ "   <AllowedMethod>PUT</AllowedMethod>\n"
				+ "   <AllowedMethod>POST</AllowedMethod>\n"
				+ "   <AllowedMethod>DELETE</AllowedMethod>\n"
				+ "   <AllowedHeader>*</AllowedHeader>\n"
				+ " </CORSRule>\n"
				+ " <CORSRule>\n"
				+ "   <AllowedOrigin>*</AllowedOrigin>\n"
				+ "   <AllowedMethod>GET</AllowedMethod>\n"
				+ " </CORSRule>\n"
				+ "</CORSConfiguration>");
		namespaceClient.changeNamespaceCORSSettings(ns, cors);

		CORSSettings cors1 = namespaceClient.getNamespaceCORSSettings(ns);
		System.out.println("--------------------------------------------------------------");
		System.out.println("New CORS Configuration:\n" + cors1.getCORSConfiguration());
		System.out.println("--------------------------------------------------------------");

		boolean deleted1 = namespaceClient.deleteNamespaceCORSSettings(ns);
		System.out.println("CORS Setting deleted:" + deleted1);

		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		// RESULT VERIFICATION --------------------------------------------------------------------
		assertTrue(deleted1 == true);
		// RESULT VERIFICATION --------------------------------------------------------------------

		// CLEAN ----------------------------------------------------------------------------------
		// CLEAN ----------------------------------------------------------------------------------
		System.out.println("Well done!");
	}
}
