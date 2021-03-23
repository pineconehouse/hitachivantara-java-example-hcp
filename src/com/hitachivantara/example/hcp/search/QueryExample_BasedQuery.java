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
package com.hitachivantara.example.hcp.search;

import java.io.IOException;
import java.util.List;

import com.amituofo.common.define.DatetimeFormat;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.kit.PrettyRecordPrinter;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.query.define.ObjectProperty;
import com.hitachivantara.hcp.query.define.Order;
import com.hitachivantara.hcp.query.model.ObjectQueryResult;
import com.hitachivantara.hcp.query.model.ObjectSummary;
import com.hitachivantara.hcp.query.model.QueryResult;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

/**
 * Demonstration of basic metadata search function based on HCP MEQ
 * 
 * @author sohan
 *
 */
public class QueryExample_BasedQuery {
	int i = 0;

	private HCPNamespace hcpClient;
	private HCPQuery hcpQuery;
	private final PrettyRecordPrinter printer = new PrettyRecordPrinter();

	public static void main(String[] args) throws IOException {
		QueryExample_BasedQuery example = new QueryExample_BasedQuery();

		try {
			example.init();

			// query execution
			example.query();
		} catch (InvalidResponseException e) {
			e.printStackTrace();
			return;
		} catch (HSCException e) {
			e.printStackTrace();
			return;
		}
	}

	public void init() throws HSCException {
		// Get the instance of the search client
		hcpQuery = HCPClients.getInstance().getHCPQueryClient();
		// Get an HCP client instance
		hcpClient = HCPClients.getInstance().getHCPClient();
	}

	public void query() throws InvalidResponseException, HSCException, IOException {

		// Create a search request
		ObjectBasedQueryRequest request = new ObjectBasedQueryRequest();

		// For more information on search syntax, see the <HCP Search Interfaces Introduction.doc>
		// Search for files that in namespace.
		request.setQuery("+(namespace:\"ns1.tenant1\")");// +(namespace:"ns1.tenant1")
		// request.setQuery("+(customMetadataContent:110223201009028931)");
		// request.setQuery("+(customMetadataContent:male) +(objectPath:beijing)");

		// Set the result sort, where the object injection time ascending order is installed
		request.addSort(ObjectProperty.ingestTime, Order.asc);
		// request.addSort(ObjectProperty.size); //Default is ASC if not specified
		// Set the number of results per page, here set to 100
		request.setResults(100);
		// Beging offset
		// requestBody.setOffset(10);

		// Sets the return result column, which is ignored here
		// If no result column specified, only（changeTimeMilliseconds/key/name/urlName/versionId/operation） will be included
		// request.addProperty(ObjectProperty.accessTime);
		// request.addProperty(ObjectProperty.accessTimeString);
		// request.addProperty(ObjectProperty.acl);
		// request.addProperty(ObjectProperty.aclGrant);
		// request.addProperty(ObjectProperty.changeTimeMilliseconds);
		// request.addProperty(ObjectProperty.changeTimeString);
		// request.addProperty(ObjectProperty.customMetadata);
		// request.addProperty(ObjectProperty.customMetadataAnnotation);
		// request.addProperty(ObjectProperty.dpl);
		// request.addProperty(ObjectProperty.gid);
		// request.addProperty(ObjectProperty.hash);
		// request.addProperty(ObjectProperty.hashScheme);
		// request.addProperty(ObjectProperty.hold);
		// request.addProperty(ObjectProperty.index);
		request.addProperty(ObjectProperty.ingestTime);
		// request.addProperty(ObjectProperty.ingestTimeString);
		// request.addProperty(ObjectProperty.namespace);
		request.addProperty(ObjectProperty.objectPath);
		// request.addProperty(ObjectProperty.operation);
		// request.addProperty(ObjectProperty.owner);
		// request.addProperty(ObjectProperty.permissions);
		// request.addProperty(ObjectProperty.replicated);
		// request.addProperty(ObjectProperty.replicationCollision);
		// request.addProperty(ObjectProperty.retention);
		// request.addProperty(ObjectProperty.retentionClass);
		// request.addProperty(ObjectProperty.retentionString);
		// request.addProperty(ObjectProperty.shred);
		request.addProperty(ObjectProperty.size);
		// request.addProperty(ObjectProperty.type);
		// request.addProperty(ObjectProperty.uid);
		// request.addProperty(ObjectProperty.updateTime);
		// request.addProperty(ObjectProperty.updateTimeString);
		request.addProperty(ObjectProperty.urlName);
		// request.addProperty(ObjectProperty.utf8Name);
		// request.addProperty(ObjectProperty.version);

		// Facet
		// request.addFacet(Facet.namespace);
		// request.addFacet(Facet.hold);
		// request.addFacet(Facet.retention);
		// request.addFacet(Facet.retentionClass);

		ObjectQueryResult result = null;
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		// Trigger the search
		result = hcpQuery.query(request);

		// Processing search results
		handleResult(result);

		// Determine if there's another page
		while (result.isIncomplete()) {
			// System.out.println(request.getRequestBody().build());

			// long s = System.currentTimeMillis();
			// If there is a Nextpage search Nextpage
			result = hcpQuery.query(request.withNextPage());

			// long e = System.currentTimeMillis();
			// System.out.println(e-s);

			// Processing search results
			handleResult(result);
		}

		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
		// Search the previous page for results
		// result = hcpQuery.query(request.withPrevPage());
		//
		// handleResult(result);
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	}

	private void handleResult(final QueryResult result) throws InvalidResponseException, HSCException, IOException {
		long totalFound = result.getStatus().getTotalResults();
		System.out.println("Total matched items:" + totalFound);

		List<ObjectSummary> res = result.getResults();
		for (ObjectSummary objectSummary : res) {
			// The results of the search are processed here and are printed here as an example
			printer.appendRecord(++i,
					objectSummary.getKey(),
					DatetimeFormat.YYYY_MM_DD_HHMMSS.format(objectSummary.getIngestTime()),
					objectSummary.getVersionId(),
					objectSummary.getUrlName());
		}

		printer.printout();
	}

}
