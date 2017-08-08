/*
 *     Copyright (C) 2017 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.boomboompower.skinchanger.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

public class RunGui extends JComponent {

    private static final Font serverGuiFont = new Font("Monospaced", Font.PLAIN, 12);
    private static final Logger LOGGER = LogManager.getLogger();

    public static void createMenu() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception var3) {
        }

        RunGui thing = new RunGui();
        JFrame jframe = new JFrame("String to UUID");
        jframe.add(thing);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent p_windowClosing_1_) {
                System.exit(0);
            }
        });
    }

    public RunGui() {
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.getMessageComponent(), "Center");
        } catch (Exception exception) {
            LOGGER.error("Couldn\'t build Menu", exception);
        }
    }

    private JComponent getMessageComponent() throws Exception {
        JPanel jpanel = new JPanel(new BorderLayout());
        final JTextArea jtextarea = new JTextArea();
        final JScrollPane jscrollpane = new JScrollPane(jtextarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jtextarea.setEditable(false);
        jtextarea.setFont(serverGuiFont);
        final JTextField jtextfield = new JTextField();
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Outputs"));
        jtextfield.addActionListener(p_actionPerformed_1_ -> {
            String s = jtextfield.getText().trim();

            if (s.length() > 0) {
                String uuid = UUID.nameUUIDFromBytes(s.getBytes()).toString();

                a(jtextarea, jscrollpane, s + " | " + uuid + "\n");
            }
            jtextfield.setText("");
        });
        return jpanel;
    }

    public void a(final JTextArea area, final JScrollPane pane, final String s) {
        Document document = area.getDocument();
        JScrollBar jscrollbar = pane.getVerticalScrollBar();
        boolean flag = false;

        if (pane.getViewport().getView() == area) {
            flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(serverGuiFont.getSize() * 4) > (double)jscrollbar.getMaximum();
        }

        try {
            document.insertString(document.getLength(), s, null);
        } catch (BadLocationException var8) {
        }

        if (flag) {
            jscrollbar.setValue(Integer.MAX_VALUE);
        }
    }
}