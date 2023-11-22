# Application to support learning English using Java
## Author
Group Noobies
  1. Nguyễn Quang Anh - 20020627
  2. Trần Thế Dũng - 20020641
  3. Nguyễn Văn Hùng - 20020671
## Description
The application is designed to support learning English. The application is written in Java and uses the JavaFX library. The application is based on the MVC model. The application has two types of dictionaries: English-Vietnamese and Vietnamese-English. The application use E_V.txt and V_E.txt files to store data.
  1. The application is designed to support learning English.
  2. The application is written in Java and uses the JavaFX library.
  3. The application is based on the MVC model.
  4. The application has two types of dictionaries: English-Vietnamese and Vietnamese-English.
  5. The application use E_V.txt and V_E.txt files to store data and word search features.
  6. The application use firebase to store data user.
  7. Game-intergrated application to practice English and study time statistics.
  8. Use Google' Api to translate the entire paragraph.
  9. Use Google' Api to pronounce each word. 
## UML diagram
![UML diagram](https://github.com/quanhspdz/Dictionary-OOP/assets/81352730/a15a785b-8c04-468f-ad78-c414ef51263a)

## Installation
  1. Clone the project from the repository by command: git clone https://github.com/quanhspdz/Dictionary-OOP.git
  2. Open the project in the IDE.
  3. Instruction for build app:
    - Project Stucture -> Modules -> DictionaryApplication:
        + Source Folders: src\main\java
        + Resource Folders: src\main\resources
    - Run -> Edit Configurations... -> Add new run configuration... -> Application
        + Name: DictionaryApp
        + MainClass: App.DictionaryApp
- Modify options --> Add VM options
- VM options: --module-path "\path\to\openjfx-21.0.1_windows-x64_bin-sdk\javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.web --add-opens java.base/java.time=ALL-UNNAMED
4. If you want to change the data, you can change the E_V.txt and V_E.txt files.
## Usage
  1. Select mode: English-Vietnamese or Vietnamese-English to choose the dictionary.
  2. Search for a word in the dictionary and click the Search button, then the right side of the window will display the meaning of the word.
  3. To add a new word, click the Add button (Plus icon).
  4. To delete a word, click the Delete button (Minus icon).
  5. To edit a word, click the Edit button (Pencil icon).
  6. To save the changes, click the Save button (Disk icon).
  7. To pronounce the word, click the Pronounce button (Speaker icon).
  8. To practice, click the Practice button (Readbook icon), then the application will display a Game window.
  9. To exit the application, click the Exit button (Cross icon).
## Demo
![Demo](
## Future improvements
  1. Add more dictionaries.
  2. Add more complex games.
  3. Optimize the word lookup algorithm.
  4. Integrate the application with API of Google Speech to Text to convert speech to text.
  5. Improve the user interface.
## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
## Project status
The project is completed.
## Notes
The application is written for educational purposes.
