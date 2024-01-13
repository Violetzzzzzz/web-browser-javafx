package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class Setting {
	WebBrowser thisBrowser;
	TabPane settingTabPane = new TabPane();
	Tab generalTab = new Tab("General");
	Tab styleTab = new Tab("Style");
	Text themeColorText;
	ComboBox<String> themeColorComboBox;
	ColorPicker buttonColorPicker;
	Text buttonColorText;

	// Default setting
	String homepage = "https://www.google.com";
	String bgColor = "#F5F5F5";
	String buttonBorderAndTextColor = "#696969";

	public Setting(WebBrowser webBrowser) {
		this.thisBrowser = webBrowser;
		this.loadSetting();
	}

	protected void setMenu() {
		thisBrowser.settingStage.initModality(Modality.APPLICATION_MODAL);
		thisBrowser.settingStage.initStyle(StageStyle.UTILITY);
		this.setGeneralTab();
		this.setStyleTab();
		// this.setViewTab();
		settingTabPane.getTabs().addAll(generalTab, styleTab);
		Scene popupScene = new Scene(settingTabPane, 450, 300);
		thisBrowser.settingStage.setScene(popupScene);
		thisBrowser.settingStage.setTitle("Preferences");
	}

	private void setStyleTab() {
		// theme setting menu
		ObservableList<String> options = FXCollections.observableArrayList("Cherry Kiss", "Citrus Sunset", "Lemon Zest",
				"Green Apple Wings", "Blueberry Dream", "Grape Melody", "Strawberry Ballet", "Grapefruit Haze");
		themeColorComboBox = new ComboBox<String>(options);
		themeColorText = new Text("Theme: ");
		Button advancedButton = new Button();
		advancedButton.setText("Advanced");

		// advance style setting menu
		// background color setting menu
		ColorPicker bgColorPicker = new ColorPicker();
		bgColorPicker.setValue(Color.CORAL);
		Text bgColorText = new Text("Background color:");
		bgColorText.setFill(bgColorPicker.getValue());

		// buttons border and text color setting menu
		buttonColorPicker = new ColorPicker();
		buttonColorPicker.setValue(Color.CORAL);
		buttonColorText = new Text("Button Text and border color:");
		buttonColorText.setFill(buttonColorPicker.getValue());

		GridPane styleGrid = new GridPane();
		styleGrid.setVgap(4);
		styleGrid.setHgap(10);
		styleGrid.setPadding(new Insets(5, 5, 5, 5));
		styleGrid.add(themeColorText, 0, 0);
		styleGrid.add(themeColorComboBox, 1, 0);
		styleGrid.add(advancedButton, 1, 2);
		styleTab.setContent(styleGrid);

		advancedButton.setOnAction(action -> {
			if (!styleGrid.getChildren().contains(bgColorText)) {
				styleGrid.add(bgColorText, 0, 3);
				styleGrid.add(bgColorPicker, 1, 3);
				styleGrid.add(buttonColorText, 0, 5);
				styleGrid.add(buttonColorPicker, 1, 5);
			} else {
				styleGrid.getChildren().removeAll(bgColorText, bgColorPicker, buttonColorText, buttonColorPicker);
			}
		});

		themeColorComboBox.setOnAction(new ThemeComboBoxEventHandler(this));

		bgColorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				bgColorText.setFill(bgColorPicker.getValue());
				bgColor = "#" + bgColorPicker.getValue().toString().substring(2, 8);
				thisBrowser.scene.getRoot().setStyle("-fx-background-color: " + bgColor + ";");
				System.out.println("Background Color change to: " + bgColor);
			}
		});

		buttonColorPicker.setOnAction(new ButtonColorPickerEventHandler(this));

	}

	private void setGeneralTab() {
		TextField homepageSetField = new TextField(homepage);
		Button homepageSaveButton = new Button();
		homepageSaveButton.setText("Save");
		Button setToCurrentPageButton = new Button();
		setToCurrentPageButton.setText("Set to current page");
		GridPane generalGrid = new GridPane();
		generalGrid.setVgap(4);
		generalGrid.setHgap(10);
		generalGrid.setPadding(new Insets(5, 5, 5, 5));
		generalGrid.add(new Label("Homepage URL: "), 0, 0);
		generalGrid.add(homepageSetField, 1, 0);
		generalGrid.add(setToCurrentPageButton, 1, 2);
		generalGrid.add(homepageSaveButton, 2, 0);
		generalTab.setContent(generalGrid);

		// set buttons' event handlers
		setToCurrentPageButton.setOnAction(action -> {
			if (thisBrowser.currentWV != null) {
				String url = thisBrowser.currentWV.getEngine().getLocation();
				homepageSetField.setText(url);
			} else {
				System.out.println(" Current WebView is null. ");
			}
		});

		homepageSaveButton.setOnAction(action -> {
			if (!homepageSetField.getText().isEmpty()) {
				if (thisBrowser.validURL(homepageSetField.getText())) {
					homepage = homepageSetField.getText();
					System.out.println(" Setting homepage...");
					System.out.println(" To URL: " + homepage);
				} else {
					System.out.println(" Invalid URL. Can't access invalid URL.");
				}
			} else {
				homepage = "";
				System.out.println(" Empty URL. Homepage is blank now.");
			}
		});
	}

	protected void updateToNewStyle() {
		// update scene background
		thisBrowser.scene.getRoot().setStyle("-fx-background-color: " + bgColor + ";");

		// update buttons style
		thisBrowser.backButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;"
				+ "-fx-text-fill: " + buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
				+ "-fx-opacity: 0.7; ");
		thisBrowser.forwardButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;"
				+ "-fx-text-fill: " + buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
				+ "-fx-opacity: 0.7; ");
		thisBrowser.urlLaunch.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;"
				+ "-fx-text-fill: " + buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
				+ "-fx-opacity: 0.7; ");
		thisBrowser.reload.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 17px;" + "-fx-text-fill: "
				+ buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
		thisBrowser.newWebTab.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 5px;" + "-fx-padding: 1px;" + "-fx-font-size: 18px;" + "-fx-text-fill: "
				+ buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.3; ");

		// update tab buttons style
		for (WebTab value : thisBrowser.tabsAndWebs.values()) {
			value.urlButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor + "; "
					+ "-fx-border-radius: 4px;" + "-fx-padding: 4px 6px;" + "-fx-font-size: 12px;" + "-fx-text-fill: "
					+ buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
			value.closeButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + buttonBorderAndTextColor
					+ "; " + "-fx-border-radius: 4px;" + "-fx-padding: 4px 6px;" + "-fx-font-size: 12px;"
					+ "-fx-text-fill: " + buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
					+ "-fx-opacity: 0.7; ");
		}

		thisBrowser.urlTextField.setStyle("-fx-font-size: 14px;" + "-fx-text-fill: " + buttonBorderAndTextColor + ";"
				+ "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
	}

	private void loadSetting() {
		System.out.println("Loading setting...");
		try {
			String settingFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/setting.txt";
			Scanner settingScan = new Scanner(new File(settingFileName));
			String savedHomepage = settingScan.nextLine();
			homepage = savedHomepage;
			System.out.println("Homepage URL: " + homepage);
			String savedBackgroundColor = settingScan.nextLine();
			bgColor = savedBackgroundColor;
			System.out.println("Background Color: " + bgColor);
			String savedTabBorderColor = settingScan.nextLine();
			buttonBorderAndTextColor = savedTabBorderColor;
			System.out.println("Tab Border Color: " + buttonBorderAndTextColor);
			settingScan.close();
		} catch (

		FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void saveSetting() {
		System.out.println("Saving setting...");
		try {
			String settingFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/setting.txt";
			PrintStream settingPS = new PrintStream(new File(settingFileName));
			settingPS.println(homepage);
			System.out.println("Saving homepage URL: " + homepage);
			settingPS.println(bgColor);
			System.out.println("Saving background color: " + bgColor);
			settingPS.println(buttonBorderAndTextColor);
			System.out.println("Saving button color: " + buttonBorderAndTextColor);
			settingPS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
