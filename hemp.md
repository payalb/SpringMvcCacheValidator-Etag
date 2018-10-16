Enables efficient resource updates i.e. no data transfer takes place if the resource doesn’t change.'

 90 seconds after the initial fetch of an asset, initiates the browser a new request (the exact same asset). The browser looks up the local cache and finds the previously cached response but cannot use it because it’s expired. This is the point where the browser requests the full content from the server. The problem with it this is that if the resource hasn’t changed, there is absolutely no reason for downloading the same asset that is already in the CDN cache.

Validation tokens are solving this problem. The edge server creates and returns arbitrary tokens, that are stored in the ETag header field, which are typically a hash or other fingerprints of content of existing files. Clients don’t need to know how the tokens are generated but need to send them to the server on subsequent requests. If the tokens are the same then resources haven’t changed thus downloads can be skipped.

The web browser provides the ETag token automatically within the “If-None-Match” HTTP request header. The server then checks tokens against current assets in the cache. A 304 Not Modified response will tell the browser if an asset in the cache hasn’t been changed and therefore allowing a renewal for another 90 seconds. It’s important to note that these assets don’t need to be downloaded again which saves bandwidth and time.

Browsers do most (if not) all the work for web developers. For instance, they automatically detect if validation tokens have been previously specified and appending them to outgoing requests and updating cache timestamps as required based on responses from servers. Web developers are therefore left with one job only which is ensuring servers provide the required ETag tokens. KeyCDN’s edge servers fully support ETags

	Last-Modified header If-Modified-Since
The Last-Modified header indicates the time a document last changed which is the most common validator. It can be seen as a legacy validator from the time of HTTP/1.0. When a cache stores an asset including a Last-Modified header, it can utilize it to query the server if that representation has changed over time (since it was last seen). This can be done using an If-Modified-Since request header fiel

	An HTTP/1.1 origin server should send both, the ETag and the Last-Modified value
	
Etag: 
HttpHeader

1) used for caching
2) for conditional requests

get : /caching

will get back Etag: "shgdsg7734jdjh387464dh" with response

when next time we send request, we send conditional request
	
	if-* header
	
	if-none-match if-match
	
	server returns 304-not modified if not changed
	
send another request => If-None-Match: "shgdsg7734jdjh387464dh" (etag we got last time)

if etag doesn't match, we have older version of resource, shud return new version

Response:
Http1.1 304 Not Modified
Etag: "shgdsg7734jdjh387464dh"

So lightweight response. 
No more data going back and forth if data not changing


	Shallow implementation
Request gets processed as normal. when server marshalls response and calculates hash over the response, it will make decision at very end whether to send data or not modified status
Most of work serevr has to do , request gets fully processed. So not of much help. BUt data not getting transferred if etag matches.

ShallowEtageHeaderFilter: add etag header on body of response. 
In web.xml/webapplicationinitializer

	@Bean("eTagFilter")
	public ShallowEtagHeaderFilter getEtag() {
		return new ShallowEtagHeaderFilter();
	}
	In web.xml
		
		javax.servlet.FilterRegistration.Dynamic filterOne=servletContext.addFilter("eTagFilter", new DelegatingFilterProxy("eTagFilter"));
		filterOne.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
		

	
	deep implementation
Request uses etag during processing. It determines based on etag if resource has changed, if not, it skips retrieving it entirely. WE need to integrate with our application the response.


The ETag generation is done using MD5 algorithm which is a hash function to produce 128 bit hash value.

ETag value validation
Validation of ETag value is nothing but comparing the two values (the one which received in request header 'If-None-match' and the one which is currently representing the resource). There are two validation approaches.

Weak Validation: The two resource representations are semantically equivalent, e.g. some of the content differences are not important from the business logic perspective e.g. current date displayed on the page might not be important for updating the entire resource for it.
The syntax for weak validation:

ETag: W/"<etag_value>" 
Note that this directive is entirely used for the server side logic and has no importance to the client browser.

Strong Validation: The two resource representations are byte-for-byte identical. This is the default one and no special directive is used for it.


How it works?
Followings are the general high level steps where response header 'ETag' along with conditional request header 'If-None-Match' is used to cache the resource copy in the client browser:

Server receives a normal HTTP request for a particular resource, say XYZ. 

The server side prepares the response. The server side logic wants the browser to cache XYZ locally. By default all browsers always cache the resources (specification) so no special header in the response is needed. 

Server includes the header 'ETag' with it's value in the response:
 ETag: "version1"

Server sends the response with above header, content of XYZ in the body and with the status code 200. The browser renders the resource and at the same time caches the resource copy along with header information.

Later the same browser makes another request for the same resource XYZ. with following conditional request header:
If-None-Match: "version1"

On receiving the request for XYZ along with 'If-None-Match' header, the server side logic checks whether XYZ needs a new copy of the resource by comparing the current value of the ETag identifier on the server side and the one which is received in the request header.
If request's If-None-Match is same as currently generated/assigned value of ETag on the server, then status code 304 (Not Modified) with the empty body is sent back and the browser uses cached copy of XYZ.
If request's If-None-Match value doesn't match the currently generated/assigned value of ETag (say "version2") for XYZ then server sends back the new content in the body along with status code 200. The 'ETag' header with the new value is also included in the response. The browser uses the new XYZ and updates its cache with the new data.