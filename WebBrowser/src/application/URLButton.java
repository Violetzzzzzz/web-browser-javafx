package application;

import javafx.scene.control.Button;

public class URLButton extends Button {
	WebBrowser thisBrowser;
	WebTab tab;

	public URLButton(WebTab tab, WebBrowser webBrowser, String url) {
		super(url);
		this.tab = tab;
		this.thisBrowser = webBrowser;
		this.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + thisBrowser.setting.buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 4px 6px;" + "-fx-font-size: 12px;" + "-fx-text-fill: "
				+ thisBrowser.setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
		this.setOnAction(action -> {
			System.out.println(thisBrowser.currentWV);
			System.out.println("Switching...");
			thisBrowser.mainPane.getChildren().remove(thisBrowser.currentWV);
			thisBrowser.currentWV = this.tab.webView;
			thisBrowser.urlTextField.setText(thisBrowser.currentWV.getEngine().getLocation());
			thisBrowser.mainPane.getChildren().add(thisBrowser.currentWV);
			System.out.println("To URL: " + thisBrowser.currentWV.getEngine().getLocation());
		});
	}

}
