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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContextBuilder;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amituofo.common.util.DigestUtils;
import com.hitachivantara.example.hcp.util.Account;

/**
 * Example of S3 HTTPS to circumvent SSL authentication
 * @author sohan
 *
 */
public class S3Example_UsingHttpsProtocol {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = null;
		{
			// Create s3 client
			String endpoint = Account.endpoint;
			// The access key encoded by Base64
			String accessKey = Account.accessKey;
			// The AWS secret access key encrypted by MD5
			String secretKey = Account.secretKey;

			com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
			// Using HTTPS protocol
			clientConfig.setProtocol(Protocol.HTTPS);
			clientConfig.setSignerOverride("S3SignerType");

			// Fully trust
			//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			try {
				SSLContextBuilder builder = new SSLContextBuilder();
				builder.loadTrustMaterial(null, new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
						return true;
					}
				});

				SSLConnectionSocketFactory sslsf = null;
//				sslsf = new SSLConnectionSocketFactory(
//						builder.build(),
////						new String[] { "TLSv1","TLSv1.1","TLSv1.2" }, // For Java 1.7
//						new String[] { "TLSv1" },// For Java1.6-1.7
//						new String[] { "TLS_RSA_WITH_AES_128_CBC_SHA"},
//						NoopHostnameVerifier.INSTANCE);
				
				sslsf = new SSLConnectionSocketFactory(
						builder.build(),
						NoopHostnameVerifier.INSTANCE);
				
				clientConfig.getApacheHttpClientConfig().setSslSocketFactory(sslsf);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			hs3Client = AmazonS3ClientBuilder.standard()
					.withClientConfiguration(clientConfig)
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
					.build();
		}
		
		S3Object s3Object = null;
		// Here is the file will be uploaded into HCP
		File file = Account.localFile1;
		// The location in HCP where this file will be stored.
		String key = "example-hcp/subfolder1/" + file.getName();
		String bucketName = Account.namespace;
		
		{
			try {
				// Inject file into HCP system.
				hs3Client.putObject(bucketName, key, file);

				// Check whether object exist.
				boolean exist = hs3Client.doesObjectExist(bucketName, key);
				assertTrue(exist == true);

				// Get the object from HCP
				s3Object = hs3Client.getObject(bucketName, key);
			} catch (AmazonServiceException e) {
				e.printStackTrace();
				return;
			} catch (SdkClientException e) {
				e.printStackTrace();
				return;
			}
		}

		// ↓↓↓=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↓↓↓
		// Verify result:
		S3ObjectInputStream in = s3Object.getObjectContent();
		byte[] orginalFileMd5 = DigestUtils.calcMD5(file);
		byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
		in.close();

		boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
		assertTrue(equals == true);
		// ↑↑↑=*=*=* CODE JUST FOR DEMONSTRATE, UNNECESSARY IN PRODUCTION ENVIRONMENT *=*=*=↑↑↑

		{
			// Delete object in HCP.
			hs3Client.deleteObject(bucketName, key);
			
			// Check whether object exist.
			boolean exist = hs3Client.doesObjectExist(bucketName, key);
			assertTrue(exist == false);
		}

		System.out.println("Well done!");
	}

}

//private final static String[] Java16_CipherSuite = new String[] { 
//		"SSL_RSA_WITH_RC4_128_MD5",
//		"SSL_RSA_WITH_RC4_128_SHA",
//		"TLS_RSA_WITH_AES_128_CBC_SHA",
//		"TLS_RSA_WITH_AES_256_CBC_SHA",
//		"TLS_ECDH_ECDSA_WITH_RC4_128_SHA",
//		"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
//		"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA",
//		"TLS_ECDH_RSA_WITH_RC4_128_SHA",
//		"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
//		"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA",
//		"TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",
//		"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
//		"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
//		"TLS_ECDHE_RSA_WITH_RC4_128_SHA",
//		"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
//		"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
//		"TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
//		"TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
//		"TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
//		"TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
//		"SSL_RSA_WITH_3DES_EDE_CBC_SHA",
//		"TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA",
//		"TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",
//		"TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
//		"TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
//		"SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
//		"SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
//		"SSL_RSA_WITH_DES_CBC_SHA",
//		"SSL_DHE_RSA_WITH_DES_CBC_SHA",
//		"SSL_DHE_DSS_WITH_DES_CBC_SHA",
//		"SSL_RSA_EXPORT_WITH_RC4_40_MD5",
//		"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
//		"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
//		"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA",
//		"TLS_EMPTY_RENEGOTIATION_INFO_SCSV",
//		"SSL_RSA_WITH_NULL_MD5",
//		"SSL_RSA_WITH_NULL_SHA",
//		"TLS_ECDH_ECDSA_WITH_NULL_SHA",
//		"TLS_ECDH_RSA_WITH_NULL_SHA",
//		"TLS_ECDHE_ECDSA_WITH_NULL_SHA",
//		"TLS_ECDHE_RSA_WITH_NULL_SHA",
//		"SSL_DH_anon_WITH_RC4_128_MD5",
//		"TLS_DH_anon_WITH_AES_128_CBC_SHA",
//		"TLS_DH_anon_WITH_AES_256_CBC_SHA",
//		"SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
//		"SSL_DH_anon_WITH_DES_CBC_SHA",
//		"TLS_ECDH_anon_WITH_RC4_128_SHA",
//		"TLS_ECDH_anon_WITH_AES_128_CBC_SHA",
//		"TLS_ECDH_anon_WITH_AES_256_CBC_SHA",
//		"TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA",
//		"SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",
//		"SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",
//		"TLS_ECDH_anon_WITH_NULL_SHA",
//		"TLS_KRB5_WITH_RC4_128_SHA",
//		"TLS_KRB5_WITH_RC4_128_MD5",
//		"TLS_KRB5_WITH_3DES_EDE_CBC_SHA",
//		"TLS_KRB5_WITH_3DES_EDE_CBC_MD5",
//		"TLS_KRB5_WITH_DES_CBC_SHA",
//		"TLS_KRB5_WITH_DES_CBC_MD5",
//		"TLS_KRB5_EXPORT_WITH_RC4_40_SHA",
//		"TLS_KRB5_EXPORT_WITH_RC4_40_MD5",
//		"TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA",
//		"TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5",
//		"SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5",
//		"SSL_RSA_WITH_IDEA_CBC_SHA",
//		"SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA",
//		"SSL_DH_DSS_WITH_DES_CBC_SHA",
//		"SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA",
//		"SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA",
//		"SSL_DH_RSA_WITH_DES_CBC_SHA",
//		"SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA",
//		"SSL_FORTEZZA_DMS_WITH_NULL_SHA",
//		"SSL_FORTEZZA_DMS_WITH_FORTEZZA_CBC_SHA",
//		"SSL_RSA_EXPORT1024_WITH_DES_CBC_SHA",
//		"SSL_DHE_DSS_EXPORT1024_WITH_DES_CBC_SHA",
//		"SSL_RSA_EXPORT1024_WITH_RC4_56_SHA",
//		"SSL_DHE_DSS_EXPORT1024_WITH_RC4_56_SHA",
//		"SSL_DHE_DSS_WITH_RC4_128_SHA",
//		"NETSCAPE_RSA_FIPS_WITH_3DES_EDE_CBC_SHA",
//		"NETSCAPE_RSA_FIPS_WITH_DES_CBC_SHA",
//		"SSL_RSA_FIPS_WITH_DES_CBC_SHA",
//		"SSL_RSA_FIPS_WITH_3DES_EDE_CBC_SHA",
//		"TLS_KRB5_WITH_IDEA_CBC_SHA",
//		"TLS_KRB5_WITH_IDEA_CBC_MD5",
//		"TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA",
//		"TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5"};
