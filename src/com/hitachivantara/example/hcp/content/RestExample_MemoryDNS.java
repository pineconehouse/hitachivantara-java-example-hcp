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

import com.amituofo.common.ex.HSCException;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.core.http.client.impl.InMemoryDnsResolver;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.RandomInputStream;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * Example of key path best practices for HCP storage
 * 
 * @author sohan
 *
 */
public class RestExample_MemoryDNS {

	public static void main(String[] args) throws IOException, HSCException, InterruptedException {
		final HCPNamespace hcpClient;
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// Create s3 client
			String endpoint = "tn1.hcpvm.bjlab.poc";
			String namespace = "song1";
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			ClientConfiguration clientConfig = new ClientConfiguration();
			// Using HTTP protocol
			clientConfig.setProtocol(Protocol.HTTP);

			// create built-in DNS resolution instance
			InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
			// If set to true, an unresolvable URL will trigger Exception. If set to false, MemoryDNS will be used first. Attempt
			// to continue parsing by operation system
			dnsResolver.setUnsolvableException(true);
			// Add the parse address
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.61");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.62");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.63");
			dnsResolver.add("song1.tn1.hcpvm.bjlab.poc", "10.129.215.64");
			clientConfig.setDnsResolver(dnsResolver);

			HCPNamespaceClientBuilder builder = HCPClientBuilder.defaultHCPClient();
			hcpClient = builder
					.withClientConfiguration(clientConfig)
					.withCredentials(new LocalCredentials(accessKey, secretKey))
					.withEndpoint(endpoint)
					.withNamespace(namespace)
					.bulid();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		{
			// Here is the folder path you want to store files.
			final String directoryKey = "example-hcp/moreThan100objs/";

			// Create 100 random content files using multiple threads (10 threads create 200 each)
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			for (int i = 0; i < 1; i++) {
				final int id = i;

				new Thread(new Runnable() {
					@Override
					public void run() {
						PutObjectResult result = null;
						for (int j = 0; j < 2000; j++) {
							String key = directoryKey + "file-" + id + "-" + j + ".txt";
							try {
								String content = new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999);

								PutObjectRequest req = new PutObjectRequest(key).withContent(content);
								// req.customHeader().put("Connection", "Keep-alive");
								// req.customHeader().put("Connection", "close");
								result = hcpClient.putObject(req);

								// InputStream in = hcpClient.getObject(key).getContent();
								// try {
								// StreamUtils.inputStream2None(in, true);
								// } catch (IOException e) {
								// // TODO Auto-generated catch block
								// e.printStackTrace();
								// }

								System.out.println(key);
							} catch (InvalidResponseException e) {
								System.out.println("Create file: " + key + " " + result.getETag());
								e.printStackTrace();
							} catch (HSCException e) {
								System.out.println("Create file: " + key + " " + result.getETag());
								e.printStackTrace();
								// } catch (IOException e) {
								// System.out.println("Create file: " + key + " " + result.getETag());
								// e.printStackTrace();
							}
						}
						System.out.println("Finished 2000 " + this);
					}
				}).start();
			}
		}

		Thread.sleep(Integer.MAX_VALUE);

		System.out.println("Well done!");
	}

}
