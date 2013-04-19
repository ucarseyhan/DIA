package client.state;

import client.ControllerInformationMessage;
import client.InteractionRequest;
import client.InteractionResponse;
import client.PeriodicServerMessage;

public interface State {
	public void requestInteraction();
	public void requestAssignment();
	public void timeOutControl();
	public void getControllerInfoMessage(ControllerInformationMessage cInf);
	public void getServerPeriodicMessage(PeriodicServerMessage msg);
	public void getInteractionResponse(InteractionResponse intrResponse);
	public void getInterActionRequest(InteractionRequest request);
	public void printInfo();

}
