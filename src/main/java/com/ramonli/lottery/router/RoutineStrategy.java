package com.ramonli.lottery.router;

/**
 * <code>RoutineStrategy</code> will be used as a key to route request. In
 * general case, the server side will prepare a request handler method map(use
 * <code>RoutineStrategy</code> as key, and value will be handler method and
 * handler object itself) first. When received client's request, a
 * <code>RoutineStrategy</code> will be constructed from request, then lookup
 * the handler method by this key. So any implementations of this interface must
 * override <code>equals()</code> and <code>hashCode()</code> methods to
 * guarantee that it can be used as a map key.
 * 
 * @author Ramon Li
 */
public interface RoutineStrategy {

	/**
	 * Construct a <code>RoutineStrategy</code> from annotation of
	 * <code>RequestMap</code>. Also you can implement your own
	 * <code>RequestMap</code> by extending it, while the default
	 * <code>RequestMap</code> can already fit most use cases.
	 * <p>
	 * For example you can define the value as a json string, then parse it to a
	 * POJO, actually by this means, you can handle any requirements.
	 */
	void from(RequestMap requestMap);

}
