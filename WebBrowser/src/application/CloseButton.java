package application;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class CloseButton extends Button {
	WebBrowser thisBrowser;
	WebTab tab;

	public CloseButton(WebTab tab, WebBrowser webBrowser) {
		super();
		this.tab = tab;
		this.thisBrowser = webBrowser;
		this.setText("✕");
		Tooltip closeTooltip = new Tooltip(" Close ");
		this.setTooltip(closeTooltip);
		this.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + thisBrowser.setting.buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 4px 6px;" + "-fx-font-size: 12px;" + "-fx-text-fill: "
				+ thisBrowser.setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
		this.setOnAction(action -> {
			System.out.println(tab.webView);
			System.out.println("Closing...");
			System.out.println("URL: " + tab.webView.getEngine().getLocation());
			thisBrowser.mainPane.getChildren().remove(tab.webView);
			thisBrowser.tabsAndWebs.remove(tab.webView);
			thisBrowser.webTabsPane.getChildren().remove(tab);
			thisBrowser.currentWV = null;
			thisBrowser.urlTextField.setText("");
		});

		// TODO Auto-generated constructor stub
	}

}
