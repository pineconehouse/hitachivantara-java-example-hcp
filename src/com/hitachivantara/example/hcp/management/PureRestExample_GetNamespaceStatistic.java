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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.Account;
import com.hitachivantara.example.hcp.util.SSLUtils;

/**
 * Obtaining statistical information
 * 
 * @author sohan
 *
 */
public class PureRestExample_GetNamespaceStatistic {

	public static void main(String[] args) throws MalformedURLException {
		// Configure the protocol type to be used
		//https://tenant1.hcp9.hcpdemo.com:9090/mapi/tenants/tenant1/namespaces/ns2/statistics?prettyprint

		final String protocol = "https";
		String resource_identifier = "tenants/" + Account.tenant + "/namespaces/" + Account.namespace + "/statistics";
		final String rest = protocol + "://" + Account.endpoint + ":9090/mapi/" + resource_identifier; // 要提交的目标地址
		final URL url = new URL(rest);
		HttpURLConnection connection = null;

		System.out.println(url);
		// ------------------------------------------------------------------------------------------------------------------------
		// Download object from HCP via REST API
		// Using a Namespace > HTTP > Working with objects and versions > Request contents
		// ------------------------------------------------------------------------------------------------------------------------
		try {
			connection = (HttpURLConnection) url.openConnection(); // 创建一个HTTP连接

			SSLUtils.trustAll((HttpsURLConnection) connection);

			connection.setRequestMethod("GET"); // 指定使用GET请求方式
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true); // 自动执行HTTP重定向
			connection.setRequestProperty("Authorization", Account.HCP_AUTHORIZATION); // 设置认证信息

			// 更多返回代码参见帮助
			// Using a Namespace > HTTP reference > HTTP return codes
			if (connection.getResponseCode() == 200) { // 判断是否响应成功
				InputStream in = connection.getInputStream();
				// 打印取得的数据并关闭in
				System.out.println("------------------------------------------------------------------");
				StreamUtils.inputStreamToConsole(in, true);
				System.out.println("\n------------------------------------------------------------------");
				// in.close();
			} else {
				System.out.println("Failed to retrieve info! " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect(); // 断开连接
				connection = null;
			}
		}

		System.out.println("Well done!");
	}

}
