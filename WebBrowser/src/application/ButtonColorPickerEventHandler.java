package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ButtonColorPickerEventHandler implements EventHandler<ActionEvent> {
	Setting setting;

	public ButtonColorPickerEventHandler(Setting setting) {
		this.setting = setting;
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		setting.buttonColorText.setFill(setting.buttonColorPicker.getValue());
		setting.buttonBorderAndTextColor = "#" + setting.buttonColorPicker.getValue().toString().substring(2, 8);
		System.out.println("Button Color change to: " + setting.buttonBorderAndTextColor);
		setting.updateToNewStyle();
	}

}
