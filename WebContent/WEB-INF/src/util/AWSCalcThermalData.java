package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import constant.Constant;
import data.DynamoDbAccess;

public class AWSCalcThermalData {

	/** key to search sounding data in Amazon DynamoDB */
	private String _soundingId = "";
	private String _strGndDwPt = "";
	
	private ArrayList<HashMap<String,String>> _measuredData = new ArrayList<HashMap<String,String>>();
	
	/**
	 * constructor: get data from DB
	 * @param soundingId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AWSCalcThermalData(String soundingId) throws FileNotFoundException, IOException {
		_soundingId = soundingId;
		
		// get Amazon DynamoDB client object
		AmazonDynamoDBClient client = DynamoDbAccess.getClient();

		// Amazon DynamoDB search
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		
		Condition hashKeyCondition = new Condition()
			.withComparisonOperator(ComparisonOperator.EQ)
			.withAttributeValueList(new AttributeValue().withS(_soundingId));
		keyConditions.put("soundingId", hashKeyCondition);
		
		QueryRequest qReq = new QueryRequest()
			.withTableName("SoundingDtl")
			.withKeyConditions(keyConditions)
			.withAttributesToGet(Arrays.asList("recordId", "height", "temperature", "reqGndTmp"))
			.withConsistentRead(true);
		
		QueryResult qRslt = client.query(qReq);
		
		// set Amazon DynamoDB data to data set
		for(Map<String, AttributeValue> rsltRec : qRslt.getItems()) {
			HashMap<String,String> recMap = new HashMap<String,String>();
			String strHeight = rsltRec.get("height").getS();
			String strTemperature = rsltRec.get("temperature").getS();
			String strReqGndTmp = rsltRec.get("reqGndTmp").getS();
			
			recMap.put("recordId", rsltRec.get("recordId").getS());
			recMap.put("strHeight", strHeight);
			recMap.put("strTemperature", strTemperature);
			recMap.put("strReqGndTmp", strReqGndTmp);
			_measuredData.add(recMap);
		}
	}
	
	public HashMap<String,String> AWSGetThermalTop(double gndTmp) {

		// height of thermal top
		String strThermalTop = "N/A";
		// height of cloud base
		String strCloudBase = "N/A";

		double gndDwPt = 0.0f;
		if ("N/A".equals(_strGndDwPt)) {
			HashMap<String,String> retMap = new HashMap<String,String>();
			retMap.put("THERMAL_TOP", strThermalTop);
			retMap.put("CLOUD_BASE", strCloudBase);
			
			return retMap;
		}
		else {
			gndDwPt = Double.parseDouble(_strGndDwPt);
		}
		
		for (int i=0; i<_measuredData.size(); ++i) {
			if("N/A".equals(_measuredData.get(i).get("strHeight").toString())
					|| "N/A".equals(_measuredData.get(i).get("strTemperature"))
					|| "N/A".equals(_measuredData.get(i).get("strRegGndTmp"))
					) {
				//TODO
				break;
			}
			else {
				double reqGndTmp = Double.parseDouble(_measuredData.get(i).get("strReqGndTmp"));
				// search for the temperature
				if (gndTmp < reqGndTmp) {
					continue;
				}
				else {
					if (i == 0) {
						strThermalTop = "0.0";
						strCloudBase = "0.0";
					}
					else {
						//-----------------------------------
						// calculate thermal top, cloud base
						//-----------------------------------
						// explore the point at which dry adiabatic line and emma gram intersect
						double height2 = Double.parseDouble(_measuredData.get(i).get("strHeight"));
						double height1 = Double.parseDouble(_measuredData.get(i-1).get("strHeight"));
						double temperature2 = Double.parseDouble(_measuredData.get(i).get("strTemperature"));
						double temperature1 = Double.parseDouble(_measuredData.get(i-1).get("strTemperature"));
						
						double intersectTmpDry = ((100.0f * gndTmp - height2 * Constant.DRY_ADIABATIC_LAPSE_RATE) * (temperature2 - temperature1) + (height2 - height1) * temperature2 * Constant.DRY_ADIABATIC_LAPSE_RATE)
								/ ((height2 - height1) * Constant.DRY_ADIABATIC_LAPSE_RATE + 100.0f * (temperature2 - temperature1))
								;
						if (intersectTmpDry < gndDwPt) {
							double cloudBase = 100.0f * (gndTmp - gndDwPt) / Constant.DRY_ADIABATIC_LAPSE_RATE;
							double intersectTmpSat = ((cloudBase - height2) * (temperature2 - temperature1) * Constant.SATURATED_ADIABATIC_LAPSE_RATE + 100.0f * gndDwPt * (temperature2 - temperature1) + Constant.SATURATED_ADIABATIC_LAPSE_RATE * (height2 - height1) * temperature2)
									/ (Constant.SATURATED_ADIABATIC_LAPSE_RATE * (height2 - height1) + 100.0f * (temperature2 - temperature1))
									;
							double thermalTop = 100.0f * (gndDwPt - intersectTmpSat) / Constant.SATURATED_ADIABATIC_LAPSE_RATE + cloudBase;
							strThermalTop = String.valueOf(thermalTop);
							strCloudBase = String.valueOf(cloudBase);
						}
						else {
							double thermalTop = 100.0f * (gndTmp - intersectTmpDry) / Constant.DRY_ADIABATIC_LAPSE_RATE;
							strThermalTop = String.valueOf(thermalTop);
						}
					}
					break;
				}
			}
		}
		
		// set return value
		HashMap<String,String> retMap = new HashMap<String,String>();
		retMap.put("THERMAL_TOP", strThermalTop);
		retMap.put("CLOUD_BASE", strCloudBase);
		
		return retMap;
	}
	
}
