package csfm.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;

public class AppController {

    @FXML
    private Label fileway;
    @FXML
    private Label logway;
    @FXML
    private Label appway;
    @FXML
    private CheckBox validate;
    @FXML
    private CheckBox s_conditions;
    @FXML
    private CheckBox s_states;
    @FXML
    private CheckBox generate_csv;
    @FXML
    private TextField cases;
    @FXML
    private TextField events;
    @FXML
    private TextArea text;

    @FXML
    protected void initialize()
    {
        LoadConfig();
    }

    public void LoadConfig()
    {
        try {
            var f = new File(Paths.get("").toAbsolutePath().toString() + File.separator + "base.cfg");
            if (!f.exists()) {
                f.createNewFile();
                var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
                writer.write(File.separator + "\n");
                writer.write(File.separator + "\n");
                writer.write(File.separator + "\n");
                writer.close();
            }
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Paths.get("").toAbsolutePath().toString() + File.separator + "base.cfg"))));
            appway.setText(reader.readLine());
            logway.setText(reader.readLine());
            fileway.setText(reader.readLine());
            reader.close();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void SaveConfig()
    {

    }

    @FXML
    protected void onRunButtonClick() {
        try{
            LoadConfig();
            String comand = "java -jar " + appway.getText();
            if (generate_csv.isSelected())
                comand += " -csv -d " + logway.getText();
            comand += " -f "+ fileway.getText();

            if (!validate.isSelected()) comand += " -nv";
            if (s_conditions.isSelected()) comand += " -sc";
            if (s_states.isSelected()) comand += " -ss";

            try {
                var cases_int = Integer.parseInt(cases.getText());
                if (cases_int > 0)
                    comand += " -c " + cases_int;
            }catch (Exception ex)
            {
                //no cases
            }
            try
            {
                var stages_int = Integer.parseInt(events.getText());
                comand += " -elim " + stages_int;
            }catch (Exception ex)
            {
                //no events
            }

            var p = Runtime.getRuntime().exec(comand);
            var p_out = new BufferedReader(new InputStreamReader( p.getInputStream() ));
            var line = "";
            var out = comand + "\n";
            do{
                System.out.println(line);
                out += line + "\n";
                line = p_out.readLine();
            }while (line != null);
            text.setText(out);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}