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
package com.hitachivantara.example.hcp.util;

import java.io.File;

public class Account {
	// ------------------------------------------------------------------------------------------------------
	 public final static String hcpdomain = "v91.hcp.com";
	 public final static String tenant = "tenant1";
	 public final static String endpoint = "tenant1.v91.hcp.com";
	 public final static String namespace = "ns1";
	// ------------------------------------------------------------------------------------------------------
//	 public final static String hcpdomain = "v82.hcpdemo.com";
//	 public final static String tenant = "tenant1";
//	 public final static String endpoint = "tenant1.v82.hcpdemo.com"; // "tenant1.hcp-demo.hcpdemo.com";// "tn9.hcp8.hdim.lab"; //
//	 public final static String namespace = "ns1";
	// ------------------------------------------------------------------------------------------------------
//	public final static String hcpdomain = "hcp.changhongx.com";
//	public final static String tenant = "tenant1";
//	public final static String endpoint = "tenant1.hcp.changhongx.com"; // "tenant1.hcp-demo.hcpdemo.com";// "tn9.hcp8.hdim.lab"; //
//	public final static String namespace = "ns2";
	// ------------------------------------------------------------------------------------------------------
	// The access key (user1) encoded by Base64
	public final static String accessKey = "YWRtaW4=";
	// The secret access key (hcp1234567) encrypted by MD5
	public final static String secretKey = "161ebd7d45089b3446ee4e0d86dbcf92";
	// ------------------------------------------------------------------------------------------------------
	public final static String system_accessKey = "YWRtaW4=";
	public final static String system_secretKey = "161ebd7d45089b3446ee4e0d86dbcf92";
	// ------------------------------------------------------------------------------------------------------
	public final static String HCP_AUTHORIZATION = "HCP " + accessKey + ":" + secretKey;
	// ------------------------------------------------------------------------------------------------------
	public static final File localFile1 = new File("C:\\Temp\\test.txt");
//	public static final File localFile1 = new File("C:\\VDisk\\DriverD\\Downloads\\Pic\\10.jpg");
	// ------------------------------------------------------------------------------------------------------
}
