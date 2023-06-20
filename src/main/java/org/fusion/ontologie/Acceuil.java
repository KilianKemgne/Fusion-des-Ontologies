package org.fusion.ontologie;

import lattice.util.exception.BadInputDataException;
import lattice.util.relation.RelationalContextFamily;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.fusion.ontologie.OntoDesigner.OntoDesigner;
import org.fusion.ontologie.RCAEngine.RCAEngine;
import org.fusion.ontologie.RFCModeler.AlignRFCModeler.AlignRfcModeler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Acceuil extends JFrame implements ActionListener {

    JButton jButton;
    JTextField jTextField;

    JButton jButton1;
    JTextField jTextField1;
    JButton jButton2;
    Acceuil() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Merge Ontology");
        this.setSize(350, 300);


        JPanel jPanel = new JPanel();
        this.add(jPanel);

        JLabel jLabel = new JLabel("Choose which ontologies to merge");
        jPanel.add(jLabel);

        jTextField = new JTextField(20);
        jTextField.setBounds(10,80,100,40);
        jTextField.setFocusable(false);
        jPanel.add(jTextField);

        jButton = new JButton("Select File");
        jButton.addActionListener(this);
        jButton.setBounds(100,80,100,25);
        jPanel.add(jButton);

        jTextField1 = new JTextField(20);
        jTextField1.setBounds(10,200,100,40);
        jTextField1.setFocusable(false);
        jPanel.add(jTextField1);

        jButton1 = new JButton("Select File");
        jButton1.addActionListener(this);
        jButton1.setBounds(100,200,100,25);
        jPanel.add(jButton1);

        jButton2 = new JButton("Merge Ontologies");
        jButton2.addActionListener(this);
        jPanel.add(jButton2);

        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource()==jButton) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File("C:\\Users\\Kilian\\Desktop"));

            int response = jFileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                jTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
                System.out.println(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getSource()==jButton1) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File("C:\\Users\\Kilian\\Desktop"));

            int response = jFileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                jTextField1.setText(jFileChooser.getSelectedFile().getAbsolutePath());
                System.out.println(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getSource() == jButton2) {
            this.setVisible(false);
            OntModel onto = ModelFactory.createOntologyModel();
            onto.read(jTextField.getText());

            OntModel onto2 = ModelFactory.createOntologyModel();
            onto2.read(jTextField1.getText());
            AlignRfcModeler alignRfcModeler = new AlignRfcModeler();

            RelationalContextFamily rcf = null;
            try {
                rcf = alignRfcModeler.modelRFC(onto, onto2);
            } catch (BadInputDataException | IOException ex) {
                throw new RuntimeException(ex);
            }

            RCAEngine rcaEngine = new RCAEngine(rcf);
            rcaEngine.computeFTR();

            OntoDesigner ontoDesigner = new OntoDesigner(rcf);
            try {
                File ontModel = ontoDesigner.generateOntModel();
                Desktop.getDesktop().open(ontModel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
