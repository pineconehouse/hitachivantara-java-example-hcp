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

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ParseException;
import com.amituofo.common.util.StreamUtils;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.api.MetadataParser;
import com.hitachivantara.hcp.standard.api.event.ListObjectHandler;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.metadata.Annotation;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.ListObjectRequest;

/**
 * Lists all the objects in the specified directory, including sub directories
 * 
 * @author sohan
 *
 */
public class RestExample_ListAllMetadataFromFolder {

	public static void main(String[] args) throws IOException {
		{
			try {
				final HCPNamespace hcpClient = HCPClients.getInstance().getHCPClient();

				// Here is the folder path you want to list.
				String directoryKey = "example-hcp/moreThan100objs/";

				// Request HCP to list all the objects in this folder.
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ListObjectRequest request = new ListObjectRequest(directoryKey)
						// Specifies that list folder recursively
						.withRecursiveDirectory(true)
						// You can Filter objects by setting a Filter
//						.withObjectFilter(new ObjectFilter() {
//
//							@Override
//							public boolean accept(HCPObjectEntry arg0) {
//								// Only objects whose names contain the letter X are accept
//								return arg0.getName().contains("X");
//							}
//						})
						;
				
				// list ojbects
				hcpClient.listObjects(request, new ListObjectHandler() {

					@Override
					public NextAction foundObject(HCPObjectSummary objectSummary) throws HSCException {
						Annotation[] metas = objectSummary.getCustomMetadatas();
						if (metas != null) {
							System.out.println("\n-------------------------------------------------------------------------------");
							System.out.println("Key=" + objectSummary.getKey());
							for (final Annotation meta : metas) {
								
								hcpClient.getMetadata(objectSummary.getKey(), meta.getName(), new MetadataParser<String>() {

									@Override
									public String parse(HCPMetadata metadata) throws ParseException {
										try {
											System.out.println("\nContent of Metadata [" + meta.getName() + "]:");
											StreamUtils.inputStreamToConsole(metadata.getContent(), true);
											System.out.println();
										} catch (IOException e) {
											e.printStackTrace();
										}
										return null;
									}
								});
								
//								or
//								HCPMetadata hcpmeta = hcpClient.getMetadata(objectSummary.getKey(), meta.getName());
//								try {
//									System.out.println("\nContent of Metadata [" + meta.getName()+"]:");
//									StreamUtils.inputStreamToConsole(hcpmeta.getContent(), true);
//									System.out.println();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
							}
						}

						return null;
					}
				});
				// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			} catch (InvalidResponseException e) {
				e.printStackTrace();
				return;
			} catch (HSCException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
