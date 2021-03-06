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

import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.core.http.HttpResponse;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.core.http.client.HttpClientBuilder;
import com.hitachivantara.core.http.client.impl.SimpleHttpClientBuilder;
import com.hitachivantara.core.http.content.HttpEntity;
import com.hitachivantara.core.http.content.StringEntity;
import com.hitachivantara.core.http.model.HttpForm;
import com.hitachivantara.core.http.model.HttpHeader;
import com.hitachivantara.core.http.model.HttpParameter;
import com.hitachivantara.core.http.util.HttpClientAgency;
import com.hitachivantara.core.http.util.HttpUtils;

public class HCIAuth {

	public HCIAuth() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ClientConfiguration configuration = new ClientConfiguration();
		HttpClientBuilder builder = new SimpleHttpClientBuilder(configuration);
		HttpClientAgency client = new HttpClientAgency(builder);

		HttpForm form = new HttpForm();
		HttpHeader header = null;
		HttpParameter param = null;
		HttpEntity entity = new StringEntity("body");
		
		form.put("grant_type", "password");
		form.put("username", "admin");
		form.put("password", "P@ssw0rd");
		form.put("scope", "*");
		form.put("client_secret", "hci-client");
		form.put("client_id", "hci-client");
		// curl -ik -X POST ST https://10.129.215.95:8000/auth/oauth/ -d/ -d grant_type=password -d username=admin -d password=P@ssw0rd -d scope=*
		// -d client_secret=hci-client -d client_id=hci-client
		HttpResponse response = client.post("https://10.129.215.95:8000/auth/oauth", header, param, form, entity);
		HttpUtils.printHttpResponse(response, false);
//		StreamUtils.inputStreamToConsole(response.getEntity().getContent(), true);
		String token = StreamUtils.inputStreamToString(response.getEntity().getContent(), true);
		System.out.println(token);
	}

}
