package dk.itu.spct.photorelay;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class RelayClient {
	@Id String id;
	ClientType type;
	
	public static String CreateIdFromRequest(HttpServletRequest req) {
		return req.getRemoteAddr() + ":" +req.getRemotePort();
	}
}

enum ClientType {
	TABLETOP,
	CUSTOMER
}