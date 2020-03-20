package com.molot.lowlevel.rw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.EmptyBorder;

import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataListener;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataProcessor;
import com.molot.lowlevel.rw.enums.ConfigirationFreq;
//import ua.com.integer.ui.com.molot.util.FrameTools;
import com.molot.util.Params;

/**
 * Created by integer on 3/12/20.
 */

public class SetRecFreq extends JDialog implements ByteDataListener {

    private static final long serialVersionUID = 7072133681727767472L;
    private JComboBox<ConfigirationFreq> freqConfComboBox;

    private int currentByteIndex;
    private int[] receivedData = new int[10];
    private byte[] rawReceivedData = new byte[10];
    private UpdateThread updateThread;
    private ReaderWriter readerWriter;
    private JFormattedTextField rxFreq;
    private ByteDataProcessor byteDataProcessor;

    class UpdateThread extends Thread {
        private boolean needStop;

        public void run() {
            while(true) {
                if (needStop) {
                    return;
                }

                if (readerWriter != null && byteDataProcessor != null && readerWriter.getReader().canRead()) {
                    while(readerWriter.getReader().canRead()) {
                        boolean nextBit = readerWriter.getReader().readBit();
                        byteDataProcessor.addBit(nextBit);
                    }
                }

                try { Thread.sleep(500);} catch (InterruptedException e) { e.printStackTrace();}
            }
        }

        public void stopThisThread() {
            needStop = true;
        }
    };


    private final JPanel contentPanel = new JPanel();



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SetRecFreq dialog = new SetRecFreq();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SetRecFreq() {
        setTitle("Налаштування ПЧ ПРМ");

        addWindowListener(new WindowClosingListener());

        setBounds(100, 100, 450, 200);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblIp = new JLabel("ПЧ ПРМ:");

        rxFreq = new JFormattedTextField();
        rxFreq.setColumns(12);



        //JLabel label_1 = new JLabel("Порт:");

        //portText = new JFormattedTextField();

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblIp)
                                        )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(rxFreq, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                        )
                                .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblIp)
                                        .addComponent(rxFreq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                
                        )
        );
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBorder(new EmptyBorder(0, 5, 5, 15));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

            JButton refreshButton = new JButton("Оновити");
           // refreshButton.addActionListener(new EthernetSettingsDialog.RefreshListener());
            buttonPane.add(refreshButton);

            Component horizontalGlue = Box.createHorizontalGlue();
            buttonPane.add(horizontalGlue);

            JButton saveButton = new JButton("Зберегти");
            saveButton.addActionListener(new SaveListener());
            buttonPane.add(saveButton);
        }

        //FrameTools.situateOnCenter(this);




    }
    class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String freq = rxFreq.getText();
            int fr = Integer.parseInt(freq);
            readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("set-receive-frequency"));
            readerWriter.writeByteAsIs((byte)(fr>>24));
            readerWriter.writeByteAsIs((byte)(fr>>16));
            readerWriter.writeByteAsIs((byte)(fr>>8));
            readerWriter.writeByteAsIs((byte)fr);



            readerWriter.flush();

            Params.SETTINGS.putString("rx_freq",freq);

            Params.SETTINGS.save();


        }
        }

    public void setReaderWriter(final ReaderWriter readerWriter) {
        byteDataProcessor = new ByteDataProcessor(this);
        this.readerWriter = readerWriter;
        readerWriter.workInRawMode(true);

        readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("close-in-out"));

        if(updateThread == null) {
            updateThread = new UpdateThread();
            updateThread.start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }
    class WindowClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (updateThread != null) {
                updateThread.stopThisThread();
                updateThread = null;

                readerWriter.close();
                ReaderWriterFactory.getInstance().close();
            }

            dispose();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            updateTextField();
            super.windowActivated(e);
        }
    }


    public void updateEthernetParams() {
        if (readerWriter == null) {
            return;
        }

//        ipAddress.setText("");
//		portText.setText("");
    //    maskText.setText("");

        byteDataProcessor.reset();
        readerWriter.reset();

        currentByteIndex = 0;
        readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("ethernet-get-params"));
    }

    class RefreshListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateTextField();
        }
    }


    public void updateTextField() {

        String Freq = Params.SETTINGS.getString("rx_freq");
       rxFreq.setValue(Freq);
    }
    @Override
    public void byteReceived(byte b) {

    }
}
