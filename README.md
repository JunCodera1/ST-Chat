# ST-Chat
### Please add JavaFx modules version SDK 21.0.7
https://gluonhq.com/products/javafx/

### If you using IntelliJ please go to Run -> Edit Configurations -> Modify Options -> Add VM Options and add this line
```text
--module-path path/to/your-sdk-jfx --add-modules javafx.controls,javafx.fxml
```

### If you using NixOS please open console and type this command for run app:
```shell
    nix-shell #to compile shell.nix
    mvn clean javafx:run #run with maven: because i still don't think how to run in IntelliJ NixOS
```
