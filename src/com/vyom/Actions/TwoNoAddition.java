package com.vyom.Actions;

import java.util.List;

import com.vyom.base.AbstractServiceImpl;
import com.vyom.chatbot.core.ChatBotLogger;
import com.vyom.chatbot.core.DataCollectionDetails;

public class TwoNoAddition extends AbstractServiceImpl {

	
	public void preProcessing() {
		ChatBotLogger.logger.info("From TwoNoAddition preProcessing");
	}
	
	public String processing(List<DataCollectionDetails> dataCollectionList) {
		Integer result=0;
		for (DataCollectionDetails dataCollectionDetails : dataCollectionList) {
			result = result + Integer.parseInt(dataCollectionDetails.getUserInput());
		}
		
		ChatBotLogger.logger.info("Final result is : " + result);
		return result.toString();
	}

	public void postProcessing() {
		ChatBotLogger.logger.info("From TwoNoAddition postProcessing");
	}
	
	public void validateNum1Value(String input) {
		ChatBotLogger.logger.info("In TwoNoAddition class validate first Value : " + input);		
	}
	public void validateNum2Value(String input) {
		ChatBotLogger.logger.info("In TwoNoAddition class validate second Value : " + input);		
	}
	public void initExecute() {
		init();
		preProcessing();
	}
}
