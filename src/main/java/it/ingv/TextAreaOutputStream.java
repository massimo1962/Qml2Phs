/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ingv;

/**
 *
 * @author massimo
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;

    private final StringBuilder sb = new StringBuilder();

    public TextAreaOutputStream(final JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {

        if (b == '\r') {
            return;
        }

        if (b == '\n') {
            final String text = sb.toString() + "\n";

            textArea.append(text);
            sb.setLength(0);
        } else {
            sb.append((char) b);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                JFrame frame = new JFrame(TextAreaOutputStream.class.getSimpleName());
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                JTextArea ta = new JTextArea(24, 80);
//                System.setOut(new PrintStream(new TextAreaOutputStream(ta)));
//                frame.add(new JScrollPane(ta));
//                frame.pack();
//                frame.setVisible(true);
//                System.out.println("Textarea console initiated");
//                Timer t = new Timer(1000, new ActionListener() {
//
//                    int count = 1;
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        System.out.println("Outputting line " + count++ + " to the console. Working properly, no?");
//                    }
//                });
//                t.start();
//            }
//        });
//    }
}
