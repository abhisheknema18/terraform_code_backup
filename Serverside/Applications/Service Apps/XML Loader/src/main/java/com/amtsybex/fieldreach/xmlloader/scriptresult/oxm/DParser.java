package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.amtsybex.fieldreach.utils.impl.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//29329 - Update XML-Loader to decrypt file data items
/**
 * Parse and validate the encrypted Script Result file into memory.
 * 
 * 
 * An error occurs decrypting the result set object.
 * 
 * @throws UnsupportedEncodingException
 */
public class DParser {

	private final Logger log = LoggerFactory.getLogger(DParser.class.getName());

	public ResultSet decryptResultSet(ResultSet resultSet) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Encrypted Result Set Start");
			
		}
		if (resultSet != null) {
			
			decryptGeneral(resultSet);
			decryptResults(resultSet.getScriptResults().getRESULTS());
			decryptCarryThrough(resultSet.getScriptResults().getCARRYTHROUGH());
			
			//decryptHk(resultSet.getScriptResults().getHK());
			resultSet.getScriptResults().setScriptCode(Common.decryptStr(resultSet.getScriptResults().getScriptCode()));
			resultSet.getScriptResults().setScriptCodeId(Common.decryptStr(resultSet.getScriptResults().getScriptCodeId()));
			resultSet.getScriptResults().setItemCount(resultSet.getScriptResults().getItemCount());
			resultSet.getScriptResults().setOnlineDate(Common.decryptStr(resultSet.getScriptResults().getOnlineDate()));
			resultSet.getScriptResults().setScriptId(Common.decryptStr(resultSet.getScriptResults().getScriptId()));
			resultSet.getScriptResults().setVersionNo(Common.decryptStr(resultSet.getScriptResults().getVersionNo()));
			resultSet.getScriptResults().setItemCount(Common.decryptStr(resultSet.getScriptResults().getItemCount()));
			
		}
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Encrypted Result Set End");
			
		}
		return resultSet;
	}

	/**
	 * Get the 'GENERAL' element of a Result Set object and decrypt it
	 * 
	 * @param resultSet
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void decryptGeneral(ResultSet resultSet) throws UnsupportedEncodingException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse General Section of Result Set Start");
			
		}

		if (resultSet.getScriptResults().getPROFILE().getGENERAL() != null) {
			for (String elementName : resultSet.getScriptResults().getPROFILE().getGENERAL().getElementNames()) {

				// Check if a value exists in the Extended section.

				// Value exists in the Extended section, now check to
				// see if there is a value
				// in the General section. If there isn't, use the value
				// found in the Extended
				// section.
				Class<?> cls;
				try {
					cls = Class.forName("com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.General");

					Method getter = cls.getDeclaredMethod("get" + elementName);

					String generalVal = null;
					String dGeneralVal = null;
					generalVal = (String) getter.invoke(resultSet.getScriptResults().getPROFILE().getGENERAL());
					if (generalVal != null) {
						dGeneralVal = Common.decryptStr(generalVal);
					} else {
						continue;
					}

					Method setter = cls.getDeclaredMethod("set" + elementName, new Class[] { String.class });

					setter.invoke(resultSet.getScriptResults().getPROFILE().getGENERAL(), dGeneralVal);

					if (log.isDebugEnabled()) {
						
						log.debug(">>> Parse General Section of Result Set End");
						
					}
				} catch (ClassNotFoundException ex) {

					throw new UnsupportedEncodingException();

				} catch (NoSuchMethodException ex) {

					throw new UnsupportedEncodingException();

				} catch (SecurityException ex) {

					throw new UnsupportedEncodingException();

				} catch (IllegalAccessException ex) {

					throw new UnsupportedEncodingException();

				} catch (IllegalArgumentException ex) {

					throw new UnsupportedEncodingException();

				} catch (InvocationTargetException ex) {

					throw new UnsupportedEncodingException();

				}
			}
		}
	}

	/**
	 * Get the 'EXTENDED' element of a Result Set Object.
	 * 
	 * @param Extended
	 * 
	 * @return EXTENDED element of the script result file specified.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public Extended deExtended(Extended ext) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Extended Section of Result Set Start");
			
		}
		if (ext != null) {
			
			Map<String, String> origExt = new HashMap<String, String>();
			String decryptedVal = null;
			origExt = ext.getValues();
			for (String key : origExt.keySet()) {
				decryptedVal = Common.decryptStr(origExt.get(key));
				ext.setValue(key, decryptedVal);
				}
		}
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Extended Section of Result Set End");
			
		}
		return ext;
	}

	/**
	 * Get the 'RESULTS' element of a Result Set Object.
	 * 
	 * @param Results
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void decryptResults(Results results) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Results  Section of Result Set Start");
			
		}

		List<Item> item = results.getITEM();
		List<Item> dItem = new ArrayList<Item>();
		Response dResponse = null;
		Defects dDefects = null;
		List<SubScriptResults> dSubScriptResults = null;
		Results dResults = new Results();

		for (Item ites : item) {
			try {
				Class<?> clazz = Class.forName("com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item");
				// TODO get the Field name dynamicaly
				Field field = clazz.getDeclaredField("response");
				field.setAccessible(true);
				Response value = (Response) field.get(ites);
				dResponse = decryptResponse(value);
				ites.setRESPONSE(dResponse);

				Field fieldDefects = clazz.getDeclaredField("defects");
				fieldDefects.setAccessible(true);
				Defects defValue = (Defects) fieldDefects.get(ites);
				if (defValue != null) {
					
					dDefects = decryptDefects(defValue);
					fieldDefects.set(ites, dDefects);
					
				}

				Field fieldSubScriptRes = clazz.getDeclaredField("subscriptresults");
				fieldSubScriptRes.setAccessible(true);
				List<SubScriptResults> subscriptresultsVal = (List<SubScriptResults>) fieldSubScriptRes.get(ites);
				if (subscriptresultsVal != null) {
					
					dSubScriptResults = decryptSubScriptResults(subscriptresultsVal);
					fieldSubScriptRes.set(ites, dSubScriptResults);

				}

				ites.setTIME(Common.decryptStr(ites.getTIME()));
				ites.setDATE(Common.decryptStr(ites.getDATE()));
				ites.setFREETEXT(Common.decryptStr(ites.getFREETEXT()));
				ites.setUOM(Common.decryptStr(ites.getUOM()));
				ites.setPREV(Common.decryptStr(ites.getUOM()));
				ites.setRESPONSEFILE(Common.decryptStr(ites.getRESPONSEFILE()));
				ites.setRESPONSEFILENAME(Common.decryptStr(ites.getRESPONSEFILENAME()));
				ites.setResOrderNo(Common.decryptStr(ites.getResOrderNo()));
				ites.setSeqNo(Common.decryptStr(ites.getSeqNo()));

				dItem.add(ites);

				Field itemField = results.getClass().getDeclaredField("item");
				itemField.set(dResults, dItem);

				if (log.isDebugEnabled()) {
					log.debug(">>> Parse Results Section of Result Set End");
				}

			} catch (SecurityException ex) {

				throw new UnsupportedEncodingException();

			} catch (ClassNotFoundException ex) {

				throw new UnsupportedEncodingException();

			}

			catch (IllegalAccessException ex) {

				throw new UnsupportedEncodingException();

			} catch (IllegalArgumentException ex) {

				throw new UnsupportedEncodingException();

			} catch (NoSuchFieldException ex) {

				throw new UnsupportedEncodingException();

			}
		}
	}

	/**
	 * Get the 'RESPONSE' element of a Result Set Object.
	 * 
	 * @param RESPONSE
	 * 
	 * @return RESPONSE element of the script result file specified.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private Response decryptResponse(Response dResponse) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Response Section of Result Set Start");
			
		}
		dResponse.setValue(Common.decryptStr(dResponse.getValue()));
		dResponse.setLABEL(Common.decryptStr(dResponse.getLABEL()));
		dResponse.setRESPONSEDisplay(Common.decryptStr(dResponse.getRESPONSEDisplay()));
		dResponse.setLINENO(Common.decryptStr(dResponse.getLINENO()));
		dResponse.setType(Common.decryptStr(dResponse.getType()));

		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Response Section of Result Set Start");
			
		}
		return dResponse;
	}

	/**
	 * Get the 'SUBSCRIPTRESULTS' element of a Result Set Object.
	 * 
	 * @param List
	 *            of SUBSCRIPTRESULTS
	 * 
	 * @return List of SUBSCRIPTRESULTS element of the script result file specified.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private List<SubScriptResults> decryptSubScriptResults(List<SubScriptResults> subscriptresultsVal)
			throws UnsupportedEncodingException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse SubScriptResults Section of Result Set Start");
			
		}

		SubScriptResults dSubScriptResults = new SubScriptResults();
		List<SubScriptResults> dSubScripResultsLst = new ArrayList<SubScriptResults>();

		for (SubScriptResults subScriptRes : subscriptresultsVal) {

			String subScriptVal = Common.decryptStr(subScriptRes.getValue());
			String subScriptResFile = Common.decryptStr(subScriptRes.getRESULTSFILE());

			dSubScriptResults.setValue(subScriptVal);
			dSubScriptResults.setRESULTSFILE(subScriptResFile);

			dSubScripResultsLst.add(dSubScriptResults);
		}

		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse SubScriptResults Section of Result Set End");
			
		}
		return dSubScripResultsLst;
	}

	/**
	 * Get the 'DEFECTS' element of a Result Set Object.
	 * 
	 * @param DEFECTS
	 * 
	 * @return DEFECTS element of the script result file specified.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private Defects decryptDefects(Defects defValue) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Defects Section of Result Set Start");
			
		}
		Defects dDefects = new Defects();
		List<Defect> dDefectList = new ArrayList<Defect>();
		try {
			Field defectCountField = dDefects.getClass().getDeclaredField("defectCount");
			defectCountField.setAccessible(true);
			String defectCount = Common.decryptStr(String.valueOf(defValue.getDefectCount()));
			defectCountField.set(dDefects, defectCount);

			Field defectSetField = dDefects.getClass().getDeclaredField("defectSet");
			defectSetField.setAccessible(true);
			String defectSet = Common.decryptStr(defValue.getDefectSet());
			defectSetField.set(dDefects, defectSet);

			Field defField = dDefects.getClass().getDeclaredField("defect");
			defField.setAccessible(true);
			List<Defect> defectList = defValue.getDEFECT();
			for (Defect defVal : defectList) {
				String decAction = Common.decryptStr(defVal.getAction());
				String decLogNo = Common.decryptStr(defVal.getLogNo());
				String decRate = Common.decryptStr(defVal.getRate());
				String decVal = Common.decryptStr(defVal.getValue());

				defVal.setAction(decAction);
				defVal.setLogNo(decLogNo);
				defVal.setRate(decRate);
				defVal.setValue(decVal);
				dDefectList.add(defVal);
			}
			defField.set(dDefects, dDefectList);
		} catch (NoSuchFieldException ex) {
			
			throw new UnsupportedEncodingException();
			
		} catch (SecurityException ex) {
			
			throw new UnsupportedEncodingException();
			
		} catch (IllegalArgumentException ex) {
			
			throw new UnsupportedEncodingException();
			
		} catch (IllegalAccessException ex) {
			
			throw new UnsupportedEncodingException();
			
		}
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse Defects Section of Result Set End");
			
		}
		return dDefects;

	}

	/**
	 * Get the 'CARRYTHROUGH' element of a Result Set Object.
	 * 
	 * @param CARRYTHROUGH
	 * 
	 * @return CARRYTHROUGH element of the script result file specified.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private CarryThrough decryptCarryThrough(CarryThrough carrythrough) throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			
			log.debug(">>> Parse CarryThrough Section of Result Set Start");
			
		}
		if (carrythrough != null) {
			CarryThrough dCarryThrough = new CarryThrough();
			try {
				List<DataItem> dataItem = carrythrough.getDATAITEM();
				List<DataItem> dDataItem = new ArrayList<DataItem>();

				for (DataItem data : dataItem) {
					String val = Common.decryptStr(data.getValue());
					String fieldName = Common.decryptStr(data.getFieldName());
					data.setValue(val);
					data.setFieldName(fieldName);
					dDataItem.add(data);
					// dCarryThrough.setDataitem(dDataItem);
				}
				Field dataItemField = dCarryThrough.getClass().getDeclaredField("dataitem");
				dataItemField.setAccessible(true);
				dataItemField.set(dCarryThrough, dDataItem);

			} catch (NoSuchFieldException ex) {
				
				throw new UnsupportedEncodingException();
				
			} catch (SecurityException ex) {
				
				throw new UnsupportedEncodingException();
				
			} catch (IllegalArgumentException ex) {
				
				throw new UnsupportedEncodingException();
				
			} catch (IllegalAccessException ex) {
				
				throw new UnsupportedEncodingException();
				
			}
			if (log.isDebugEnabled()) {
				log.debug(">>> Parse CarryThrough Section of Result Set End");
			}
			return dCarryThrough;
		}
		return null;
	}
	
	
	/*//33896 : WebServices: Repeating Groups Seq No cant be loaded in XML Loader whilst encrypted 
	*//**
	 * Get the 'HK' element of a Result Set Object.
	 * 
	 * @param HK
	 * 
	 * @throws UnsupportedEncodingException
	 *//*
	private void decryptHk(Hk hk) throws UnsupportedEncodingException {
		
		if (log.isDebugEnabled()) {
			
			log.debug("<<< Parse HK Section of Result Set Start");
			
		}
		if (hk != null) {
			
			try {
				
				List<Idx> dIdx = new ArrayList<Idx>();
				List<SeqNo> dSeqNo = new ArrayList<SeqNo>();

				List<CaInvalid> caInval = hk.getCAINVALID();
				List<Rgroups> rGroups = hk.getRGROUPS();

				for (CaInvalid caInvalid : caInval) {
					List<Idx> idx = caInvalid.getIDX();
					for (Idx id : idx) {
						
						//These values were used in the legacy system
						int jts = Integer.parseInt(Common.decryptStr(String.valueOf(id.getJTS())));
						int val = Integer.parseInt(Common.decryptStr(String.valueOf(id.getValue())));

						id.setJTS(id.getJTS());
						id.setValue(id.getValue());
						dIdx.add(id);
					}
				}

				for (Rgroups rgrps : rGroups) {
					List<SeqNo> seq = rgrps.getSEQNO();
					for (SeqNo seqNo : seq) {
						
						//These values were used in the legacy system
						int start = Integer.parseInt(Common.decryptStr(String.valueOf(seqNo.getSTART())));
						int noGrp = Integer.parseInt(Common.decryptStr(String.valueOf(seqNo.getNOGROUPS())));

						seqNo.setSTART(seqNo.getSTART());;
						seqNo.setNOGROUPS(seqNo.getNOGROUPS());
						dSeqNo.add(seqNo);
					}
					
				}

			} catch (SecurityException ex) {
				
				throw new UnsupportedEncodingException();
				
			} catch (IllegalArgumentException ex) {
				
				throw new UnsupportedEncodingException();
				
			} 
			
			if (log.isDebugEnabled()) {
				
				log.debug(">>> Parse HK Section of Result Set END");
			}
		}
	}*/
}
