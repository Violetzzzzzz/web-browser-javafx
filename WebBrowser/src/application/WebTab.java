package application;

import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

public class WebTab extends HBox {
	WebBrowser thisBrowser;
	URLButton urlButton;
	WebView webView;
	CloseButton closeButton;

	public WebTab(WebBrowser webBrowser, WebView webView) {
		super();
		this.thisBrowser = webBrowser;
		this.webView = webView;
		String url = "";
		if (webView.getEngine().getLocation().length() > 25) {
			url = webView.getEngine().getLocation().substring(0, 25) + "...";
		} else {
			url = webView.getEngine().getLocation();
		}
		urlButton = new URLButton(this, webBrowser, url);
		this.closeButton = new CloseButton(this, webBrowser);
		this.getChildren().addAll(urlButton, closeButton);
		this.setStyle("-fx-border-width: 0px; ");
		this.getInsets();
	}

	public WebView getWebView() {
		return webView;
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}

	public void changeText(String location) {
		String url = "";
		if (location.length() > 25) {
			url = location.substring(0, 25) + "...";
		} else {
			url = location;
		}
		urlButton.setText(url);
	}

}
