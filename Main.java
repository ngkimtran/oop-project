package project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application{
	
	//GUI elements
	//for input
	private final ComboBox<String> categoryCmb = new ComboBox<>(); 
	private final ArrayList<String> category = new ArrayList<>();
	private final TextField sumField = new TextField();
	private final TextField descrField = new TextField();
	private final TextField dateField = new TextField();
	
	//for search
	private final ComboBox searchType = new ComboBox<>();
	private final TextField searchDate = new TextField();
	private final ComboBox<String> searchCategory = new ComboBox<>();
	
	//for date result
	private final TextArea textArea = new TextArea();
	
	//for category result
	private final CategoryAxis xAxis = new CategoryAxis();
	private final NumberAxis yAxis = new NumberAxis();
	private final BarChart<String, Number> barchart = new BarChart<>(xAxis,yAxis);
	
	//other
	private final FileChooser fileChooser = new FileChooser();
	private final Label status = new Label();
	
	//Category file contains basic categories for user to choose from
	//User can add new category by editing the text file
	private final String cfile = "project/category.txt";
	
	//ArrayList for expenses
	private ArrayList<Expenses> obj = new ArrayList<>();

	//Read categories from text file and add it to ComboBox
	public void readCategory(ComboBox cBox) throws Exception{
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource(cfile); 

		try(InputStream in = url.openStream(); BufferedReader input = new BufferedReader(new InputStreamReader(in))){
			String line;
			while((line = input.readLine()) != null) {
				category.add(line);
			}
			List<String> names = category.stream().sorted().collect(Collectors.toList());
			cBox.setItems(FXCollections.observableArrayList(names));
			category.clear();
		}
	}
	
	//Create new expense date and add it to the list
	public void createNewExpense(ActionEvent e) {
		//Catch errors
		if(dateField.getText().length() == 0) {
			Alert a = new Alert(AlertType.ERROR, "Check your input!");
			dateField.requestFocus();
			dateField.selectAll();
            a.showAndWait();
            return;
		}
		else {
			//check for existing date
			for (int i=0; i<obj.size();i++) {
				if (obj.get(i).getDate().isSame(new Date(dateField.getText()))) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Error");
					alert.setHeaderText("Date already exists!");
					alert.showAndWait();
					return;
				}
			}
			
			obj.add(new Expenses(new Date(dateField.getText())));
			status.setText("Created new expense");
		}
	}
	
	//Add new expense to current expense date
	public void addExpense(ActionEvent e) {
		Date d;
		try {
			d = new Date(dateField.getText());
		} catch(Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Date doesn't exist.");
			alert.showAndWait();
			return;
		}
		if(descrField.getText() == null) {
			Alert a = new Alert(AlertType.ERROR, "Check your input description!");
			descrField.requestFocus();
            a.showAndWait();
            return;
		}
		if (categoryCmb.getValue() == null) {
			Alert a = new Alert(AlertType.ERROR, "Choose a category!");
			categoryCmb.requestFocus();
            a.showAndWait();
            return;
		}
		for (Expenses o : obj) {
			
			if (o.getDate().isSame(d)) {
				try {
					o.addExpense(new Expense(categoryCmb.getValue(),Double.parseDouble(sumField.getText()), descrField.getText()));
					status.setText("Added new expense");
				} catch(NumberFormatException ex) {
					Alert a = new Alert(AlertType.ERROR, "Check your input sum!");
					sumField.requestFocus();
					sumField.selectAll();
		            a.showAndWait();
		            return;

				}
			}
		}
		
	}
	
	//Adding different search types for user to choose
		public void addType() {
			ArrayList<String> stype = new ArrayList<>();
			stype.add("By date");
			stype.add("By category");
			searchType.setItems(FXCollections.observableArrayList(stype));
		}
	
	//This method will give the value required for the user to search based on their choice
	public void searchMethod(GridPane grid) {
		Label searchl = new Label();
		grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 11);
		if (searchType.getValue() == "By date") {
			searchl.setText("Search date: ");
			grid.add(searchl, 0, 11);
			grid.add(searchDate, 1, 11);
			searchDate.requestFocus();
			status.setText("Search by date chosen");
		}
		else if (searchType.getValue() == "By category") {
				try {
				readCategory(searchCategory);
			} catch (Exception ex) {						
			}
			searchl.setText("Search category: ");
			grid.add(searchl, 0, 11);
			grid.add(searchCategory, 1, 11);
			searchCategory.requestFocus();
			status.setText("Search by category chosen");
		}
	}
	
	//Search and print expenses
	public void searchExpense(ActionEvent e, BorderPane mainpanel) {
		status.setText("Showing result...");
		if (searchType.getValue() == "By date") {
			methodDate(e, mainpanel);
		}
		else if (searchType.getValue() == "By category") {
			methodCategory(e, mainpanel);
		}
	}
	
	//Search by date method
	public void methodDate(ActionEvent e, BorderPane mainpanel) {
		mainpanel.setCenter(null);
		textArea.setText("");
		try {
			//catch errors
			if(searchDate.getText().length() == 0) {
				Alert a = new Alert(AlertType.ERROR, "Check your search input!");
				searchDate.requestFocus();
	            a.showAndWait();
	            return;
			}
			
			//TextArea center of border panel
			textArea.setEditable(false);
			textArea.setFont(Font.font("Courier New", FontWeight.NORMAL, 17));
			textArea.setPadding(new Insets(5, 5, 5, 5));
			mainpanel.setCenter(textArea);
			
			int flag = 0;
			//search and print
			for (Expenses ex: obj) {
				if (ex.getDate().isSame(new Date(searchDate.getText()))) {
					textArea.setText(ex.toString());
					flag = 1;
				}
			}
			if (flag == 0) {
				textArea.setText("No data available for the date you searched.");
			}
		}
		catch (Exception ex) {}
	}
	
	//Search by category method
	public void methodCategory(ActionEvent e, BorderPane mainpanel) {
		mainpanel.setCenter(null);
		barchart.getData().clear();
		double sum = 0;
		
		try {
			//catch errors
			if(searchCategory.getValue() == null) {
				Alert a = new Alert(AlertType.ERROR, "Choose a category!");
				searchCategory.requestFocus();
	            a.showAndWait();
	            return;
			}
			
			//sort the dates in ascending order
			Collections.sort(obj);
			
	        barchart.setTitle("Expenses by category");
	        barchart.setLegendVisible(false);
	        barchart.setAnimated(false);
	        barchart.setPadding(new Insets(5, 5, 5, 5));
	        xAxis.setLabel("Date");       
	        yAxis.setLabel("Sum");
			
	        XYChart.Series<String, Number> series = new Series<>();
	        
	        ArrayList<Double> result = new ArrayList<>();
	        for (Expenses ex : obj) {
	        	result.add(ex.getSumByCategory(searchCategory.getValue()));
	        	sum += ex.getSumByCategory(searchCategory.getValue());
	        }
	        for (int i = 0; i <obj.size(); i++) {
	        	series.getData().add(new Data<>(obj.get(i).getDate().toString(), result.get(i)));
	        }
	        
			status.setText("Sum: " + String.format("%.2f",sum) + "€");
			
	        barchart.getData().add(series);
	        mainpanel.setCenter(barchart);
		}
		catch (Exception ex) {}
	}
	
	//Save to file
	public void saveToFile(File file) {
		System.out.println(file.getAbsolutePath());
		
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(obj);
			status.setText("Saved suscessfully");
		}
		catch(Exception e) {
			status.setText("Problems with " + file);
			e.printStackTrace();
		}
	}
	
	//Read from file
	public void readFromFile(File file) {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			obj = (ArrayList<Expenses>)in.readObject();
			clearControls();
			status.setText("Loaded suscessfully");
		}
		catch(Exception e) {
			status.setText("Problems with " + file);
			e.printStackTrace();
		}
	}

	
	@Override
	public void start (Stage pStage) {
		try {
			readCategory(categoryCmb);
		} catch (Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Problems with the categories file.");
			alert.setContentText("Check that it's path is correct in the program code.");
			alert.showAndWait();
			return;
		}
		
		try {
			addType();
		} catch (Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.showAndWait();
			return;
		}
		
		//Main panel
		BorderPane mainpanel = new BorderPane();
		
		//Controls for creating and searching expenses
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		mainpanel.setLeft(grid);
		
		//Instructions
		Text instrText = new Text("Enter the date. (dd.mm.yyyy)");
		grid.add(instrText, 0, 0, 2, 1);
		
		//Labels and TextFields
		Label datel = new Label("Date: ");
		grid.add(datel, 0, 1);
		grid.add(dateField, 1, 1);
		
		Button createBtn = new Button("Create expense");
		grid.add(createBtn, 1, 2);
		
		//Controls for adding new expense to expenses list
		Text instrText2 = new Text("Then choose a category, enter the sum and write a short description.");
		grid.add(instrText2, 0, 3, 2, 1);
		Label categoryl = new Label("Category: ");
		grid.add(categoryl, 0, 4);
		grid.add(categoryCmb, 1, 4);
		
		Label suml = new Label("Sum: ");
		grid.add(suml, 0, 5);
		grid.add(sumField, 1, 5);
		
		Label descrl = new Label("Description: ");
		grid.add(descrl, 0, 6);
		grid.add(descrField, 1, 6);
		
		Button addBtn = new Button("Add");
		grid.add(addBtn, 1, 7);
		
		//Controls to show expense
		Text instrText3 = new Text("Enter a date or choose a category to view the result.");
		grid.add(instrText3, 0, 8, 2, 1);
		Label searchl = new Label("Search method: ");
		grid.add(searchl, 0, 9);
		grid.add(searchType, 1, 9);
		
		Button searchBtn = new Button("Search");
		grid.add(searchBtn, 1, 10);
		
		//status label
		mainpanel.setBottom(status);
		
		//Create menu bar and File menu
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		menuBar.getMenus().add(menuFile);
		
		MenuItem startNew = new MenuItem("New");
		MenuItem open = new MenuItem("Open");
		MenuItem saveAs = new MenuItem("Save As");
		MenuItem exit = new MenuItem("Exit");
		menuFile.getItems().addAll(startNew, open, saveAs, new SeparatorMenuItem(), exit);
		mainpanel.setTop(menuBar);
		
		//Initialize the application
		Scene scene = new Scene(mainpanel, 1200,800);
		pStage.setTitle("Expenses");
		pStage.setScene(scene);
		pStage.show();
			
		//Event handlers
		createBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				createNewExpense(e);
			}
		});
		
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				addExpense(e);
			}
		});
		
		searchType.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				searchMethod(grid);			
			}
		});
		
		searchBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				searchExpense(e, mainpanel);
			}
		});

		saveAs.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				File file = fileChooser.showSaveDialog(pStage);
				if (file != null) {
					saveToFile(file);
				}
			}
		});
		
		open.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				File file = fileChooser.showOpenDialog(pStage);
				if (file != null) {
					readFromFile(file);
				}
			}
		});
		
		exit.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent e) {
				Platform.exit();
			}
		});
		
		startNew.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				clearControls();
				obj.clear();
			}
		});
	}
	
	//Clears GUI controls
	private void clearControls() {
		sumField.setText(null);
		descrField.setText(null);
		dateField.setText(null);
		categoryCmb.getSelectionModel().clearSelection();
		searchCategory.getSelectionModel().clearSelection();
		searchDate.setText(null);
		barchart.getData().clear();
		textArea.setText(null);;
	}
		
	//Main method to start the application
	public static void main(String args[]) {
		launch(args);
	}
	
}
