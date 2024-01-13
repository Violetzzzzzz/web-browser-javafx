package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;

public class ThemeComboBoxEventHandler implements EventHandler<ActionEvent> {

	Setting setting;

	public ThemeComboBoxEventHandler(Setting setting) {
		this.setting = setting;
	}

	@Override
	public void handle(ActionEvent event) {
		String themeName = setting.themeColorComboBox.getValue();
		if (themeName.equals("Cherry Kiss")) {
			setting.themeColorText.setFill(Color.MAROON);
			setting.bgColor = "#FFE4E1"; // MistyRose
			setting.buttonBorderAndTextColor = "#800000"; // Maroon
		} else if (themeName.equals("Citrus Sunset")) {
			setting.themeColorText.setFill(Color.DARKORANGE);
			setting.bgColor = "#FFEBCD"; // BlanchedAlmond
			setting.buttonBorderAndTextColor = "#FF8C00"; // DarkOrange
		} else if (themeName.equals("Lemon Zest")) {
			setting.themeColorText.setFill(Color.GOLDENROD);
			setting.bgColor = "#FFFFE0"; // LightYellow
			setting.buttonBorderAndTextColor = "#DAA520"; // GoldenRod
		} else if (themeName.equals("Green Apple Wings")) {
			setting.themeColorText.setFill(Color.SEAGREEN);
			setting.bgColor = "#F5FFFA"; // MintCream
			setting.buttonBorderAndTextColor = "#2E8B57"; // SeaGreen
		} else if (themeName.equals("Blueberry Dream")) {
			setting.themeColorText.setFill(Color.NAVY);
			setting.bgColor = "#F0FFFF"; // Azure
			setting.buttonBorderAndTextColor = "#000080"; // Navy
		} else if (themeName.equals("Grape Melody")) {
			setting.themeColorText.setFill(Color.web("#663399"));
			setting.bgColor = "#E6E6FA"; // Lavender
			setting.buttonBorderAndTextColor = "#663399"; // RebeccaPurple
		} else if (themeName.equals("Strawberry Ballet")) {
			setting.themeColorText.setFill(Color.HOTPINK);
			setting.bgColor = "#FFF0F5"; // LavenderBlush
			setting.buttonBorderAndTextColor = "#FF69B4"; // HotPink
		} else if (themeName.equals("Grapefruit Haze")) {
			setting.themeColorText.setFill(Color.DIMGRAY);
			setting.bgColor = "#F5F5F5"; // WhiteSmoke
			setting.buttonBorderAndTextColor = "#696969"; // DimGrey
		}
		System.out.println("Change Theme to : " + themeName);
		setting.updateToNewStyle();
	}

}
