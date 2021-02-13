import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class JSONgui
    implements ActionListener {
    
    private final ScriptEngineManager manager = new ScriptEngineManager();
    private TextArea editor;
    private Frame frame;
    private final String script_name;
    private final Object config;
    private final String[] help_contents;
    private final Runnable completion;
    
    public JSONgui(String script, Object config, String[] help, Runnable completion) {
        this.script_name = script;
        this.config = config;
        this.help_contents = help;
        this.completion = completion;
    }
    
    public void showFrame() {
        
        editor = new TextArea(create_json(config), 0, 0,
                TextArea.SCROLLBARS_VERTICAL_ONLY);
        editor.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        Menu file = new Menu("File");
        file.add(menu_item("Save"));
        file.add(menu_item("Load"));
        file.addSeparator();
        file.add(menu_item("Exit"));
        
        Menu edit = new Menu("Edit");
        edit.add(menu_item("Cut"));
        edit.add(menu_item("Copy"));
        edit.add(menu_item("Paste"));
        edit.add(menu_item("Select all"));
        edit.addSeparator();
        edit.add(menu_item("Reset (safety net)"));
        
        Menu help = new Menu("Help");
        help.add(menu_item("Help for " + script_name));
        help.add(menu_item("About"));
        
        MenuBar bar = new MenuBar();
        bar.add(file);
        bar.add(edit);
        bar.add(help);
        
        Button done = new Button("Done");
        done.addActionListener(this);
        
        Panel button_pane = new Panel();
        button_pane.add(done);
        
        frame = new Frame("Configure " + script_name);
        frame.setMenuBar(bar);
        frame.addWindowListener(
            new StandardCloseHandler(frame, StandardCloseHandler.DISPOSE)
        );
        frame.setIconImages(Constants.ICONS);
        
        frame.add(editor, BorderLayout.CENTER);
        frame.add(button_pane, BorderLayout.SOUTH);
        
        frame.setSize(500, 400);
        
        show_window(frame, null);
    }
    
    // JSON.stringify() == broken for java primitives
    // so, this hack is currently required
    private static final String create_json(Object object) {
        StringBuilder b = new StringBuilder("{\n");
        Field[] fields = object.getClass().getFields();
        for (int i = 0; i < fields.length; ++i) {
            Field f = fields[i];
            int mod = f.getModifiers();
            if ((mod & Modifier.FINAL) != 0 || (mod & Modifier.PUBLIC) == 0) {
                continue;
            }
            
            String property_name = f.getName();
            Class<?> c = f.getType();
            
            b.append("\t\"");
            b.append(property_name);
            b.append("\": ");
            
            try {
                if (Object[].class.isAssignableFrom(c)) {
                    b.append(Arrays.deepToString((Object[]) f.get(object)));
                } else if (c.equals(int[].class)) {
                    b.append(Arrays.toString((int[]) f.get(object)));
                } else if (c.equals(boolean[].class)) {
                    b.append(Arrays.toString((boolean[]) f.get(object)));
                } else if (c.equals(long[].class)) {
                    b.append(Arrays.toString((long[]) f.get(object)));
                } else if (c.equals(char[].class)) {
                    b.append(Arrays.toString((char[]) f.get(object)));
                } else if (c.equals(byte[].class)) {
                    b.append(Arrays.toString((byte[]) f.get(object)));
                } else if (c.equals(short[].class)) {
                    b.append(Arrays.toString((short[]) f.get(object)));
                } else if (c.equals(float[].class)) {
                    b.append(Arrays.toString((float[]) f.get(object)));
                } else if (c.equals(double[].class)) {
                    b.append(Arrays.toString((double[]) f.get(object)));
                } else if (CharSequence.class.isAssignableFrom(c)) {
                    b.append('"');
                    b.append(f.get(object).toString());
                    b.append('"');
                } else {
                    b.append(f.get(object).toString());
                }
            } catch (Throwable t) {
                b.append("ERROR");
            }
            if (i != (fields.length - 1)) {
                b.append(",\n");
            } else {
                b.append("\n");
            }
        }
        b.append("}");
        return b.toString();
    }

    private MenuItem menu_item(String str) {
        MenuItem item = new MenuItem(str);
        item.addActionListener(this);
        return item;
    }
    
    private static void show_window(Window w, Window parent) {
        w.setLocationRelativeTo(parent);
        w.toFront();
        w.requestFocus();
        w.setVisible(true);
    }
    
    private static void set_clipbard(String str) {
        try {
            StringSelection sel = new StringSelection(str);
            Toolkit.getDefaultToolkit()
                .getSystemClipboard().setContents(sel, null);
        } catch (Throwable t) {
            System.out.println("Couldn't set clipboard contents: " + t);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (cmd.equals("Done")) {
            try {
                ScriptEngine engine = manager.getEngineByExtension("js");
                engine.put("text", editor.getText());
                engine.put("config", config);
                engine.eval("var o = JSON.parse(text);"
                        + "\nfor (var p in o) {"
                        + "\n   config[p] = o[p];"
                        + "\n}");
                frame.dispose();
                if (completion != null) {
                    completion.run();
                }
            } catch (Throwable t) {
                System.out.println("Error processing config: " + t);
                t.printStackTrace();
            }
            
        } else if (cmd.equals("Save")) {
            FileDialog d = new FileDialog(frame, "Save configuration", FileDialog.SAVE);
            d.setFile(script_name + ".json");
            d.setIconImages(Constants.ICONS);
            show_window(d, frame);
            String dir = d.getDirectory();
            String file = d.getFile();
            if (dir != null && file != null) {
                try {
                    Files.write(Paths.get(dir + file),
                            editor.getText().getBytes(Constants.UTF_8));
                } catch (Throwable t) {
                    System.out.println("Write error: " + t);
                }
            }
            
        } else if (cmd.equals("Load")) {
            FileDialog d = new FileDialog(frame, "Load configuration", FileDialog.LOAD);
            d.setFile(script_name + ".json");
            d.setIconImages(Constants.ICONS);
            show_window(d, frame);
            String dir = d.getDirectory();
            String file = d.getFile();
            if (dir != null && file != null) {
                try {
                    editor.setText(new String(
                            Files.readAllBytes(Paths.get(dir + file)),
                            Constants.UTF_8));
                } catch (Throwable t) {
                    System.out.println("Read error: " + t);
                }
            }
            
        } else if (cmd.equals("Cut")) {
            String str = editor.getText();
            String start = str.substring(0, editor.getSelectionStart());
            String end = str.substring(editor.getSelectionEnd(), str.length());
            set_clipbard(editor.getSelectedText());
            editor.setText(start + end);
            
        } else if (cmd.equals("Copy")) {
            set_clipbard(editor.getSelectedText());
            
        } else if (cmd.equals("Paste")) {
            try {
                editor.append((String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getContents(editor)
                        .getTransferData(DataFlavor.stringFlavor));
            } catch (Throwable t) {
                System.out.println("Couldn't paste: " + t);
            }
            
        } else if (cmd.equals("Select all")) {
            String str = editor.getText();
            editor.select(0, str.length());
            
        } else if (cmd.startsWith("Reset")) {
            editor.setText(create_json(config));
            
        } else if (cmd.startsWith("Help")) {
            StringBuilder b = new StringBuilder();
            if (help_contents != null) {
                for (String str : help_contents) {
                    b.append(str);
                    b.append('\n');
                }
            }
            
            TextArea t = new TextArea(b.toString(), 0, 0,
                    TextArea.SCROLLBARS_VERTICAL_ONLY);
            t.setEditable(false);
            
            final Dialog d = new Dialog(frame, "Script configuration help");
            d.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    d.dispose();
                }
            });
            d.setIconImages(Constants.ICONS);
            d.add(t, BorderLayout.CENTER);
            d.setSize(500, 400);
            show_window(d, frame);
            
        } else if (cmd.equals("About")) {
            System.out.println("JSON script config editor by S");
            
        } else if (cmd.equals("Exit")) {
            frame.dispose();
            
        } else {
            System.out.println("unrecognized: " + cmd);
        }
    }
}
