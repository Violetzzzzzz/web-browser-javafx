package application;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;

public class NewWebTab extends Button {
	WebBrowser thisBrowser;

	public NewWebTab(WebBrowser webBrowser) {
		super();
		this.thisBrowser = webBrowser;
		this.setText(" + ");
		this.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + thisBrowser.setting.buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 5px;" + "-fx-padding: 1px;" + "-fx-font-size: 18px;" + "-fx-text-fill: "
				+ thisBrowser.setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
				+ "-fx-opacity: 0.3; ");
		this.setOnAction(action -> {
			thisBrowser.mainPane.getChildren().remove(thisBrowser.currentWV);
			WebView newWebView = new WebView();
			newWebView.getEngine().load(thisBrowser.setting.homepage);
			thisBrowser.setEngineListener(newWebView);
			WebTab newTab = new WebTab(thisBrowser, newWebView);
			thisBrowser.tabsAndWebs.put(newWebView, newTab);
			Node lastNode = thisBrowser.webTabsPane.getChildren().get(thisBrowser.webTabsPane.getChildren().size() - 1);
			thisBrowser.webTabsPane.getChildren().remove(lastNode);
			thisBrowser.webTabsPane.getChildren().addAll(newTab, lastNode);
			thisBrowser.currentWV = newWebView;
			thisBrowser.urlTextField.setText(thisBrowser.currentWV.getEngine().getLocation());
			thisBrowser.mainPane.getChildren().add(thisBrowser.currentWV);
		});
	}

}
