package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WebBrowser extends Application {
	Stage primaryStage;
	Stage settingStage = new Stage();
	Stage aboutStage = new Stage();
	Setting setting = new Setting(this);;
	Scene scene;
	WebView currentWV;
	MenuBar menuBar;
	HBox root = new HBox();
	VBox mainPane = new VBox();
	HBox urlSearcherPane = new HBox();
	HBox webTabsPane = new HBox();
	VBox sidePane = new VBox();
	TabPane listsTabPane = new TabPane();
	TextField urlTextField;
	NewWebTab newWebTab;
	Button backButton;
	Button forwardButton;
	Button urlLaunch;
	Button reload;
	double currentZoom = 1.0;
	Map<WebView, WebTab> tabsAndWebs = new HashMap<WebView, WebTab>();
	ListView<String> historyListView = new ListView<>();
	ListView<String> bookmarkListView = new ListView<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			scene = new Scene(root, 1200, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("BerrySurf");
			primaryStage.setScene(scene);
			primaryStage.show();

			this.loadSetting();
			this.setUIControls();
			this.setMenus();
			this.setEngineListener(currentWV);
			this.loadHistoryAndBookmarks();
			this.setListsSelectionListener();

			VBox.setVgrow(listsTabPane, Priority.ALWAYS);
			HBox.setHgrow(mainPane, Priority.ALWAYS);
			VBox.setVgrow(mainPane, Priority.ALWAYS);
			HBox.setHgrow(urlTextField, Priority.ALWAYS);
			mainPane.getChildren().addAll(menuBar, urlSearcherPane, webTabsPane, currentWV);
			root.getChildren().addAll(sidePane, mainPane);
			scene.getRoot().setStyle("-fx-background-color: " + setting.bgColor + ";");
			this.primaryStage = primaryStage;
			this.primaryStage.setOnCloseRequest(event -> {
				this.saveHistory();
				this.saveBookmarks();
				this.setting.saveSetting();
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setListsSelectionListener() {
		// Add a mouse click event handler to show the context menu on right-click

		// set bookmark and history context menu
		ContextMenu bookmarkMenu = new ContextMenu();
		ContextMenu historyMenu = new ContextMenu();

		// set delete button and action
		MenuItem deleteBookmarkMenuItem = new MenuItem("Delete");
		deleteBookmarkMenuItem.setOnAction(event -> {
			String selectedBookmark = bookmarkListView.getSelectionModel().getSelectedItem();
			if (selectedBookmark != null) {
				bookmarkListView.getItems().remove(selectedBookmark);
				this.saveBookmarks();
			}
		});
		MenuItem deleteHistoryMenuItem = new MenuItem("Delete");
		deleteHistoryMenuItem.setOnAction(event -> {
			String selectedHistory = historyListView.getSelectionModel().getSelectedItem();
			if (selectedHistory != null) {
				historyListView.getItems().remove(selectedHistory);
				this.saveHistory();
			}
		});

		// set open button and action
		MenuItem openBookmarkMenuItem = new MenuItem("Open");
		openBookmarkMenuItem.setOnAction(event -> {
			String selectedBookmark = bookmarkListView.getSelectionModel().getSelectedItem();
			if (selectedBookmark != null) {
				if (currentWV != null) {
					currentWV.getEngine().load(selectedBookmark);
				} else {
					newWebTab.fire();
					currentWV.getEngine().load(selectedBookmark);
				}
			}
		});
		MenuItem openHistoryMenuItem = new MenuItem("Open");
		openHistoryMenuItem.setOnAction(event -> {
			String selectedHistory = historyListView.getSelectionModel().getSelectedItem();
			if (selectedHistory != null) {
				if (currentWV != null) {
					currentWV.getEngine().load(selectedHistory);
				} else {
					newWebTab.fire();
					currentWV.getEngine().load(selectedHistory);
				}
			}
		});

		// set open in new tab button and action
		MenuItem openInNewTabBookmarkMenuItem = new MenuItem("Open in New Tab");
		openInNewTabBookmarkMenuItem.setOnAction(event -> {
			String selectedBookmark = bookmarkListView.getSelectionModel().getSelectedItem();
			if (selectedBookmark != null) {
				newWebTab.fire();
				currentWV.getEngine().load(selectedBookmark);
			}
		});
		MenuItem openInNewTabHistoryMenuItem = new MenuItem("Open in New Tab");
		openInNewTabHistoryMenuItem.setOnAction(event -> {
			String selectedHistory = historyListView.getSelectionModel().getSelectedItem();
			if (selectedHistory != null) {
				newWebTab.fire();
				currentWV.getEngine().load(selectedHistory);
			}
		});

		bookmarkMenu.getItems().addAll(deleteBookmarkMenuItem, openBookmarkMenuItem, openInNewTabBookmarkMenuItem);
		historyMenu.getItems().addAll(deleteHistoryMenuItem, openHistoryMenuItem, openInNewTabHistoryMenuItem);
		// Set a mouse click event handler
		bookmarkListView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				bookmarkMenu.show(bookmarkListView, event.getScreenX(), event.getScreenY());
			}
		});

		historyListView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				historyMenu.show(historyListView, event.getScreenX(), event.getScreenY());
			}
		});

	}

	// Add WebView Engine state change event handler
	protected void setEngineListener(WebView wv) {
		wv.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			WebTab tab = tabsAndWebs.get(wv);
			if ((newValue == Worker.State.SUCCEEDED) && (tab != null)) {
				System.out.println(wv.getEngine().getLocation());
				tab.changeText(wv.getEngine().getLocation());
				tab.setWebView(wv);
				urlTextField.setText(wv.getEngine().getLocation());
				VBox.setVgrow(wv, Priority.ALWAYS);
				if (!historyListView.getItems().isEmpty()) {
					if (!historyListView.getItems().get(historyListView.getItems().size() - 1)
							.equals(wv.getEngine().getLocation())) {
						historyListView.getItems().add(wv.getEngine().getLocation());
					}
				} else {
					historyListView.getItems().add(wv.getEngine().getLocation());
				}
			} else if ((newValue == Worker.State.RUNNING) && (tab != null)) {
				System.out.println("Loading...");
				tab.changeText("Loading...");
			}
		});

		wv.setOnKeyPressed(event -> {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.I) {
					currentZoom *= 1.1;
					wv.setZoom(currentZoom);
				} else if (event.getCode() == KeyCode.O) {
					currentZoom /= 1.1;
					wv.setZoom(currentZoom);
				}
			}
		});
	}

	/**
	 * Set URL text field and buttons to load webpages. Set a default webview.
	 */
	private void setUIControls() {

		// set go back and forward buttons
		backButton = new Button("<");
		backButton.setTooltip(new Tooltip("Go back"));
		backButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + setting.buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;" + "-fx-text-fill: "
				+ setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");

		forwardButton = new Button(">");
		forwardButton.setTooltip(new Tooltip("Go forward"));
		forwardButton.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + setting.buttonBorderAndTextColor
				+ "; " + "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;"
				+ "-fx-text-fill: " + setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";"
				+ "-fx-opacity: 0.7; ");

		// set lauch button
		urlLaunch = new Button("🔍︎");
		urlLaunch.setTooltip(new Tooltip("Launch"));
		urlLaunch.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + setting.buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;" + "-fx-text-fill: "
				+ setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");

		// set reload button
		reload = new Button("↻");
		reload.setTooltip(new Tooltip("Reload"));
		reload.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + setting.buttonBorderAndTextColor + "; "
				+ "-fx-border-radius: 4px;" + "-fx-padding: 2px 6px;" + "-fx-font-size: 17px;" + "-fx-text-fill: "
				+ setting.buttonBorderAndTextColor + ";" + "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");

		// set URL text field
		urlTextField = new TextField("");
		urlTextField.setStyle("-fx-font-size: 14px;" + "-fx-text-fill: " + setting.buttonBorderAndTextColor + ";"
				+ "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");

		// set url searcher pane
		urlSearcherPane = new HBox();
		urlSearcherPane.getChildren().addAll(backButton, forwardButton, urlTextField, urlLaunch, reload);

		// set default WebView
		WebView deWeb = new WebView();
		deWeb.getEngine().load(setting.homepage);
		WebTab deTab = new WebTab(this, deWeb);
		tabsAndWebs.put(deWeb, deTab);
		currentWV = deWeb;

		// set add new tab button
		newWebTab = new NewWebTab(this);
		newWebTab.setTooltip(new Tooltip("New Tab"));
		webTabsPane.getChildren().addAll(deTab, newWebTab);

		// set buttons' event handlers
		backButton.setOnAction(action -> {
			if (currentWV != null) {
				if (currentWV.getEngine().getHistory().getCurrentIndex() > 0) {
					currentWV.getEngine().getHistory().go(-1);
				}
			} else {
				System.out.println(" Current WebView is null. Can't access the back page.");
			}
		});

		forwardButton.setOnAction(action -> {
			if (currentWV != null) {
				if (currentWV.getEngine().getHistory()
						.getCurrentIndex() < (currentWV.getEngine().getHistory().getEntries().size() - 1)) {
					currentWV.getEngine().getHistory().go(1);
				}
			} else {
				System.out.println(" Current WebView is null. Can't access the forward page.");
			}

		});

		urlLaunch.setOnAction(action -> {
			String url = urlTextField.getText();

			// Allow flexible user interaction (e.g. typing both “www.google.com” and
			// “http://www.google.com” works)
			if (url.contains("https://")) {
				url.trim();
			} else {
				url = "https://" + url;
				url.trim();
			}

			if (this.validURL(url)) {
				if (currentWV != null) {
					System.out.println(currentWV);
					System.out.println("Switching...");
					System.out.println("Old URL: " + currentWV.getEngine().getLocation());
					mainPane.getChildren().remove(currentWV);
					currentWV.getEngine().load(url);
					urlTextField.setText(currentWV.getEngine().getLocation());
					tabsAndWebs.get(currentWV).changeText(currentWV.getEngine().getLocation());
					mainPane.getChildren().add(currentWV);
					System.out.println("To URL: " + currentWV.getEngine().getLocation());
				} else {
					newWebTab.fire();
					currentWV.getEngine().load(url);
				}
			} else {
				urlTextField.setText("Invalid URL");
			}
		});

		reload.setOnAction(action -> {
			if (currentWV != null) {
				System.out.println(currentWV);
				System.out.println("Reloading...");
				System.out.println("Current URL: " + currentWV.getEngine().getLocation());
				currentWV.getEngine().reload();
			} else {
				System.out.println("Current WebView is null. Can't reload null WebView.");
			}
		});

	}

	protected boolean validURL(String url) {
		try {
			@SuppressWarnings("deprecation")
			URL testURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) testURL.openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return responseCode == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	// Create a menu bar and menus
	private void setMenus() {
		menuBar = new MenuBar();

		Menu berrySurfMenu = new Menu("BerrySurf");
		MenuItem aboutMenuItem = new MenuItem("About BerrySurf");
		MenuItem settingMenuItem = new MenuItem("Preferences");
		MenuItem quitMenuItem = new MenuItem("Quit");
		berrySurfMenu.getItems().addAll(aboutMenuItem, settingMenuItem, quitMenuItem);

		Menu fileMenu = new Menu("File");
		MenuItem saveHTMLMenuItem = new MenuItem("Save as HTML");
		MenuItem printMenuItem = new MenuItem("Print");
		fileMenu.getItems().addAll(saveHTMLMenuItem, printMenuItem);

		Menu historyMenu = new Menu("History");
		MenuItem showHistoryMenuItem = new MenuItem("Show History");
		MenuItem hideHistoryMenuItem = new MenuItem("Hide History");
		MenuItem clearHistoryMenuItem = new MenuItem("Clear History");
		historyMenu.getItems().addAll(showHistoryMenuItem, hideHistoryMenuItem, clearHistoryMenuItem);

		Menu bookmarkMenu = new Menu("Bookmarks");
		MenuItem addBookmarkMenuItem = new MenuItem("Add BookMark");
		MenuItem showBookmarkMenuItem = new MenuItem("Show Bookmarks");
		MenuItem hideBookmarkMenuItem = new MenuItem("Hide Bookmarks");
		bookmarkMenu.getItems().addAll(addBookmarkMenuItem, showBookmarkMenuItem, hideBookmarkMenuItem);

		menuBar.getMenus().addAll(berrySurfMenu, fileMenu, historyMenu, bookmarkMenu);

		aboutMenuItem.setOnAction(action -> {
			VBox aboutRoot = new VBox();
			Image berry = new Image("/application/berryIcon.png");
			ImageView berryView = new ImageView(berry);
			Text berrySurf = new Text("BerrySurf");
			berrySurf.setFont(Font.font("Courier New", 24));
			Text vision = new Text("Version 1.0 (28/9/2023)");
			vision.setFont(Font.font("Courier New", 10));
			Text developer = new Text("Developer @ Chi(Violet) Zhang");
			developer.setFont(Font.font("Courier New", 10));
			berryView.setFitWidth(150);
			berryView.setFitHeight(150);
			aboutRoot.getChildren().addAll(berryView, berrySurf, vision, developer);
			aboutRoot.setAlignment(Pos.CENTER);
			Scene aboutScene = new Scene(aboutRoot, 400, 300);
			aboutStage.setScene(aboutScene);
			aboutStage.show();
		});

		settingMenuItem.setOnAction(action -> {
			settingStage.show();
		});

		quitMenuItem.setOnAction(action -> {
			this.saveHistory();
			this.saveBookmarks();
			this.setting.saveSetting();
			Platform.exit();
		});

		saveHTMLMenuItem.setOnAction(action -> {
			if (currentWV != null) {
				// Execute JavaScript to get HTML source
				String htmlSource = (String) currentWV.getEngine().executeScript("document.documentElement.outerHTML");
				System.out.println("HTML Source:");
				System.out.println(htmlSource);

				// Save HTML source to a file
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
				File file = fileChooser.showSaveDialog(primaryStage);
				if (file != null) {
					try {
						FileWriter fileWriter = new FileWriter(file);
						fileWriter.write(htmlSource);
						fileWriter.close();
						System.out.println("HTML source saved to: " + file.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("No selected website");
			}
		});

		printMenuItem.setOnAction(action -> {
			if (currentWV != null) {
				// Create a PrinterJob
				PrinterJob printerJob = PrinterJob.createPrinterJob();
				if (printerJob != null) {
					// Show the print dialog
					boolean printDialogShown = printerJob.showPrintDialog(primaryStage);
					if (printDialogShown) {
						// Create a Scale object to set the WebView's scale
						Scale scale = new Scale(0.4, 0.4); // Adjust the scale as needed
						currentWV.getTransforms().add(scale);
						// Print the WebView content
						boolean printed = printerJob.printPage(currentWV);
						if (printed) {
							// End the print job
							printerJob.endJob();
							Scale scaleback = new Scale(2.5, 2.5); // Adjust the scale as needed
							// Apply the scale to the WebView's transform
							currentWV.getTransforms().add(scaleback);
						}
					}
				}
			} else {
				System.out.println("No selected website");
			}

		});

		// set tab pane for history and bookmarks
		Tab historyTab = new Tab("History");
		historyTab.setContent(historyListView);
		Tab bookmarkTab = new Tab("Bookmarks");
		bookmarkTab.setContent(bookmarkListView);

		showHistoryMenuItem.setOnAction(action -> {
			historyListView.setPrefWidth(300);
			System.out.println(historyListView.toString());
			System.out.println("Displaying history...");
			listsTabPane.getTabs().addAll(historyTab);
			if (!sidePane.getChildren().contains(listsTabPane)) {
				sidePane.getChildren().add(listsTabPane);
			}
		});

		hideHistoryMenuItem.setOnAction(action -> {
			System.out.println("Hiding history...");
			listsTabPane.getTabs().remove(historyTab);
			if (listsTabPane.getTabs().isEmpty()) {
				sidePane.getChildren().remove(listsTabPane);
			}
		});

		clearHistoryMenuItem.setOnAction(action -> {
			System.out.println("Clearing history...");
			historyListView.getItems().clear();
			this.saveHistory();
		});

		addBookmarkMenuItem.setOnAction(action -> {
			System.out.println("Adding bookmark...");
			if (currentWV != null) {
				if (!bookmarkListView.getItems().contains(currentWV.getEngine().getLocation())) {
					System.out.println(currentWV.getEngine().getLocation());
					bookmarkListView.getItems().add(currentWV.getEngine().getLocation());
				}
			} else {
				System.out.println("No website is selected...");
			}
		});

		showBookmarkMenuItem.setOnAction(action -> {
			bookmarkListView.setPrefWidth(300);
			System.out.println(historyListView.toString());
			System.out.println("Displaying bookmarks...");
			listsTabPane.getTabs().addAll(bookmarkTab);
			if (!sidePane.getChildren().contains(listsTabPane)) {
				sidePane.getChildren().add(listsTabPane);
			}

		});

		hideBookmarkMenuItem.setOnAction(action -> {
			System.out.println("Hiding bookmarks...");
			listsTabPane.getTabs().remove(bookmarkTab);
			if (listsTabPane.getTabs().isEmpty()) {
				sidePane.getChildren().remove(listsTabPane);
			}
		});
	}

	private void loadSetting() {
		setting.setMenu();
	}

	private void saveBookmarks() {
		System.out.println("Saving bookmarks...");
		try {
			String bookmarksFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/bookmarks.txt";
			PrintStream bookmarksPS = new PrintStream(new File(bookmarksFileName));
			for (String s : bookmarkListView.getItems()) {
				System.out.println(s);
				bookmarksPS.println(s);
			}
			bookmarksPS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveHistory() {
		System.out.println("Saving history...");
		try {
			String historyFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/history.txt";
			PrintStream historyPS = new PrintStream(new File(historyFileName));
			for (String s : historyListView.getItems()) {
				System.out.println(s);
				historyPS.println(s);
			}
			historyPS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadHistoryAndBookmarks() {
		System.out.println("Loading history...");
		try {
			String historyFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/history.txt";
			Scanner historyScan = new Scanner(new File(historyFileName));
			while (historyScan.hasNext()) {
				String url = historyScan.nextLine();
				System.out.println(url);
				historyListView.getItems().add(url);
			}
			historyScan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Loading bookmarks...");
		try {
			String bookmarksFileName = "/Users/zhangchi/eclipse-workspace/WebBrowser/src/application/bookmarks.txt";
			Scanner bookmarksScan = new Scanner(new File(bookmarksFileName));
			while (bookmarksScan.hasNext()) {
				String url = bookmarksScan.nextLine();
				System.out.println(url);
				bookmarkListView.getItems().add(url);
			}
			bookmarksScan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
