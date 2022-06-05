package csfm.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleAction;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;


class AppControllerTest {

    private static Thread thread;
    private static volatile boolean success;

    private void Wait(){Wait(3000);}
    private void Wait(int seconds)
    {
        try {Thread.sleep(seconds);}
        catch(InterruptedException ex) {}

    }

    private ObservableList<Node> GetControl()
    {
        return GuiMain.primaryStage.getScene().getRoot().getChildrenUnmodifiable();
    }

    private LinkedList<String> GetCSV(File file)
    {
        var list = new LinkedList<String>();
        try {
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            var line = reader.readLine();
            while(line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e) {
            Assertions.fail();
        }
        return list;
    }

    private void CheckCSV(File file, String[] file_out)
    {
        if(!file.exists())
            Assertions.fail();
        var list = new LinkedList<String>();
        try {
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            var line = reader.readLine();
            while(line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e) {
            Assertions.fail();
        }
        for(int i = list.size()-1; i >= list.size()-4; i--)
            Assertions.assertEquals(file_out[i-list.size()+4], list.get(i).substring(0,file_out[i-list.size()+4].length()));
    }

    @BeforeAll
    @AfterAll
    public static void ConfigSetup()
    {
        try {
            var f = new File(Paths.get("").toAbsolutePath().toString() + File.separator + "base.cfg");
            if (!f.exists())
                f.createNewFile();
            var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
            writer.write(Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-dist/build/libs/cfsm-1.2.0.jar" + "\n");
            writer.write(Paths.get("").toAbsolutePath().getParent().toString() + "/log.csv" + "\n");
            writer.write(Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm2.json" + "\n");
            writer.close();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @BeforeAll
    public static void Prepare() {
        success = false;
        thread = new Thread(() -> {
            try {
                Application.launch(GuiMain.class);
                success = true;
            } catch(Throwable t) {
                if(t.getCause() != null && t.getCause().getClass().equals(InterruptedException.class)) {
                    success = true;
                    return;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @AfterEach
    public void Depare()
    {
        Platform.runLater(()-> {
                    try {
                        GuiMain.primaryStage.close();
                        Wait();
                        GuiMain.primaryStage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(new File(Paths.get("").toAbsolutePath().toString() + File.separator + "main-view.fxml").toURI().toURL());
                        Scene scene = new Scene(fxmlLoader.load(),1024,640);
                        GuiMain.primaryStage.setTitle("CSFM-Gui-1.2.0");
                        GuiMain.primaryStage.setScene(scene);
                        GuiMain.primaryStage.show();
                    }catch (Exception ex)
                    {}
                });
        Wait();
    }

    @AfterAll
    public static void ThreadCheck()
    {
        thread.interrupt();
        try { thread.join(100);}
        catch (InterruptedException ex){}

        Assertions.assertTrue(success);
    }

    @Test
    void onRunButtonClick() {
        String basic_out = "java -jar " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-dist/build/libs/cfsm-1.2.0.jar" + " -f " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm2.json" + " -nv\n" +
                "\n" +
                "Specified path to file is: " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm2.json" + "\n" +
                "Parsing configuration file......\n" +
                "Valid JSON: OK\n" +
                "Valid syntax: OK\n" +
                "3 machines found\n" +
                "A: state1 ---> state2\n" +
                "B: state1 ---> state2\n" +
                "B: state2 ---> state3\n" +
                "C: state1 ---> state2\n";
        Wait();
        var controls = GetControl();

        controls.get(12).executeAccessibleAction(AccessibleAction.FIRE);
        Wait();

        Assertions.assertEquals(basic_out, ((TextArea) controls.get(9)).getText());
    }

    @Test
    void onRunButtonClick22() {
        var out_case_2_event_2 = "java -jar " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-dist/build/libs/cfsm-1.2.0.jar" + " -f " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm2.json" + " -nv -c 2 -elim 2\n" +
                "\n" +
                "Specified path to file is: " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm2.json" + "\n" +
                "Parsing configuration file......\n" +
                "Valid JSON: OK\n" +
                "Valid syntax: OK\n" +
                "3 machines found\n" +
                "A: state1 ---> state2\n" +
                "A: state1 ---> state2\n" +
                "B: state1 ---> state2\n" +
                "B: state1 ---> state2\n";
        Wait();
        var controls = GetControl();

        ((TextField)((HBox)controls.get(7)).getChildrenUnmodifiable().get(1)).setText("2");
        ((TextField)((HBox)controls.get(8)).getChildrenUnmodifiable().get(1)).setText("2");
        controls.get(12).executeAccessibleAction(AccessibleAction.FIRE);

        Wait();

        Assertions.assertEquals(out_case_2_event_2, ((TextArea) controls.get(9)).getText());
    }

    @Test
    void CsvTestShowSC() {
        var file_out = new String[]{ "1;1;A-transition1-state1->state2-B ! msg1;","1;2;B-transition2-state1->state2-A ? msg1;","1;3;B-transition3-state2->state3-C ! msg1;","1;4;C-transition4-state1->state2-B ? msg1;"};

        Wait();
        var controls = GetControl();

        controls.get(3).executeAccessibleAction(AccessibleAction.FIRE);
        controls.get(5).executeAccessibleAction(AccessibleAction.FIRE);
        controls.get(6).executeAccessibleAction(AccessibleAction.FIRE);
        controls.get(12).executeAccessibleAction(AccessibleAction.FIRE);
        Wait();

        CheckCSV(new File (((Label)((HBox)controls.get(1)).getChildrenUnmodifiable().get(1)).getText()), file_out);
    }

    @Test
    void ValidateCfgTest(){
        var out_config_val = "java -jar " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-dist/build/libs/cfsm-1.2.0.jar" + " -f " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm_same_name_transition.json\n" +
                "\n" +
                "Specified path to file is: " + Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm_same_name_transition.json\n" +
                "Parsing configuration file......\n" +
                "Valid JSON: OK\n" +
                "Valid syntax: OK\n" +
                "Valid config objects: OK\n" +
                "2 machines found\n" +
                "A: state1 ---> state2\n" +
                "B: state1 ---> state2\n";

        Wait();
        var controls = GetControl();
        try{((Label)((HBox)controls.get(2)).getChildrenUnmodifiable().get(1)).setText(Paths.get("").toAbsolutePath().getParent().toString() + "/CFSM-engine/src/test/resources/cfsm_same_name_transition.json");}
        catch (IllegalStateException ignored){}

        controls.get(4).executeAccessibleAction(AccessibleAction.FIRE);
        controls.get(12).executeAccessibleAction(AccessibleAction.FIRE);
        Wait();

        Assertions.assertEquals(out_config_val, ((TextArea) controls.get(9)).getText());
    }

    @Test
    void CsvTest() {
        var file_out = new String[]{ "1;1;A-transition1;","1;2;B-transition2;","1;3;B-transition3;","1;4;C-transition4;"};

        Wait();
        var controls = GetControl();

        controls.get(3).executeAccessibleAction(AccessibleAction.FIRE);
        controls.get(12).executeAccessibleAction(AccessibleAction.FIRE);
        Wait();

        CheckCSV(new File (((Label)((HBox)controls.get(1)).getChildrenUnmodifiable().get(1)).getText()), file_out);
    }
}
