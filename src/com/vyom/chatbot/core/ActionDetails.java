package com.vyom.chatbot.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import noNamespace.AEChatBotMenuDocument.AEChatBotMenu.MenuNode.ActionNode;
import noNamespace.AEChatBotMenuDocument.AEChatBotMenu.MenuNode.ActionNode.DataCollection.DataNode;

public class ActionDetails {

	String actionClass;
	List<DataCollectionDetails> dataCollectionList;
	
	private ActionDetails(String actionClass, List<DataCollectionDetails> dataCollectionList) {
		this.actionClass = actionClass;
		this.dataCollectionList = dataCollectionList;
	}
	
	public static ActionDetails Create(ActionNode actionNode) throws Exception {
		initializeActionPreprocess(actionNode);
		List<DataCollectionDetails> dataCollectionList = new ArrayList<DataCollectionDetails>();
		for (DataNode dataNode : actionNode.getDataCollection().getDataNodeArray()) {
			dataCollectionList.add(new DataCollectionDetails(dataNode.getDisplayText(), dataNode.getInputName(), "", dataNode.getValidationMethod()));
		}
		return new ActionDetails(actionNode.getActionClass(), dataCollectionList);
	}
	
	private static void initializeActionPreprocess(ActionNode actionNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Class cls = Class.forName(actionNode.getActionClass());
		Object obj = cls.newInstance();
		Method method = cls.getMethod("initExecute");
		method.invoke(obj);
	}
	
	public String getActionDataCollectionAndExecution(UserDetails userDetails) throws Exception {
		String userParam = "";
		for (DataCollectionDetails dataCollectionDetails : this.dataCollectionList) {
			if("".equals(dataCollectionDetails.getUserInput()) && dataCollectionDetails.getDisplayText().equals(userDetails.getLastParamAsked())) {
				if(!validateUserInput(this.actionClass, dataCollectionDetails.getValidationMethod(), userDetails.getLastMsg())) {
					return "Wrong Input.." + "\n" + dataCollectionDetails.getDisplayText();
				}
				dataCollectionDetails.setUserInput(userDetails.getLastMsg());
			}
			if("".equals(dataCollectionDetails.getUserInput())) {
				userParam = dataCollectionDetails.getDisplayText();
				userDetails.setLastParamAsked(userParam);
				return userParam;
			}
		}
		userDetails.setType("CN");
		userParam = (String) executeProcessing(this.actionClass, "processing", this.dataCollectionList);
		userDetails.setActionDetails(null);
		return userParam;
	}
	
	private Object executeProcessing(String className, String methodName, List<DataCollectionDetails> dataCollectionList) throws Exception {
		Class cls = Class.forName(className);
		Object obj = cls.newInstance();
		Method method = cls.getDeclaredMethod(methodName, List.class);
		Object obj1 = method.invoke(obj, dataCollectionList);
		if(obj1 instanceof String) {
			return obj1;
		}
		return "";
	}
	
	private boolean validateUserInput(String className, String validateMethodName, String lastMsg) {
		Class cls;
		try {
			cls = Class.forName(className);
			Object obj = cls.newInstance();
			Method method = cls.getMethod(validateMethodName, String.class);
			method.invoke(obj, lastMsg);
			return true;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
}