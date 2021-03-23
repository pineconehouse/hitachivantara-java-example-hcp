package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.HCPClients;

/**
 * 通过于预签名可以生成临时访问链接，达到无需密钥访问数据的需求
 * 
 * @author sohan
 *
 */
public class S3Example_GeneratePresignedUrlStaticWeb {

	public static void main(String[] args) throws IOException {
		AmazonS3 hs3Client = HCPClients.getInstance().getS3Client();

		// Here is the file will be uploaded into HCP
		// String objectKey = "/7-Zip/apache-tomcat-7.0.78/RUNNING.txt";
		String objectKey = "MailContent2/fyqnicm2387910-p4_1516953617157.shtml";// "/7-Zip/apache-tomcat-7.0.78/lib/catalina.jar";
		// The location in HCP where this file will be stored.
		String bucketName = Account.namespace;

		// 公开链接有效期截至时间
		// Add 1 minute.
		Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 1);

		// 通过预签名读取对象
		{
			ResponseHeaderOverrides responeHeader = new ResponseHeaderOverrides().withContentType("text/html; charset=utf-8").withContentDisposition("inline;");
			// 生成预签名时间
			URL urlForGet = hs3Client.generatePresignedUrl(
					new GeneratePresignedUrlRequest(bucketName, objectKey).withResponseHeaders(responeHeader).withExpiration(expiration).withMethod(HttpMethod.GET));
			System.out.println("-------------------Get Object Content by URL--------------------");
			System.out.println(urlForGet.toString());

			// HttpURLConnection connection = (HttpURLConnection) urlForGet.openConnection();
			// connection.setDoInput(true);
			// connection.setRequestMethod("GET");
			// InputStream in = connection.getInputStream();
			// StreamUtils.inputStreamToConsole(in, true);
			// connection.disconnect();
			//
			// System.out.println();
			System.out.println("----------------------------------------------------------------");
		}

	}

}
