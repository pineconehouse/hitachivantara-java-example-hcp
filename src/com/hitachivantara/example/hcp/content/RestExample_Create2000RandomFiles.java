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
import java.util.concurrent.CountDownLatch;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ParseException;
import com.amituofo.common.util.RandomUtils;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.ObjectParser;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;

/**
 * Create 2000 random content files using multiple threads
 * 
 * @author sohan
 *
 */
public class RestExample_Create2000RandomFiles {

	public static void main(String[] args) throws IOException, InterruptedException {
		final int maxi = 1000;
		final int maxj = 100;
		final CountDownLatch latch = new CountDownLatch(maxi * maxj);
		final HCPNamespace hcpClient;

		try {
			hcpClient = HCPClients.getInstance().getHCPClient();
		} catch (InvalidResponseException e) {
			e.printStackTrace();
			return;
		} catch (HSCException e) {
			e.printStackTrace();
			return;
		}

		// Here is the folder path you want to store files.
		final String directoryKey = "example-hcp/moreThan100objs/";

		// Create 100 random content files using multiple threads (10 threads create 200 each)
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
		for (int i = 0; i < maxi; i++) {
			final int id = i;

			new Thread(new Runnable() {
				final Long x = new Long(111030);
				int c = 0;

				@Override
				public void run() {
					long tid = Thread.currentThread().getId();

					for (int j = 0; j < maxj; j++) {
						String key = directoryKey + "file-" + id + "-" + j + ".txt";
						try {
							String cid = "(" + id + "-" + j + ")";
							// String content = cid + (new Date().toString() + " " + RandomInputStream.randomInt(10000, 99999));
							String content = cid + (new Date().toString() + " " + RandomUtils.randomString(RandomUtils.randomInt(1024, 10240)));

							PutObjectResult result = hcpClient.putObject(new PutObjectRequest(key).withContent(content));

							boolean exist = hcpClient.doesObjectExist(key);

							String contentOnHCP = hcpClient.getObject(key, new ObjectParser<String>() {

								@Override
								public String parse(HCPObject object) throws ParseException {
									try {
										return StreamUtils.inputStreamToString(object.getContent(), true);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return null;
								}
							});

							System.out.println(tid + " " + key + " " + result.getETag() + " " + ((exist && content.equals(contentOnHCP)) ? "Created" : "Failed to create!!!!!"));
							// if (!exist || !content.equals(contentOnHCP)) {
							// System.out.println(tid + " " + key + " " + result.getETag() + " Failed to create!!!!!");
							// }
							latch.countDown();
							//
							// // 延时获取数据
							// Timer timer = new Timer();
							// timer.schedule(new TimerTask() {
							//
							// @Override
							// public void run() {
							// HCPObject hcpobject;
							// String getContent;
							// try {
							// hcpobject = hcpClient.getObject(key);
							// getContent = StreamUtils.inputStreamToString(hcpobject.getContent(), true);
							//
							// String ETag1 = hcpobject.getETag();
							// String ETag2 = DigestUtils.calcMD5ToHex(getContent);
							// String ETag3 = DigestUtils.calcMD5ToHex(content);
							//
							// synchronized (x) {
							// if (ETag1.equalsIgnoreCase(ETag2) && ETag1.equalsIgnoreCase(ETag3)) {
							// // System.out.println(tid + " " + key + "Get OK " + (++c));
							// System.out.println(tid + key + "-> ETAG OK!<" + ETag1 + "><" + ETag2 + ">" + latch.getCount());
							// } else {
							// // System.out.println(tid + " " + key + "Get NG " + (++c));
							// System.out.println(tid + key + "-> ETAG NG!<" + ETag1 + "><" + ETag2 + ">!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							// }
							// }
							//
							// } catch (Exception e) {
							// e.printStackTrace();
							// } finally {
							// timer.cancel();
							// latch.countDown();
							// }
							// }
							// }, RandomUtils.randomInt(1, 10000));

						} catch (InvalidResponseException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

		latch.await();

		System.out.println("Well done!");
	}

}
