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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amituofo.common.ex.HSCException;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.build.HCPQueryClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.management.api.HCPSystemManagement;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

public class HCPClients {

	private static HCPClients instance = new HCPClients();

	private AmazonS3 hs3Client = null;
	private HCPNamespace hcpClient = null;
	private HCPQuery hcpQueryClient = null;

	private HCPTenantManagement tenantMgrClient;

	private HCPSystemManagement systemMgrClient;

	private HCPClients() {
	}

	public static HCPClients getInstance() {
		return instance;
	}

	public AmazonS3 getS3Client() {
		if (hs3Client == null) {
			// Create s3 client
			// ?????????????????????HCP ?????? ??? ???
			String endpoint = Account.endpoint;
			// ????????????????????????
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// ?????????????????????
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			// ?????????Client????????????????????????????????????????????????????????????????????????????????????????????????????????????Client???????????????
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			hs3Client = newS3Client(endpoint, accessKey, secretKey);
		}

		return hs3Client;
	}

	public HCPNamespace getHCPClient() throws HSCException {
		if (hcpClient == null) {
			// ?????????????????????HCP ?????? ??? ???
			String endpoint = Account.endpoint;
			String namespace = Account.namespace;
			// ????????????????????????
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// ?????????????????????
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			// ?????????Client????????????????????????????????????????????????????????????????????????????????????????????????????????????Client???????????????
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			hcpClient = newHCPClient(endpoint, namespace, accessKey, secretKey);
		}

		return hcpClient;
	}

	public HCPNamespace newHCPClient(String endpoint, String namespace, String accessKey, String secretKey) throws HSCException {
		ClientConfiguration clientConfig = new ClientConfiguration();
		// clientConfig.setConnectTimeout(2000);
		// Using HTTP protocol
		clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTP);
		// clientConfig.setDefaultMaxConnectionsPerRoute(20);
		// clientConfig.setMaxConnections(20);

		// clientConfig.setProxy("localhost", 8080);
		// clientConfig.setProxyUsername(proxyUsername);

		// InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
		// dnsResolver.setUnsolvableException(true);
		// dnsResolver.add("cloud.tn9.hcp8.hdim.lab", "10.129.214.75");
		// dnsResolver.add("admin.hcp8.hdim.lab", "10.129.214.75");
		//// dnsResolver.add("tn9.hcp8.hdim.lab", "10.129.214.75");
		// myClientConfig.setDnsResolver(dnsResolver);
		// dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.61");
		// dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.62");
		// dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.63");
		// dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.64");

		HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
		HCPNamespace hcpClient = builder.withClientConfiguration(clientConfig)
				.withCredentials(new LocalCredentials(accessKey, secretKey))
				.withEndpoint(endpoint)
				.withNamespace(namespace).bulid();

		return hcpClient;
	}

	public AmazonS3 newS3Client(String endpoint, String accessKey, String secretKey) {
		com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
		// Using HTTP protocol
		clientConfig.setProtocol(com.amazonaws.Protocol.HTTP);
		// clientConfig.setSignerOverride("AWS4SignerType");
		clientConfig.setSignerOverride("S3SignerType");

		// clientConfig.setProxyHost("localhost");
		// clientConfig.setProxyPort(8080);
		// clientConfig.setMaxConnections(maxConnections);

		AmazonS3 hs3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfig).withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).build();

		return hs3Client;
	}

	public HCPTenantManagement getHCPTenantManagementClient() throws HSCException {
		if (tenantMgrClient == null) {
			// ?????????????????????HCP ?????? ??? ???
			String hcpdomain = Account.hcpdomain;
			String tenant = Account.tenant;
			// ????????????????????????
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// ?????????????????????
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
			// myClientConfig1.setProxy("localhost", 8080);
			// myClientConfig1.ignoreSslVerification();
			// myClientConfig1.ignoreHostnameVerification();

//			myClientConfig1.setSupportedCipherSuites(new String[] {"TLS_AES_128_GCM_SHA256","TLS_AES_256_GCM_SHA384"});
//			myClientConfig1.setSupportedProtocols(new String[] { "TLSv1.2", "TLSv1.1" });
//	        String[] defaultCiphers = ((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).getDefaultCipherSuites();
//	        String[] availableCiphers = ((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).getSupportedCipherSuites();
//	        myClientConfig1.setSupportedCipherSuites(availableCiphers);

			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			// ?????????Client????????????????????????????????????????????????????????????????????????????????????????????????????????????Client???????????????
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			tenantMgrClient = HCPClientBuilder.tenantManagementClient()
					.withEndpoint(hcpdomain)
					.withTenant(tenant)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withClientConfiguration(myClientConfig1).bulid();

		}

		return tenantMgrClient;
	}

	public HCPSystemManagement getHCPSystemManagementClient() throws HSCException {
		if (systemMgrClient == null) {
			// ?????????????????????HCP ?????? ??? ???
			String hcpdomain = Account.hcpdomain;
			// ????????????????????????
			// The access key encoded by Base64
			String accessKey = Account.system_accessKey;
			// ?????????????????????
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.system_secretKey;

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			// ?????????Client????????????????????????????????????????????????????????????????????????????????????????????????????????????Client???????????????
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			systemMgrClient = HCPClientBuilder.systemManagementClient().withEndpoint(hcpdomain).withCredentials(new LocalCredentials(accessKey, secretKey))
					.withClientConfiguration(myClientConfig1).bulid();

		}

		return systemMgrClient;
	}

	public HCPQuery getHCPQueryClient() throws HSCException {
		if (hcpQueryClient == null) {
			// ?????????????????????HCP ?????? ??? ???
			String endpoint = Account.endpoint;
			// ????????????????????????
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// ?????????????????????
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(com.hitachivantara.core.http.Protocol.HTTP);

			HCPQueryClientBuilder builder = HCPClientBuilder.queryClient();
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			// ?????????Client????????????????????????????????????????????????????????????????????????????????????????????????????????????Client???????????????
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
			hcpQueryClient = builder.withClientConfiguration(clientConfig).withCredentials(new LocalCredentials(accessKey, secretKey)).withEndpoint(endpoint).bulid();
		}

		return hcpQueryClient;
	}

}
