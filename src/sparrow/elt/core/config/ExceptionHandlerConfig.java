package sparrow.elt.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.jaxb.EXCEPTIONHANDLERType;
import sparrow.elt.jaxb.HANDLERType;
import sparrow.elt.jaxb.HANDLEType;
import sparrow.elt.jaxb.ERRORType;
import sparrow.elt.jaxb.HANDLERType;
import sparrow.elt.jaxb.HANDLEType;


public interface ExceptionHandlerConfig {

	abstract Map getHandlers();

	abstract Map getHandles();

}

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Saji Venugopalan
 * @version 1.0
 */
final class ExceptionHandlerConfigImpl implements ExceptionHandlerConfig {

	private Map handlers = null;

	private Map handles = null;

	/**
	 * 
	 * @param handler
	 *            EXCEPTIONHANDLERType
	 */
	ExceptionHandlerConfigImpl(EXCEPTIONHANDLERType handler) {
		handlers = new HashMap();
		handles = new HashMap();
		bindDefault();
		if (handler != null) {
			bind(handler.getHANDLER(), handler.getHANDLE());
		}
	}

	/**
	 * 
	 * 
	 */
	private void bindDefault() {
		List dHandlers = new ArrayList();

		Map hndlrs = SparrowUtil.getImplConfig("handler");

		for (int i = 1; hndlrs.containsKey(i + ".name"); i++) {
			String name = (String) hndlrs.get(i + ".name");
			String klass = (String) hndlrs.get(i + ".class");
			HANDLERType hndlrTypeImpl = new HANDLERType();
			hndlrTypeImpl.setCLASS(klass);
			hndlrTypeImpl.setNAME(name);
			dHandlers.add(hndlrTypeImpl);
		}
		// *********************************************************************
		List dHandles = new ArrayList();

		Map hndls = SparrowUtil.getImplConfig("handle");

		for (int i = 1; hndls.containsKey(i + ".exception.class"); i++) {
			String exceptionClass = (String) hndls.get(i + ".exception.class");
			String handler = (String) hndls.get(i + ".hander.name");

			HANDLEType htimpl = new HANDLEType();
			htimpl.setEXCEPTION(exceptionClass);
			htimpl.setHANDLER(handler);
			for (int j = 1; hndls.containsKey(i + ".error." + j + ".code"); j++) {
				String code = (String) hndls.get(i + ".error." + j + ".code");
				String desc = (String) hndls.get(i + ".error." + j + ".desc");
				String type = (String) hndls.get(i + ".error." + j + ".type");
				
				ERRORType error = new ERRORType();
				error.setCODE(code);
				error.setVALUE(desc);
				error.setTYPE(type);
				htimpl.getERROR().add(error);
				
			}
			dHandles.add(htimpl);
		}
		// *********************************************************************
		
		bind(dHandlers,dHandles);
		
	}


	/**
	 * getHandlers
	 * 
	 * @return List
	 */
	public Map getHandlers() {
		return handlers;
	}

	/**
	 * getHandles
	 * 
	 * @return List
	 */
	public Map getHandles() {
		return handles;
	}

	/**
	 * 
	 * @param subQuery
	 *            SubQuery
	 */
	private void addHandlers(String name, ExcepHandler handler) {
		handlers.put(name, handler);
	}

	/**
	 * 
	 * @param
	 */
	private void addHandle(String expClassName, Handle handle) {
		handles.put(expClassName, handle);
	}

	/**
	 * 
	 * @param hndlrs
	 *            List
	 * @param hndls
	 *            List
	 */
	private void bind(List hndlrs, List hndls) {

		if (!hndlrs.isEmpty()) {
			for (Iterator iter = hndlrs.iterator(); iter.hasNext();) {
				HANDLERType item = (HANDLERType) iter.next();
				addHandlers(item.getNAME(), new ExceptionHandlerImpl(item));
			}
		}

		if (!hndls.isEmpty()) {
			for (Iterator iter = hndls.iterator(); iter.hasNext();) {
				HANDLEType item = (HANDLEType) iter.next();
				addHandle(item.getEXCEPTION(), new HandleImpl(item));
			}
		}

	}


}
